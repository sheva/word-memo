package com.essheva.wordMemo.services.mail;

import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.exceptions.InternalServerError;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Email;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import static com.mailjet.client.resource.Contact.*;
import static com.mailjet.client.resource.Email.*;


@Slf4j
@Service
public class MailjetService {

    private final MailjetClient client;
    private final SMTPConfig config;

    public MailjetService(SMTPConfig config) {
        this.config = config;
        this.client = new MailjetClient(config.getSMTPUsername(), config.getSMTPPassword());
    }

    public void sendMailWithNewPassword(String username, String to, String restoreURL, String token) {
        JSONArray recipients = new JSONArray().put(new JSONObject().put(EMAIL, to));
        String linkToResetPassword = restoreURL + "?token=" + token;
        log.info("Restore password link generated " + linkToResetPassword);
        String text = "Hi " + username + ",\n" +
                "\n" +
                "A password reset for your account was requested.\n" +
                "\n" +
                "You can use the following link to reset your password:\n" + linkToResetPassword +
                "\n" +
                "Note that this link is valid for " + ResetToken.TOKEN_LIVENESS.toHours() + " hours. After the time limit has expired, " +
                "you will have to resubmit the request for a password reset by " + restoreURL;

        MailjetRequest email = new MailjetRequest(Email.resource)
                .property(FROMEMAIL, config.getSenderEmail())
                .property(FROMNAME, "Word Memo")
                .property(SUBJECT, "Reset password on Word Memo")
                .property(TEXTPART, text)
                .property(RECIPIENTS, recipients)
                .property(MJCUSTOMID, "WM-Email");
        try {
            MailjetResponse response = client.post(email);
            log.info("Email " + (response.getStatus() == 200 ? "successfully" : "not") + " sent to " + to);
        } catch (MailjetException | MailjetSocketTimeoutException e) {
            log.error("Error occurred during sending email to " + to, e);
            throw new InternalServerError("Error occurred during sending email to " + to + "." + e.getMessage(), e);
        }
    }
}