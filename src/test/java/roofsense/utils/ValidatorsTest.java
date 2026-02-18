package roofsense.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Validators} utility methods.
 */
class ValidatorsTest {

    @Test
    void testGetValidatorReturnsSingleton() {
        final var v1 = Validators.getValidator();
        final var v2 = Validators.getValidator();

        assertNotNull(v1);
        assertSame(v1, v2, "getValidator should return the same singleton instance");
    }

    @Test
    void testValidateNullObjectThrowsException() {
        assertThrows(NullPointerException.class, () -> Validators.validate(null));
    }

    @Test
    void testValidateValidObjectReturnsEmptyViolations() {
        final var bean = new TestBean();
        bean.setName("John");

        final Set<ConstraintViolation<TestBean>> violations = Validators.validate(bean);
        assertTrue(violations.isEmpty(), "Expected no violations for a valid bean");
    }

    @Test
    void testValidateInvalidObjectReturnsViolations() {
        final var bean = new TestBean();
        bean.setName("   ");

        final Set<ConstraintViolation<TestBean>> violations = Validators.validate(bean);
        assertFalse(violations.isEmpty(), "Expected violations for an invalid bean");
    }

    @Test
    void testValidatePropertyWrongPropertyNameThrowsException() {
        assertThrows(NullPointerException.class, () -> Validators.validateProperty(null, "code"));
    }

    @Test
    void testValidatePropertyInvalidPropertyReturnsViolations() {
        final var bean = new TestBean();
        bean.setName("   ");

        final Set<ConstraintViolation<TestBean>> nameViolations = Validators.validateProperty(bean, "name");
        assertFalse(nameViolations.isEmpty(), "Expected violations for invalid name");
    }

    @Test
    void testValidatePropertyValidPropertyReturnsNoViolations() {
        final var bean = new TestBean();
        bean.setName("Alice");

        final Set<ConstraintViolation<TestBean>> noViolations = Validators.validateProperty(bean, "name");
        assertTrue(noViolations.isEmpty(), "Expected no violations for valid name");
    }

    /**
     * Simple bean used to trigger validation constraints in test cases.
     */
    private static final class TestBean {

        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

    }

}
