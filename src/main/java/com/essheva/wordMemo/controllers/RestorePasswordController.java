package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.services.ResetTokenService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.mail.MailjetService;
import com.essheva.wordMemo.services.validators.CaptchaValidator;
import com.essheva.wordMemo.services.validators.ChangePasswordValidator;
import com.essheva.wordMemo.services.validators.UserEmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Controller
public class RestorePasswordController {

    private final UserEmailValidator userEmailValidator;
    private final ChangePasswordValidator passwordValidator;
    private final CaptchaValidator captchaValidator;
    private final ResetTokenService resetTokenService;
    private final UserService userService;
    private final MailjetService mailjetService;

    public RestorePasswordController(UserEmailValidator userEmailValidator, ChangePasswordValidator passwordValidator,
                                     CaptchaValidator captchaValidator, ResetTokenService resetTokenService,
                                     UserService userService, MailjetService mailjetService) {
        this.userEmailValidator = userEmailValidator;
        this.passwordValidator = passwordValidator;
        this.captchaValidator = captchaValidator;
        this.resetTokenService = resetTokenService;
        this.userService = userService;
        this.mailjetService = mailjetService;
    }

    @GetMapping("/restore")
    public String restoreInitial(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("action", "start");
        model.addAttribute("originURL", "index");
        model.addAttribute("captcha", "");
        return "restore";
    }

    @PostMapping("/restore")
    public String restoreInitial(@ModelAttribute("user") User user, @ModelAttribute("captcha") String captcha,
                                 BindingResult bindingResult, Model model, HttpServletRequest request) {

        String expected = (String) request.getSession().getAttribute("CAPTCHA");
        if (!Strings.isBlank(expected)) {
            boolean result = captchaValidator.validate(expected, captcha, bindingResult);
            if (log.isDebugEnabled()) {
                log.debug("Captcha validation " + (result ? "succeeded." : "failed."));
            }
        }

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
    public String restoreTokenSentToEmail(@PathVariable String userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("action", "sent");
        model.addAttribute("userEmail", user.getEmail());
        return "restore";
    }

    @GetMapping(value = "/restore", params = {"token"})
    public String restoreBySpecifiedToken(@RequestParam("token") String token, Model model) {
        ResetToken resetToken = resetTokenService.findByToken(token);
        User user = userService.findUserById(resetToken.getUserId());
        model.addAttribute("action", "newPassword");
        model.addAttribute("user", user);
        return "restore";
    }

// TODO: use PATCH
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
    public String restoreFinal(@PathVariable String userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("action", "complete");
        return "restore";
    }
}
