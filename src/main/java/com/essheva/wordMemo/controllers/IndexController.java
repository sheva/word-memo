package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.Session;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.InvalidCredentialsError;
import com.essheva.wordMemo.exceptions.UserAlreadyExistsError;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.services.SessionService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.validators.LoginValidator;
import com.essheva.wordMemo.services.validators.SigninValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.essheva.wordMemo.domain.Session.SESSION_LIVENESS_SECONDS;
import static java.lang.String.format;


@Slf4j
@Controller
@ComponentScan(basePackages = {"com.essheva.wordMemo.controllers"})
public class IndexController {

    private final UserService userService;
    private final SessionService sessionService;
    private final LoginValidator loginValidator;
    private final SigninValidator signinValidator;

    public IndexController(UserService userService, SessionService sessionService, LoginValidator loginValidator,
                           SigninValidator signinValidator) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.loginValidator = loginValidator;
        this.signinValidator = signinValidator;
    }

    @GetMapping({"", "/", "/index"})
    public String getIndexPage(@CookieValue(value = "sessionId", required = false) String sessionId,  Model model) {
        if (sessionId != null) {
            String username = sessionService.getUsernameBySessionId(sessionId);
            try {
                userService.findUserByUsername(username);
            } catch (UserNotFound e) {
                log.warn("User has been deleted. Perform 'logout'.");
                return "redirect:/logout";
            }
            model.addAttribute("userLogged", username);
        }
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/")
    public String postIndexLogin(@ModelAttribute("user") User userModel, BindingResult bindingResult,
                                 HttpServletResponse response) {
        if (!loginValidator.validate(userModel, bindingResult)) {
            return "index";
        }

        User user;
        try {
            user = userService.passwordMatch(userModel.getUsername(), userModel.getPassword());
        } catch (InvalidCredentialsError e) {
            bindingResult.addError(new FieldError("user", "password", e.getMessage()));
            return "index";
        } catch (UserNotFound e) {
            bindingResult.addError(new FieldError("user", "username", e.getMessage()));
            return "index";
        }

        Session session = sessionService.startSession(user);

        Cookie cookie = new Cookie("sessionId", session.getId());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(SESSION_LIVENESS_SECONDS);
        response.addCookie(cookie);

        log.info(format("User '%s' has been logged in.", user.getUsername()));

        return "redirect:/index";
    }

    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") @Valid User userModel, BindingResult bindingResult,
                         HttpServletResponse response) {
        User user;
        try {
            user = userService.addUser(userModel);
        } catch (UserAlreadyExistsError e) {
            bindingResult.addError(new FieldError("user",
                    e.getProperty() != null ? e.getProperty() : "username", e.getMessage()));
            return "signup";
        }
        if (!signinValidator.validate(userModel, bindingResult)) {
            return "signup";
        }
        Session session = sessionService.startSession(user);
        response.addCookie(new Cookie("sessionId", session.getId()));
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = "sessionId") String sessionId, HttpServletResponse response) {
        sessionService.endSessionById(sessionId);
        Cookie cookie = new Cookie("sessionId", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/index";
    }
}
