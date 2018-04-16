package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class Session {

    @Id
    private String id;

    @NotNull
    @NotBlank(message = "Must not be blank.")
    private String username;

    public Session(String id, String username) {
        this.id = id;
        this.username = username;
    }
}
