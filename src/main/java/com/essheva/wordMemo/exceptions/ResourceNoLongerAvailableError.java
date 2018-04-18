package com.essheva.wordMemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class ResourceNoLongerAvailableError extends RuntimeException {

    public ResourceNoLongerAvailableError() {
    }

    public ResourceNoLongerAvailableError(String message) {
        super(message);
    }

    public ResourceNoLongerAvailableError(String message, Throwable cause) {
        super(message, cause);
    }
}
