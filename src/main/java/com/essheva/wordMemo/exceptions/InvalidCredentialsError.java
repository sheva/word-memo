package com.essheva.wordMemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsError extends RuntimeException {

    public InvalidCredentialsError() {
    }

    public InvalidCredentialsError(String message) {
        super(message);
    }

    public InvalidCredentialsError(String message, Throwable cause) {
        super(message, cause);
    }
}
