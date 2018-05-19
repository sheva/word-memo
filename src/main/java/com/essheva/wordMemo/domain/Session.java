package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Getter
@Setter
@ToString
public class Session {

    public static final int SESSION_LIVENESS_SECONDS =  3 * 60 * 60;

    @Id
    private String id;

    @NotEmpty(message = "Must not be blank or empty.")
    private String username;

    @Indexed(expireAfterSeconds = SESSION_LIVENESS_SECONDS)
    private LocalDateTime expired = LocalDateTime.now(ZoneOffset.UTC);

    public Session(String id, String username) {
        this.id = id;
        this.username = username;
    }
}
