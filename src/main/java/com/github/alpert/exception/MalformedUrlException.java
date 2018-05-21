package com.github.alpert.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400
public class MalformedUrlException extends RuntimeException {

    public MalformedUrlException(String message) {
        super(message);
    }
}
