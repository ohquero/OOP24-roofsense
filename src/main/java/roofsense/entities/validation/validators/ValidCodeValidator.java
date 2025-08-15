package roofsense.entities.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import roofsense.entities.validation.annotations.ValidCode;

/**
 * Validator for {@link ValidCode} constraint annotation.
 * Validates that a string code does not contain unallowed characters.
 */
public class ValidCodeValidator implements ConstraintValidator<ValidCode, String> {

    /**
     * Validates if the given code string does not contain unallowed characters.
     *
     * @param value   the code string to validate
     * @param context context in which the constraint is evaluated
     *
     * @return true if the code is null (delegating null validation to @NotNull) or contains no unallowed characters,
     *         false otherwise
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        // If code is null, we leverage the validation to the @NotNull annotation
        return value == null || value.matches("^\\S+$");
    }

}
