package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document
public class ResetToken {

    public static final Duration TOKEN_LIVENESS = Duration.ofHours(3L);

    @Id
    private String id;

    private String userId;

    private String token;

    private LocalDateTime expiration;

    public ResetToken(String userId, String token, LocalDateTime expiration) {
        this.userId = userId;
        this.token = token;
        this.expiration = expiration;
    }
}
