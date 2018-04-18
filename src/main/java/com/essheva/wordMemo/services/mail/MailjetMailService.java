package com.essheva.wordMemo.services.mail;

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
public class MailjetMailService {

    private static final String API_KEY = "6c9edac5a1ae56f0f91a00f1546885ee";
    private static final String API_SECRET = "181753c86ac7ebb8da573b62349716f2";
    private static final String FROM = "essheva@gmail.com"; // TODO: use something more meaningful as sender email

    private final MailjetClient client;

    MailjetMailService() {
        client = new MailjetClient(API_KEY, API_SECRET);
    }

    public void sendMailWithNewPassword(String to, String linkToResetPassword) {
        JSONArray recipients = new JSONArray().put(new JSONObject().put(EMAIL, to));

        MailjetRequest email = new MailjetRequest(Email.resource)
                .property(FROMEMAIL, FROM)
                .property(FROMNAME, "Word Memo")
                .property(SUBJECT, "Reset password on Word Memo")
                .property(TEXTPART, "I heard that you lost your Word Memo password.\n\nDon’t worry! " +
                        "You can use the following link to reset your password:\n\n" + linkToResetPassword)
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