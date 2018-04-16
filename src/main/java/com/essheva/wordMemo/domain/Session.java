package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class Session {

    @Id
    private String id;
    private String username;

    public Session(String id, String username) {
        this.id = id;
        this.username = username;
    }
}
