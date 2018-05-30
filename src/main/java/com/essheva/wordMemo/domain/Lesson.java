package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Setter
@Getter
@ToString
public class Lesson {
    @Id
    private String id;
    private String name;
    private String userId;
    private Set<Word> words;
}
