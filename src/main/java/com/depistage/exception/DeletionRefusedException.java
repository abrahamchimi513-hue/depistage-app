package com.depistage.exception;

import org.springframework.http.HttpStatus;

public class DeletionRefusedException extends DepistageException {

    public DeletionRefusedException(String message) {
        super(message, null, HttpStatus.CONFLICT);
    }
}
