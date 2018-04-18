package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.ResourceNoLongerAvailableError;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.services.ResetTokenService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.mail.MailjetService;
import com.essheva.wordMemo.services.validators.NewPasswordValidator;
import com.essheva.wordMemo.services.validators.UserEmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@Controller
public class RestorePasswordController {

    private final UserService userService;
    private final UserEmailValidator userEmailValidator;
    private final NewPasswordValidator newPasswordValidator;
    private final ResetTokenService resetTokenService;
    private final MailjetService mailjetService;

    public RestorePasswordController(UserService userService, UserEmailValidator userEmailValidator,
                                     NewPasswordValidator newPasswordValidator, ResetTokenService resetTokenService,
                                     MailjetService mailjetService) {
        this.userService = userService;
        this.userEmailValidator = userEmailValidator;
        this.newPasswordValidator = newPasswordValidator;
        this.resetTokenService = resetTokenService;
        this.mailjetService = mailjetService;
    }

    @GetMapping(value = "/restore")
    public String restorePassword(Model model,HttpServletRequest request) {
        model.addAttribute("user", new User());
        model.addAttribute("action", "start");
        model.addAttribute("originURL", request.getRequestURL());
        return "restore";
    }

    @PostMapping("/restore")
    public String restorePasswordAction(@ModelAttribute("user") User user, BindingResult bindingResult, HttpServletRequest request) {
        userEmailValidator.validate(user, bindingResult);
        try {
            User userFound = userService.findUserByEmail(user.getEmail());
            ResetToken resetToken = resetTokenService.createToken(userFound.getId());

            String linkToResetPassword = request.getRequestURL() + "?token=" + resetToken.getToken();
            log.info("linkToReset " + linkToResetPassword);
            mailjetService.sendMailWithNewPassword(user.getEmail(), linkToResetPassword);

            return "redirect:restore/" + resetToken.getUserId() + "/sent";

        } catch (UserNotFound e) {
            log.error("User not found.");
            bindingResult.addError(new FieldError("user", "email", "User does not exists with this email address."));

            return "restore";
        }
    }
    @GetMapping(value = "/restore/{userId}/sent")
    public String restorePasswordSent(@PathVariable String userId, Model model, HttpServletRequest request) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("action", "sent");
        model.addAttribute("originURL", request.getRequestURL());
        return "restore";
    }

    @GetMapping(value = "/restore", params = {"token"})
    public String restorePassword(@RequestParam(value = "token") String token, Model model) {
        ResetToken resetToken = resetTokenService.findByToken(token);
        if (LocalDateTime.now().isAfter(resetToken.getExpiration())) {
            log.error("Password reset token expired.");
            throw new ResourceNoLongerAvailableError("Password reset link has expired. Try once more.");
        }
        User user = userService.findUserById(resetToken.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("action", "newPassword");
        return "restore";
    }

    @PostMapping(value = "/restore/{userId}/newPassword")
    public String saveNewPassword(@PathVariable String userId, @ModelAttribute("user") User user,
                                  BindingResult bindingResult, HttpServletRequest request) {
        User userFound = userService.findUserById(userId);
        newPasswordValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "restore?token"; // TODO: fix!
        }
        userService.updatePassword(userFound, user.getPassword());
        resetTokenService.deleteAllTokensForUser(userFound.getId());
        return "redirect:/restore/" + userFound.getId() + "/complete";
    }

    @GetMapping(value = "/restore/{userId}/complete")
    public String newPasswordSet(@PathVariable String userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("action", "complete");
        return "restore";
    }
}
