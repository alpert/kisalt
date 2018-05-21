package com.github.alpert.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR) // 500
public class UrlCorruptedException extends RuntimeException {

    public UrlCorruptedException(String message) {
        super(message);
    }
}
