package com.essheva.wordMemo.controllers;

import com.essheva.wordMemo.exceptions.InternalServerError;
import com.essheva.wordMemo.services.captcha.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Controller
@ComponentScan(basePackages = {"com.essheva.wordMemo.controllers"})
public class CaptchaController {

    private final CaptchaService service;
    private static final String FILE_TYPE = "jpeg";

    public CaptchaController(CaptchaService service) {
        this.service = service;
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void getImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            CaptchaService.Captcha captcha = service.generateCaptcha();
            HttpSession session = request.getSession();
            session.setAttribute("CAPTCHA", captcha.getString());

            try (OutputStream outputStream = response.getOutputStream()) {
                ImageIO.write(captcha.getImage(), FILE_TYPE, outputStream);
            }
        } catch (IOException | FontFormatException e) {
            log.error("Captcha generation failed.", e);
            throw new InternalServerError("Captcha generation failed: " + e.getMessage(), e);
        }
    }
}
