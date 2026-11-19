package com.depistage.exception;

import org.springframework.http.HttpStatus;

public class DuplicateException extends DepistageException {

    public DuplicateException(String field, String message) {
        super(message, field, HttpStatus.CONFLICT);
    }
}
