package com.depistage.exception;

import org.springframework.http.HttpStatus;

/** Racine des exceptions métier. Le champ (facultatif) permet d'afficher l'erreur sous le bon champ du formulaire. */
public abstract class DepistageException extends RuntimeException {

    private final String field;
    private final HttpStatus status;

    protected DepistageException(String message, String field, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
    }

    public String getField() {
        return field;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
