package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.services.mail.MailjetMailService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.validators.RestorePasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Controller
public class RestorePasswordController {

    private final UserService userService;
    private final RestorePasswordValidator restorePasswordValidator;
    private final MailjetMailService mailjetMailService;

    public RestorePasswordController(UserService userService, RestorePasswordValidator restorePasswordValidator, MailjetMailService mailjetMailService) {
        this.userService = userService;
        this.restorePasswordValidator = restorePasswordValidator;
        this.mailjetMailService = mailjetMailService;
    }

    @GetMapping("/restore")
    public String restorePassword(Model model, HttpServletRequest request) {
        model.addAttribute("user", new User());
        model.addAttribute("originURL", request.getContextPath());
        return "restore";
    }

    @PostMapping("/restore")
    public String restorePasswordAction(@ModelAttribute("user") User user, BindingResult bindingResult) {
        restorePasswordValidator.validate(user, bindingResult);
        try {
            userService.findUserByEmail(user.getEmail());
        } catch (UserNotFound e) {
            log.error("User not found.");
            bindingResult.addError(new FieldError("user", "email", "User does not exists with this email address."));
        }
        mailjetMailService.sendMailWithNewPassword(user.getEmail(), "blah-blah-blah");

        return "restore";
    }
}
