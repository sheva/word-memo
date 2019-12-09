package com.essheva.wordMemo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Getter
@Setter
@ToString(exclude = {"salt", "password", "curPassword", "passwordVerified"})
@Document
@CompoundIndexes({
        @CompoundIndex(name = "username_email", def = "{'username' : 1, 'email': 1}", unique = true)
})
public class User {

    @Id
    private String id;

    @NotEmpty(message = "Must not be blank or empty.")
    @Pattern(regexp = "^[\\p{Alnum}]{2,}$", message = "Should contain alphanumeric symbols and at least 2.")
    @Indexed(unique = true)
    private String username;

    @NotEmpty(message = "Must not be blank or empty.")
    @Pattern(regexp = "^.{3,}$", message = "Should contain at least 3 symbols.")
    private String password;

    @Transient
    private String curPassword;

    @Transient
    private String passwordVerified;

    private String salt;

    @NotEmpty(message = "Must not be blank or empty.")
    @Email(message = "Invalid email mentioned. Please, double check.")
    @Indexed(unique = true)
    private String email;

    private LocalDate memberSince = LocalDate.now(ZoneOffset.UTC);
}
