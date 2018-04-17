package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString(exclude = {"salt", "password", "passwordVerified"})
@Document
public class User {

    @Id
    private String id;

    @NotEmpty(message = "Must not be blank or empty.")
    @Pattern(regexp = "^[\\p{Alnum}]{2,}$", message = "Should contain at least 2 alphanumeric symbols.")
    @Indexed(unique = true)
    private String username;

    @NotEmpty(message = "Must not be blank or empty.")
    @Pattern(regexp = "^.{3,}$", message = "Should contain at least 3 symbols.")
    private String password;

    @Transient
    private String passwordVerified;

    private String salt;

    @NotEmpty(message = "Must not be blank or empty.")
    @Email(message = "Invalid email mentioned. Please, double check.")
    private String email;
}
