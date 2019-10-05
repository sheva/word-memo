package com.essheva.wordMemo.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsError extends RuntimeException {

    private String property;
    private String value;

    public UserAlreadyExistsError() {
    }

    public UserAlreadyExistsError(String message) {
        super(message);
    }

    public UserAlreadyExistsError(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistsError(String property, String value, Throwable cause) {
        super(String.format("User with %s '%s' already exists. Please, choose another %s.",
                property, value, property), cause);
        this.property = property;
        this.value = value;
    }
}
