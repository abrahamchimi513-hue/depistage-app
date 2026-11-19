package com.depistage.controller;

import com.depistage.exception.DepistageException;
import org.springframework.validation.BindingResult;

/** Affiche dans le formulaire une erreur de règle métier venue d'un service. */
final class FormErrors {

    private FormErrors() {
    }

    static void apply(BindingResult result, DepistageException e) {
        if (e.getField() != null) {
            result.rejectValue(e.getField(), "metier", e.getMessage());
        } else {
            result.reject("metier", e.getMessage());
        }
    }
}
