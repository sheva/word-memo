package com.essheva.wordMemo.services.mail;

import com.essheva.wordMemo.domain.ResetToken;
import com.essheva.wordMemo.domain.User;
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

    public void sendMailForPasswordReset(User user, String restoreURL, String token) {
        final String receiverEmail = user.getEmail();
        final JSONArray recipients = new JSONArray().put(new JSONObject().put(EMAIL, receiverEmail));
        final String senderEmail = config.getSenderEmail();
        final String linkToResetPassword = restoreURL + "?token=" + token;

        String body = "Hi " + user.getUsername() + ",\n" +
                "\n" +
                "A password reset for your account was requested.\n" +
                "\n" +
                "You can use the following link to reset your password:\n" + linkToResetPassword +
                "\n\n" +
                "Note that this link will expire after " + ResetToken.TOKEN_LIVENESS.toHours() + " hours. After that time, " +
                "you will have to resubmit the request for a password reset by " + restoreURL + "\n\n" +
                "Thanks,\n" +
                "Word Memo.";

        MailjetRequest email = new MailjetRequest(Email.resource)
                .property(FROMEMAIL, senderEmail)
                .property(FROMNAME, "Word Memo")
                .property(SUBJECT, "Reset password on Word Memo")
                .property(TEXTPART, body)
                .property(RECIPIENTS, recipients)
                .property(MJCUSTOMID, "WM-Email");
        try {
            MailjetResponse response = client.post(email);
            log.debug("Email " + (response.getStatus() == 200 ? "successfully" : "not") + " sent to " + receiverEmail);
        } catch (MailjetException | MailjetSocketTimeoutException e) {
            log.error("Error occurred during sending email to " + receiverEmail, e.getMessage(), e);
            throw new InternalServerError("Error occurred during sending email to " + receiverEmail + ". Please, try again later.", e);
        }
    }
}