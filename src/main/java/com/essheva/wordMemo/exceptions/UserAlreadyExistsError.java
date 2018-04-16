package com.essheva.wordMemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsError extends RuntimeException {

    public UserAlreadyExistsError() {
    }

    public UserAlreadyExistsError(String message) {
        super(message);
    }

    public UserAlreadyExistsError(String message, Throwable cause) {
        super(message, cause);
    }
}
