package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString(exclude = {"salt", "password", "passwordVerified"})
@Document
public class User {

    @Id
    private String id;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[\\p{Alnum}]{2,}$", message = "Should contain at least 2 alphanumeric symbols.")
    @Indexed(unique = true)
    private String username;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^.{6,}$", message = "Should contain at least 6 symbols.")
    private String password;

    @Transient
    private String passwordVerified;

    private String salt;

    @Pattern(regexp = "|^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
            message = "Invalid email mentioned. Please, double check.")
    private String email;
}
