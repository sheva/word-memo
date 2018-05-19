package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.services.ResetTokenService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.mail.MailjetService;
import com.essheva.wordMemo.services.validators.ChangePasswordValidator;
import com.essheva.wordMemo.services.validators.UserEmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Controller
public class RestorePasswordController {

    private final UserService userService;
    private final UserEmailValidator userEmailValidator;
    private final ChangePasswordValidator passwordValidator;
    private final ResetTokenService resetTokenService;
    private final MailjetService mailjetService;

    public RestorePasswordController(UserService userService, UserEmailValidator userEmailValidator,
                                     ChangePasswordValidator passwordValidator, ResetTokenService resetTokenService,
                                     MailjetService mailjetService) {
        this.userService = userService;
        this.userEmailValidator = userEmailValidator;
        this.passwordValidator = passwordValidator;
        this.resetTokenService = resetTokenService;
        this.mailjetService = mailjetService;
    }

    @GetMapping("/restore")
    public String restorePassword(Model model, HttpServletRequest request) {
        model.addAttribute("user", new User());
        model.addAttribute("action", "start");
        model.addAttribute("originURL", request.getRequestURL());
        return "restore";
    }

    @PostMapping("/restore")
    public String restorePasswordAction(@ModelAttribute("user") User user, BindingResult bindingResult, Model model, HttpServletRequest request) {
        userEmailValidator.validate(user, bindingResult);
        try {
            User userFound = userService.findUserByEmail(user.getEmail());
            ResetToken resetToken = resetTokenService.createToken(userFound.getId());

            mailjetService.sendMailForPasswordReset(userFound, request.getRequestURL().toString(), resetToken.getToken());

            return "redirect:restore/" + userFound.getId() + "/sent";
        }
        catch (UserNotFound e) {
            log.warn(e.getMessage());
            FieldError error = new FieldError("user", "email", "User does not exists with this email address.");
            bindingResult.addError(error);
            if (log.isDebugEnabled()) {
                log.debug(error.toString());
            }
            model.addAttribute("action", "start");
            return "restore";
        }
    }

    @GetMapping("/restore/{userId}/sent")
    public String restorePasswordSent(@PathVariable String userId, Model model, HttpServletRequest request) {
        User user = userService.findUserById(userId);
        model.addAttribute("action", "sent");
        model.addAttribute("userEmail", user.getEmail());
        return "restore";
    }

    @GetMapping(value = "/restore", params = {"token"})
    public String restorePassword(@RequestParam(value = "token") String token, Model model) {
        ResetToken resetToken = resetTokenService.findByToken(token);
        User user = userService.findUserById(resetToken.getUserId());
        model.addAttribute("action", "newPassword");
        model.addAttribute("user", user);
        return "restore";
    }

    @PostMapping("/restore/{userId}/newPassword")
    public String saveNewPassword(@ModelAttribute("user") User user, BindingResult bindingResult,
                                  @PathVariable String userId, Model model) {
        final User userFound = userService.findUserById(userId);

        if (!passwordValidator.validate(user, bindingResult)) {
            model.addAttribute("action", "newPassword");
            model.addAttribute("userId", userFound.getId());
            model.addAttribute("username", userFound.getUsername());

            return "restore";
        }

        userService.updatePassword(userFound, user.getPassword());
        resetTokenService.deleteAllTokensForUser(userFound.getId());

        return "redirect:/restore/" + userFound.getId() + "/complete";
    }

    @GetMapping("/restore/{userId}/complete")
    public String newPasswordSet(@PathVariable String userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("action", "complete");
        return "restore";
    }
}
