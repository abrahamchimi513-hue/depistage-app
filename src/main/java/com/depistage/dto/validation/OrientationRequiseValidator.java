package com.depistage.dto.validation;

import com.depistage.dto.DepistageForm;
import com.depistage.enums.Resultat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrientationRequiseValidator implements ConstraintValidator<OrientationRequise, DepistageForm> {

    @Override
    public boolean isValid(DepistageForm form, ConstraintValidatorContext context) {
        if (form == null || form.getResultat() == null || form.getResultat() == Resultat.NEGATIF) {
            return true;
        }
        String orientation = form.getOrientation();
        if (orientation != null && !orientation.isBlank()) {
            return true;
        }
        // L'erreur est rattachée au champ « orientation » et non à l'objet entier
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("orientation")
                .addConstraintViolation();
        return false;
    }
}
