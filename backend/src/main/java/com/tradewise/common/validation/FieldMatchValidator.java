package com.tradewise.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

/**
 * Validator backing {@link FieldMatch}. Reads both properties reflectively and
 * reports the failure against the second field for a precise error location.
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldMatch constraint) {
        this.firstFieldName = constraint.first();
        this.secondFieldName = constraint.second();
        this.message = constraint.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
        Object firstValue = wrapper.getPropertyValue(firstFieldName);
        Object secondValue = wrapper.getPropertyValue(secondFieldName);

        boolean matches = Objects.equals(firstValue, secondValue);
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondFieldName)
                    .addConstraintViolation();
        }
        return matches;
    }
}
