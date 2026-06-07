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

    private static final String VALID_CODE = "ROOF";
    private static final String VALID_BUILDING_ADDRESS = "123 Test Street";
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    private static Roof createValidRoof() {
        return new Roof(VALID_CODE, VALID_BUILDING_ADDRESS);
    }

    @Test
    void testEmptyConstructor() {
        final var roof = new Roof();

        assertNull(roof.getCode());
        assertNull(roof.getBuildingAddress());
    }

    @Test
    void testParametrizedConstructor() {
        final var roof = new Roof(VALID_CODE, VALID_BUILDING_ADDRESS);

        assertEquals(VALID_CODE, roof.getCode());
        assertEquals(VALID_BUILDING_ADDRESS, roof.getBuildingAddress());
    }

    @Test
    void testCopyConstructor() {
        final var original = createValidRoof();
        final var copy = new Roof(original);

        assertEquals(original.getCode(), copy.getCode());
        assertEquals(original.getBuildingAddress(), copy.getBuildingAddress());
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
        roof1.setCode(VALID_CODE);
        roof1.setBuildingAddress("Address 1");

        final var roof2 = new Roof();
        roof2.setCode(VALID_CODE);
        roof2.setBuildingAddress("Address 2");

        assertEquals(roof1, roof2);
        assertEquals(roof1.hashCode(), roof2.hashCode());
    }

    @Test
    void testEqualsHashCodeRoofsWithDifferentCodes() {
        final var roof1 = new Roof();
        roof1.setCode("code1");
        roof1.setBuildingAddress(VALID_BUILDING_ADDRESS);

        final var roof2 = new Roof();
        roof2.setCode("code2");
        roof2.setBuildingAddress(VALID_BUILDING_ADDRESS);

        assertNotEquals(roof1, roof2);
        assertNotEquals(roof1.hashCode(), roof2.hashCode());
    }

    @Test
    void testEqualHashCodeBothRoofsWithNullCode() {
        final var roof1 = new Roof();
        roof1.setBuildingAddress(VALID_BUILDING_ADDRESS);
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
    void testCodeValidation() {
        final var roof = createValidRoof();

        final String[] validValues = {
                "validCode",
                "code123",
                "CODE",
                "a",
                "code-with-dashes",
                "123456",
        };

        for (final var value : validValues) {
            roof.setCode(value);
            final var violations = validator.validate(roof);

            assertTrue(
                    violations.isEmpty(),
                    "Code '" + value + "' should be valid but got violations: " + violations
            );
        }

        final String[] invalidValues = {
                null,
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

        for (final String value : invalidValues) {
            roof.setCode(value);
            final var violations = validator.validate(roof);

            assertEquals(
                    1,
                    violations.size(),
                    "Code '" + value + "' should have exactly one validation violation"
            );

            final var violation = violations.iterator().next();
            assertEquals("code", violation.getPropertyPath().toString());
            assertEquals(ValidCode.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }

    @Test
    void testBuildingAddressValidation() {
        final var roof = createValidRoof();

        final String[] validValues = {
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

        for (final String value : validValues) {
            roof.setBuildingAddress(value);
            final var violations = validator.validate(roof);

            assertTrue(
                    violations.isEmpty(),
                    "Building address '" + value + "' should be valid but got violations: " + violations
            );
        }

        final String[] invalidValues = {
                null, "", " ", "  ", "\t", "\n", "\r", "   \t  \n  \r  ",
        };

        for (final String value : invalidValues) {
            roof.setBuildingAddress(value);
            final var violations = validator.validate(roof);

            assertEquals(
                    1,
                    violations.size(),
                    "Building address '" + value + "' should have exactly one validation violation"
            );

            final var violation = violations.iterator().next();
            assertEquals("buildingAddress", violation.getPropertyPath().toString());
            assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }

}
