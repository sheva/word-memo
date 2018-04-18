package com.essheva.wordMemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResetTokenNotFoundError extends RuntimeException {

    public ResetTokenNotFoundError() {
    }

    public ResetTokenNotFoundError(String message) {
        super(message);
    }

    public ResetTokenNotFoundError(String message, Throwable cause) {
        super(message, cause);
    }
}
