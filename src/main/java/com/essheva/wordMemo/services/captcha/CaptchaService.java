package com.essheva.wordMemo.services.captcha;

import com.essheva.wordMemo.crypto.EncryptionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;

@Slf4j
@Service
public class CaptchaService {

    private static final int CAPTCHA_STRING_LENGTH = 7;
    private static final int CAPTCHA_WIDTH = 140;
    private static final int CAPTCHA_HEIGHT = 30;

    public Captcha generateCaptcha() throws IOException, FontFormatException {
        String string = EncryptionUtils.generateSalt(CAPTCHA_STRING_LENGTH);
        Font cabinSketch = Font.createFont(Font.TRUETYPE_FONT,
                Paths.get("src/main/resources/static/fonts/cabin-sketch/CabinSketch-Bold.ttf").toFile());
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        try {
            g.setColor(new Color(87, 184, 70));
            g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(cabinSketch.deriveFont(25f));

            g.drawString(string, 13, 23);
        } finally {
            g.dispose();
        }

        return new Captcha(string, image);
    }

    @Getter
    public class Captcha {
        private String string;
        private BufferedImage image;

        private Captcha(String string, BufferedImage image) {
            this.string = string;
            this.image = image;
        }
    }
}
