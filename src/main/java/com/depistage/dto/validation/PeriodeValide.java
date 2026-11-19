package com.depistage.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** La date de fin d'une campagne ne peut pas précéder sa date de début. */
@Documented
@Constraint(validatedBy = PeriodeValideValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PeriodeValide {
    String message() default "{periode.invalide}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
