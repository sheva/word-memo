package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Setter
@Getter
@ToString
public class Word {

    @Id
    private String id;
    private String original;
    private String translation;
}
