package com.depistage.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends DepistageException {

    public BusinessRuleException(String message) {
        super(message, null, HttpStatus.CONFLICT);
    }

    public BusinessRuleException(String field, String message) {
        super(message, field, HttpStatus.CONFLICT);
    }
}
