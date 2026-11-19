package com.depistage.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** La date de naissance annotée doit correspondre à un âge compris entre min et max ans. */
@Documented
@Constraint(validatedBy = AgeEntreValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeEntre {
    String message() default "{age.plage}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 1;

    int max() default 110;
}
