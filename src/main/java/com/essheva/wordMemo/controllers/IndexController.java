package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.Session;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.services.SessionService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.validators.LoginValidator;
import com.essheva.wordMemo.services.validators.RestorePasswordValidator;
import com.essheva.wordMemo.services.validators.SignupValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Slf4j
@Controller
public class IndexController {

    private final UserService userService;
    private final SessionService sessionService;
    private final LoginValidator loginValidator;
    private final SignupValidator signupValidator;

    public IndexController(UserService userService, SessionService sessionService, LoginValidator loginValidator,
                           SignupValidator signupValidator, RestorePasswordValidator restorePasswordValidator) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.loginValidator = loginValidator;
        this.signupValidator = signupValidator;
    }

    @GetMapping({"", "/", "/index"})
    public String getIndexPage(@CookieValue(value = "sessionId", required = false) String sessionId,  Model model) {
        if (sessionId != null) {
            String username = sessionService.getUsernameBySessionId(sessionId);
            model.addAttribute("userLogged", username);
        }
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/")
    public String postIndexLogin(@ModelAttribute("user") User userModel, BindingResult bindingResult, HttpServletResponse response) {
        loginValidator.validate(userModel, bindingResult);
        if (bindingResult.hasErrors()) {
            return "index";
        }

        User user = userService.userLogin(userModel.getUsername(), userModel.getPassword());
        Session session = sessionService.startSession(user);
        Cookie cookie = new Cookie("sessionId", session.getId());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        return "redirect:/index";
    }

    @GetMapping("/singup")
    public String singupView(Model model) {
        model.addAttribute("user", new User());
        return "singup";
    }

    @PostMapping("/singup")
    public String singup(@ModelAttribute("user") @Valid User userModel, BindingResult bindingResult, HttpServletResponse response) {
        signupValidator.validate(userModel, bindingResult);
        if (bindingResult.hasErrors()) {
            return "singup";
        }
        User user = userService.addUser(userModel);
        Session session = sessionService.startSession(user);
        response.addCookie(new Cookie("sessionId", session.getId()));
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = "sessionId", required = false) String sessionId, HttpServletResponse response) {
        if (sessionId != null) {
            sessionService.endSessionById(sessionId);
            Cookie cookie = new Cookie("sessionId", null);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return "redirect:/index";
    }
}
