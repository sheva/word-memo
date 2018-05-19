package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document
public class ResetToken {

    public static final int TOKEN_LIVENESS_SECONDS =  3 * 60 * 60;

    @Id
    private String id;

    private String userId;

    private String token;

    @Indexed(expireAfterSeconds = TOKEN_LIVENESS_SECONDS)
    private LocalDateTime expiration;

    public ResetToken(String userId, String token, LocalDateTime expiration) {
        this.userId = userId;
        this.token = token;
        this.expiration = expiration;
    }
}
