package roofsense.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import roofsense.entities.validation.annotations.ValidCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementPointTest {

    private static final String VALID_CODE = "MP-001";
    private static final String VALID_BUILDING_ADDRESS = "Test Address";
    private static final double VALID_LATITUDE = 44.4949;
    private static final double VALID_LONGITUDE = 10.6333;
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

    private static Roof createRoof(final String code) {
        final var roof = new Roof();
        roof.setCode(code);
        roof.setBuildingAddress(VALID_BUILDING_ADDRESS);
        return roof;
    }

    private static MeasurementPoint createValidMeasurementPoint() {
        return new MeasurementPoint(
                VALID_CODE,
                createRoof("ROOF-001"),
                VALID_LATITUDE,
                VALID_LONGITUDE,
                Orientation.N
        );
    }

    @Test
    void testEmptyConstructor() {
        final var measurementPoint = new MeasurementPoint();

        assertNull(measurementPoint.getCode());
        assertNull(measurementPoint.getLatitude());
        assertNull(measurementPoint.getLongitude());
        assertNull(measurementPoint.getOrientation());
        assertNull(measurementPoint.getRoof());
    }

    @Test
    void testParametrizedConstructor() {
        final var roof = createRoof("ROOF-002");
        final var measurementPoint =
                new MeasurementPoint(VALID_CODE, roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.S);

        assertEquals(0, validator.validate(measurementPoint).size());
        assertEquals(VALID_CODE, measurementPoint.getCode());
        assertEquals(VALID_LATITUDE, measurementPoint.getLatitude());
        assertEquals(VALID_LONGITUDE, measurementPoint.getLongitude());
        assertEquals(Orientation.S, measurementPoint.getOrientation());
        assertEquals(roof, measurementPoint.getRoof());
    }

    @Test
    void testCopyConstructor() {
        final var original = createValidMeasurementPoint();
        final var copy = new MeasurementPoint(original);

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getCode(), copy.getCode());
        assertEquals(original.getLatitude(), copy.getLatitude());
        assertEquals(original.getLongitude(), copy.getLongitude());
        assertEquals(original.getOrientation(), copy.getOrientation());
        assertEquals(original.getRoof(), copy.getRoof());
    }

    @Test
    void testCode() {
        final var measurementPoint = new MeasurementPoint();
        assertNull(measurementPoint.getCode());

        final var anotherCode = "MP-002";
        measurementPoint.setCode(anotherCode);
        assertEquals(anotherCode, measurementPoint.getCode());
    }

    @Test
    void testLatitude() {
        final var measurementPoint = new MeasurementPoint();
        assertNull(measurementPoint.getLatitude());

        final var latitude = 45.0;
        measurementPoint.setLatitude(latitude);
        assertEquals(latitude, measurementPoint.getLatitude());
    }

    @Test
    void testLongitude() {
        final var measurementPoint = new MeasurementPoint();
        assertNull(measurementPoint.getLongitude());

        final var longitude = 11.0;
        measurementPoint.setLongitude(longitude);
        assertEquals(longitude, measurementPoint.getLongitude());
    }

    @Test
    void testOrientation() {
        final var measurementPoint = new MeasurementPoint();
        assertNull(measurementPoint.getOrientation());

        measurementPoint.setOrientation(Orientation.NE);
        assertEquals(Orientation.NE, measurementPoint.getOrientation());
    }

    @Test
    void testRoof() {
        final var measurementPoint = new MeasurementPoint();
        assertNull(measurementPoint.getRoof());

        final var roof = createRoof("ROOF-003");
        measurementPoint.setRoof(roof);
        assertEquals(roof, measurementPoint.getRoof());
    }

    @Test
    void testEqualsWithNull() {
        final var measurementPoint = createValidMeasurementPoint();
        assertNotEquals(measurementPoint, null);
    }

    @Test
    void testEqualsWithDifferentType() {
        final var measurementPoint = createValidMeasurementPoint();
        assertNotEquals(measurementPoint, new Object());
    }

    @Test
    void testEqualsHashCodeWithSameCode() {
        final var roof = createRoof("ROOF-004");
        final var mp1 = new MeasurementPoint(VALID_CODE, roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.N);
        final var mp2 = new MeasurementPoint(VALID_CODE, roof, 50.0, 20.0, Orientation.S);

        assertEquals(mp1, mp2);
        assertEquals(mp1.hashCode(), mp2.hashCode());
    }

    @Test
    void testEqualsHashCodeWithDifferentCodes() {
        final var roof = createRoof("ROOF-005");
        final var mp1 = new MeasurementPoint("MP-001", roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.N);
        final var mp2 = new MeasurementPoint("MP-002", roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.N);

        assertNotEquals(mp1, mp2);
        assertNotEquals(mp1.hashCode(), mp2.hashCode());
    }

    @Test
    void testToString() {
        final var measurementPoint = createValidMeasurementPoint();

        final var toString = measurementPoint.toString();

        assertTrue(toString.contains("MeasurementPoint"));
        assertTrue(toString.contains(VALID_CODE));
    }

    @Test
    void testValidMeasurementPoint() {
        final var measurementPoint = createValidMeasurementPoint();

        final var violations = validator.validate(measurementPoint);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testCodeValidation() {
        final var measurementPoint = createValidMeasurementPoint();

        for (final var value : CodeTestValues.VALID) {
            measurementPoint.setCode(value);
            final var violations = validator.validate(measurementPoint);

            assertTrue(
                    violations.isEmpty(),
                    "Code '" + value + "' should be valid but got violations: " + violations
            );
        }

        for (final String value : CodeTestValues.INVALID) {
            measurementPoint.setCode(value);
            final var violations = validator.validate(measurementPoint);

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
    void testLatitudeValidation() {
        final var measurementPoint = createValidMeasurementPoint();
        measurementPoint.setLatitude(null);

        final var violations = validator.validate(measurementPoint);

        assertEquals(1, violations.size());
        final var violation = violations.iterator().next();
        assertEquals("latitude", violation.getPropertyPath().toString());
        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testLongitudeValidation() {
        final var measurementPoint = createValidMeasurementPoint();
        measurementPoint.setLongitude(null);

        final var violations = validator.validate(measurementPoint);

        assertEquals(1, violations.size());
        final var violation = violations.iterator().next();
        assertEquals("longitude", violation.getPropertyPath().toString());
        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testOrientationValidation() {
        final var measurementPoint = createValidMeasurementPoint();
        measurementPoint.setOrientation(null);

        final var violations = validator.validate(measurementPoint);

        assertEquals(1, violations.size());
        final var violation = violations.iterator().next();
        assertEquals("orientation", violation.getPropertyPath().toString());
        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    }

}
