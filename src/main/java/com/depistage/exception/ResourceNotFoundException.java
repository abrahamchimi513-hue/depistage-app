package com.depistage.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends DepistageException {

    public ResourceNotFoundException(String message) {
        super(message, null, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String field, String message) {
        super(message, field, HttpStatus.NOT_FOUND);
    }
}
