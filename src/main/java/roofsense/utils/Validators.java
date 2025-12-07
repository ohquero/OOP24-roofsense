package roofsense.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.commons.lang3.Validate;

import java.util.Set;

/**
 * Utility class for objects validation using Jakarta Bean Validation.
 */
public final class Validators {

    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = FACTORY.getValidator();

    private Validators() {
    }

    /**
     * Returns the shared validator instance.
     *
     * @return the validator
     */
    public static Validator getValidator() {
        return VALIDATOR;
    }

    /**
     * Validates all constraints on an object.
     *
     * @param <T>    the type of object to validate
     * @param object the object to validate
     *
     * @return set of constraint violations, empty if valid
     */
    public static <T> Set<ConstraintViolation<T>> validate(final T object) {
        Validate.notNull(object, "Object to validate cannot be null");
        return VALIDATOR.validate(object);
    }

    /**
     * Validates a specific property of an object.
     *
     * @param <T>          the type of object to validate
     * @param object       the object to validate
     * @param propertyName the name of the property to validate
     *
     * @return set of constraint violations for the property, empty if valid
     */
    public static <T> Set<ConstraintViolation<T>> validateProperty(final T object, final String propertyName) {
        Validate.notNull(object, "Object to validate cannot be null");
        return VALIDATOR.validateProperty(object, propertyName);
    }

}
