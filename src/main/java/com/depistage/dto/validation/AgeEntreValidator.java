package com.depistage.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AgeEntreValidator implements ConstraintValidator<AgeEntre, LocalDate> {

    private int min;
    private int max;

    @Override
    public void initialize(AgeEntre annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(LocalDate dateNaissance, ConstraintValidatorContext context) {
        // Les dates absentes ou dans le futur sont déjà refusées par @NotNull et @Past
        if (dateNaissance == null || !dateNaissance.isBefore(LocalDate.now())) {
            return true;
        }
        int age = Period.between(dateNaissance, LocalDate.now()).getYears();
        return age >= min && age <= max;
    }
}
