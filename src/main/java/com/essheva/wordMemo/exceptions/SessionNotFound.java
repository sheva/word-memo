package com.essheva.wordMemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SessionNotFound extends RuntimeException {

    public SessionNotFound() {
    }

    public SessionNotFound(String message) {
        super(message);
    }

    public SessionNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
