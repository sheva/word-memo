package com.essheva.wordMemo.services.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:mailjet.secret")
public class SMTPConfig {

    @Value("${smtp.username}")
    private String smtpUsername;

    @Value("${smtp.password}")
    private String smtpPassword;

    @Value("${sender.email}")
    private String senderEmail;

    String getSMTPUsername() {
        return smtpUsername;
    }

    String getSMTPPassword() {
        return smtpPassword;
    }

    String getSenderEmail() {
        return senderEmail;
    }
}
