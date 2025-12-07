package roofsense.entities.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import roofsense.entities.validation.annotations.ValidCode;

import java.util.regex.Pattern;

/**
 * Validator for {@link ValidCode} annotation.
 */
public class ValidCodeValidator implements ConstraintValidator<ValidCode, String> {

    private static final Pattern CODE_VALIDATION_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-]*$");

    private String message;
    private String messageForBlank;

    /**
     * Initializes the validator with constraint annotation parameters.
     * Subclasses can override this method to customize initialization behavior.
     *
     * @param constraintAnnotation the annotation instance
     */
    @Override
    public final void initialize(final ValidCode constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.messageForBlank = constraintAnnotation.messageForBlank();
    }

    /**
     * Validates the code string value.
     *
     * @param value   the value to validate
     * @param context the constraint validator context
     *
     * @return true if valid, false otherwise
     */
    @Override
    public final boolean isValid(final String value, final ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (value == null || value.isBlank()) {
            context.buildConstraintViolationWithTemplate(this.messageForBlank).addConstraintViolation();
            return false;
        }

        if (!CODE_VALIDATION_PATTERN.matcher(value).matches()) {
            context.buildConstraintViolationWithTemplate(this.message).addConstraintViolation();
            return false;
        }

        return true;
    }

}
