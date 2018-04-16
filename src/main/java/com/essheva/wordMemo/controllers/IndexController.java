package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.Session;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.UserAlreadyExistsError;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.services.SessionService;
import com.essheva.wordMemo.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
public class IndexController {

    private final UserService userService;
    private final SessionService sessionService;

    public IndexController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
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
    public String postIndexLogin(@ModelAttribute("user") @Valid User userModel, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        User user = userService.userLogin(userModel.getUsername(), userModel.getPassword());
        Session session = sessionService.startSession(user);
        Cookie cookie = new Cookie("sessionId", session.getId());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        return "index";
    }

    @GetMapping("/singup")
    public String singupView(Model model) {
        model.addAttribute("user", new User());
        return "singup";
    }

    @PostMapping("/singup")
    public String singup(@ModelAttribute("user") @Valid User userModel, BindingResult bindingResult, HttpServletResponse response) {
        if (!userModel.getPassword().equals(userModel.getPasswordVerified())) {
            bindingResult.addError(new FieldError("user", "passwordVerified", "Password do not match."));
            return "singup";
        }
        if (bindingResult.hasErrors()) {
            return "singup";
        }
        User user = userService.addUser(userModel);
        Session session = sessionService.startSession(user);
        response.addCookie(new Cookie("sessionId", session.getId()));
        return "redirect:/index";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFound.class)
    public ModelAndView handleUserNotFound(Exception exception, HttpServletRequest request) {
        log.error("Handling user not found exception.");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", exception);
        modelAndView.addObject("originURL", request.getServletPath());

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsError.class)
    public ModelAndView handleUserDuplication(Exception exception, HttpServletRequest request) {
        log.error("Handling user duplication exception.");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("400error");
        modelAndView.addObject("exception", exception);
        modelAndView.addObject("originURL", request.getServletPath());

        return modelAndView;
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
