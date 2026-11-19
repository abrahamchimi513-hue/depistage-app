package com.depistage.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** Règle croisée : l'orientation est obligatoire quand le résultat n'est pas NEGATIF. */
@Documented
@Constraint(validatedBy = OrientationRequiseValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrientationRequise {
    String message() default "{orientation.obligatoire}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
