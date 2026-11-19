package com.depistage.dto.validation;

import com.depistage.dto.CampagneForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PeriodeValideValidator implements ConstraintValidator<PeriodeValide, CampagneForm> {

    @Override
    public boolean isValid(CampagneForm form, ConstraintValidatorContext context) {
        if (form == null || form.getDateDebut() == null || form.getDateFin() == null
                || !form.getDateFin().isBefore(form.getDateDebut())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("dateFin")
                .addConstraintViolation();
        return false;
    }
}
