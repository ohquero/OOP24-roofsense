package roofsense.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import roofsense.entities.validation.annotations.ValidCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoofTest {

    private static final String ROOF_CORRECT_CODE = "test-code";
    private static final String ROOF_CORRECT_ADDRESS = "123 Test Street";
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static Roof createValidRoof() {
        final var roof = new Roof();
        roof.setCode(ROOF_CORRECT_CODE);
        roof.setBuildingAddress(ROOF_CORRECT_ADDRESS);
        return roof;
    }

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void testDefaultConstructorWithDefaultParameters() {
        final var roof = new Roof();

        assertNull(roof.getCode());
        assertNull(roof.getBuildingAddress());
    }

    @Test
    void testDefaultConstructorWithParameters() {
        final var roof = new Roof(ROOF_CORRECT_CODE, ROOF_CORRECT_ADDRESS);

        assertEquals(ROOF_CORRECT_CODE, roof.getCode());
        assertEquals(ROOF_CORRECT_ADDRESS, roof.getBuildingAddress());
    }

    @Test
    void testCode() {
        final var roof = new Roof();
        assertNull(roof.getCode());

        final var anotherCode = "another_test_code";
        roof.setCode(anotherCode);
        assertEquals(anotherCode, roof.getCode());
    }

    @Test
    void testBuildingAddress() {
        final var roof = new Roof();
        assertNull(roof.getBuildingAddress());

        final var address = "456 Another Test Street";
        roof.setBuildingAddress(address);
        assertEquals(address, roof.getBuildingAddress());
    }

    @Test
    void testEqualsWithNull() {
        final var roof = createValidRoof();
        assertNotEquals(roof, null);
    }

    @Test
    void testEqualsWithDifferentType() {
        final var roof = createValidRoof();
        assertNotEquals(roof, new Object());
    }

    @Test
    void testEqualsHashCodeRoofsWithSameCode() {
        final var roof1 = new Roof();
        roof1.setCode(ROOF_CORRECT_CODE);
        roof1.setBuildingAddress("Address 1");

        final var roof2 = new Roof();
        roof2.setCode(ROOF_CORRECT_CODE);
        roof2.setBuildingAddress("Address 2");

        assertEquals(roof1, roof2);
        assertEquals(roof1.hashCode(), roof2.hashCode());
    }

    @Test
    void testEqualsHashCodeRoofsWithDifferentCodes() {
        final var roof1 = new Roof();
        roof1.setCode("code1");
        roof1.setBuildingAddress(ROOF_CORRECT_ADDRESS);

        final var roof2 = new Roof();
        roof2.setCode("code2");
        roof2.setBuildingAddress(ROOF_CORRECT_ADDRESS);

        assertNotEquals(roof1, roof2);
        assertNotEquals(roof1.hashCode(), roof2.hashCode());
    }

    @Test
    void testEqualHashCodeBothRoofsWithNullCode() {
        final var roof1 = new Roof();
        roof1.setBuildingAddress(ROOF_CORRECT_ADDRESS);
        final var roof2 = new Roof();
        roof2.setBuildingAddress("Different Address");

        assertEquals(roof1, roof2);
        assertEquals(roof1.hashCode(), roof2.hashCode());
    }

    @Test
    void testEqualsHashCodeOnlyOneRoofWithNullCode() {
        final var roof1 = createValidRoof();
        final var roof2 = new Roof();
        roof2.setCode(null);

        assertNotEquals(roof1, roof2);
        assertNotEquals(roof1.hashCode(), roof2.hashCode());
    }

    @Test
    void testValidRoof() {
        final var roof = createValidRoof();

        final var violations = validator.validate(roof);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testCodeValidationWithValidCodes() {
        final var roof = createValidRoof();

        final String[] validCodes = {
                "validCode",
                "code123",
                "CODE",
                "a",
                "code-with-dashes",
                "123456",
        };

        for (final var validCode : validCodes) {
            roof.setCode(validCode);
            final var violations = validator.validate(roof);

            assertTrue(
                    violations.isEmpty(),
                    "Code '" + validCode + "' should be valid but got violations: " + violations
            );
        }
    }

    @Test
    void testCodeValidationWithNullCode() {
        final var roof = createValidRoof();
        roof.setCode(null);

        final var violations = validator.validate(roof);

        assertEquals(1, violations.size(), "Code should have exactly one validation violation");

        final var violation = violations.iterator().next();
        assertEquals("code", violation.getPropertyPath().toString());
        assertEquals(ValidCode.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testCodeValidationWithCodesWithInvalidCharacters() {
        final var roof = createValidRoof();

        final String[] invalidCodes = {
                "",
                "code.with.dots",
                "code@symbol",
                "code_with_underscores",
                "code with space",
                " codeWithLeadingSpace",
                "codeWithTrailingSpace ",
                " code with multiple spaces ",
                "code\twith\ttab",
                "code\nwith\nnewline",
                "code\rwith\rcarriagereturn",
                "\t",
                "\n",
                " ",
                "  ",
                "code with\tmixed\nwhitespace",
        };

        for (final String invalidCode : invalidCodes) {
            roof.setCode(invalidCode);
            final var violations = validator.validate(roof);

            assertEquals(
                    1,
                    violations.size(),
                    "Code '" + invalidCode + "' should have exactly one validation violation"
            );

            final var violation = violations.iterator().next();
            assertEquals("code", violation.getPropertyPath().toString());
            assertEquals(ValidCode.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }

    @Test
    void testBuildingAddressValidationWithValidAddresses() {
        final var roof = createValidRoof();

        final String[] validAddresses = {
                "123 Main Street",
                "Valid Address",
                "a",
                "123",
                "Address with multiple words and numbers 456",
                "Address\twith\ttabs",
                "Address\nwith\nnewlines",
                " Address with leading space",
                "Address with trailing space ",
                "  Address with multiple spaces  ",
        };

        for (final String validAddress : validAddresses) {
            roof.setBuildingAddress(validAddress);
            final var violations = validator.validate(roof);

            assertTrue(
                    violations.isEmpty(),
                    "Building address '" + validAddress + "' should be valid but got violations: " + violations
            );
        }
    }

    @Test
    void testBuildingAddressValidationWithInvalidAddresses() {
        final var roof = createValidRoof();

        final String[] invalidAddresses = {
                null, "", " ", "  ", "\t", "\n", "\r", "   \t  \n  \r  ",
        };

        for (final String invalidAddress : invalidAddresses) {
            roof.setBuildingAddress(invalidAddress);
            final var violations = validator.validate(roof);

            assertEquals(
                    1,
                    violations.size(),
                    "Building address '" + invalidAddress + "' should have exactly one validation violation"
            );

            final var violation = violations.iterator().next();
            assertEquals("buildingAddress", violation.getPropertyPath().toString());
            assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }

}
