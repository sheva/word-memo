package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.InvalidCredentialsError;
import com.essheva.wordMemo.services.SessionService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.validators.ChangePasswordValidator;
import com.essheva.wordMemo.services.validators.UserEmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Slf4j
@Controller
@ComponentScan(basePackages = {"com.essheva.wordMemo.controllers"})
public class AccountSettingsController {

    private static final String viewName = "account";

    private final UserService userService;
    private final SessionService sessionService;
    private final ChangePasswordValidator passwordValidator;
    private final UserEmailValidator emailValidator;

    public AccountSettingsController(UserService userService, SessionService sessionService,
                                     ChangePasswordValidator passwordValidator, UserEmailValidator emailValidator) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.passwordValidator = passwordValidator;
        this.emailValidator = emailValidator;
    }

    @GetMapping("/account")
    public String accountSettings(@CookieValue(value = "sessionId", required = false) String sessionId, Model model) {
        if (sessionId != null) {
            String username = sessionService.getUsernameBySessionId(sessionId);
            User user = userService.findUserByUsername(username);
            model.addAttribute("user", user);

            return viewName;
        } else {
            return "redirect:/index";
        }
    }

    @PostMapping(value = "/account", params = "action=updateEmail")
    public String postAccountUpdate(@ModelAttribute("user") User userModel, BindingResult bindingResult, Model model) {
        model.addAttribute("action", "updateEmail");
        final Optional<User> user = checkPasswordMatch(userModel, bindingResult);
        if (user.isPresent()) {
            if (!emailValidator.validate(userModel, bindingResult)) {
                return viewName;
            }
            User userFound = user.get();
            userFound.setEmail(userModel.getEmail());
            userService.update(userFound);
            model.addAttribute("status", "Email address has been changed successfully!");
        }
        return viewName;
    }

    @PostMapping(value = "/account", params = "action=updatePassword")
    public String postAccountUpdatePassword(@ModelAttribute("user") User userModel, BindingResult bindingResult, Model model) {
        model.addAttribute("action", "updatePassword");
        final Optional<User> user = checkPasswordMatch(userModel, bindingResult);
        if (user.isPresent()) {
            if (!passwordValidator.validate(userModel, bindingResult)) {
                return viewName;
            }
            userService.updatePassword(user.get(), userModel.getPassword());
            model.addAttribute("status", "Password has been changed successfully!");
        }
        return viewName;
    }

    private Optional<User> checkPasswordMatch(@ModelAttribute("user") User userModel, BindingResult bindingResult) {
        try {
            return Optional.of(userService.passwordMatch(userModel.getUsername(), userModel.getCurPassword()));
        } catch (InvalidCredentialsError e) {
            log.debug("Current password does not match.");
            bindingResult.addError(new FieldError("user", "curPassword", "Current password does not match."));
            return Optional.empty();
        }
    }
}
