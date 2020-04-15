package com.backend.tasks.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator to validate that field doesn't contain whitespaces
 */
public class NonWhitespacesFieldValidator implements ConstraintValidator<NonWhitespacesField, String> {

    @Override
    public boolean isValid(String field, ConstraintValidatorContext context) {
        if (field == null || StringUtils.isEmpty(field)) {
            return true;
        }

        return !field.contains(" ");
    }

}
