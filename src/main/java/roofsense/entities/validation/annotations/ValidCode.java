package roofsense.entities.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import roofsense.entities.validation.validators.ValidCodeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to validate if a field contains a valid code format.
 * The validation logic is implemented in {@link ValidCodeValidator}.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCodeValidator.class)
public @interface ValidCode {

    /**
     * @return the error message template for blank code validation failures
     */
    String messageForBlank() default "{validation.not-blank}";

    /**
     * @return the default error message template for code validation failures.
     */
    String message() default "{validation.contains-invalid-characters}";

    /**
     * @return the validation groups to which this constraint belongs
     */
    Class<?>[] groups() default {};

    /**
     * @return the payload associated with the constraint
     */
    Class<? extends Payload>[] payload() default {};

}
