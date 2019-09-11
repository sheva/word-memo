package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.domain.User;
import com.essheva.wordMemo.exceptions.UserNotFound;
import com.essheva.wordMemo.model.RestoreInfo;
import com.essheva.wordMemo.services.ResetTokenService;
import com.essheva.wordMemo.services.UserService;
import com.essheva.wordMemo.services.mail.MailjetService;
import com.essheva.wordMemo.services.validators.ChangePasswordValidator;
import com.essheva.wordMemo.services.validators.RestoreInfoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Controller
@ComponentScan(basePackages = {"com.essheva.wordMemo.controllers"})
public class RestorePasswordController {

    private final ChangePasswordValidator passwordValidator;
    private final RestoreInfoValidator restoreInfoValidator;
    private final ResetTokenService resetTokenService;
    private final UserService userService;
    private final MailjetService mailjetService;

    public RestorePasswordController(ChangePasswordValidator passwordValidator, RestoreInfoValidator restoreInfoValidator,
                                     ResetTokenService resetTokenService, UserService userService, MailjetService mailjetService) {
        this.passwordValidator = passwordValidator;
        this.restoreInfoValidator = restoreInfoValidator;
        this.resetTokenService = resetTokenService;
        this.userService = userService;
        this.mailjetService = mailjetService;
    }

    @GetMapping("/restore")
    public String restoreInitial(Model model) {
        model.addAttribute("restoreInfo", new RestoreInfo());
        model.addAttribute("action", "start");
        model.addAttribute("originURL", "index");
        return "restore";
    }

    @PostMapping("/restore")
    public String restoreInitial(@ModelAttribute("restoreInfo") RestoreInfo actualInfo, BindingResult bindingResult,
                                 Model model, HttpServletRequest request) {

        final String expectedCaptcha = (String) request.getSession().getAttribute("CAPTCHA");
        RestoreInfo expectedInfo = new RestoreInfo();
        expectedInfo.setCaptcha(expectedCaptcha);

        if (!restoreInfoValidator.validate(expectedInfo, actualInfo, bindingResult)) {
            model.addAttribute("action", "start");
            return "restore";
        }

        try {
            User userFound = userService.findUserByEmail(actualInfo.getEmail());
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
