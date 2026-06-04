package roofsense.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementPointTest {

    private static final String VALID_CODE = "MP-001";
    private static final double VALID_LATITUDE = 44.4949;
    private static final double VALID_LONGITUDE = 10.6333;
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static MeasurementPoint createValidMeasurementPoint() {
        final var roof = new Roof("ROOF-001", "Via Test 1");
        final var measurementPoint = new MeasurementPoint();
        measurementPoint.setCode(VALID_CODE);
        measurementPoint.setLatitude(VALID_LATITUDE);
        measurementPoint.setLongitude(VALID_LONGITUDE);
        measurementPoint.setOrientation(Orientation.N);
        measurementPoint.setRoof(roof);
        return measurementPoint;
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
    void testDefaultConstructor() {
        final var measurementPoint = new MeasurementPoint();

        assertNull(measurementPoint.getId());
        assertNull(measurementPoint.getCode());
        assertNull(measurementPoint.getLatitude());
        assertNull(measurementPoint.getLongitude());
        assertNull(measurementPoint.getOrientation());
        assertNull(measurementPoint.getRoof());
    }

    @Test
    void testConstructorWithParameters() {
        final var roof = new Roof("ROOF-002", "Via Test 2");
        final var measurementPoint =
                new MeasurementPoint(VALID_CODE, roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.S);

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

        final var roof = new Roof("ROOF-003", "Via Test 3");
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
        final var roof = new Roof("ROOF-004", "Via Test 4");
        final var mp1 = new MeasurementPoint(VALID_CODE, roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.N);
        final var mp2 = new MeasurementPoint(VALID_CODE, roof, 50.0, 20.0, Orientation.S);

        assertEquals(mp1, mp2);
        assertEquals(mp1.hashCode(), mp2.hashCode());
    }

    @Test
    void testEqualsHashCodeWithDifferentCodes() {
        final var roof = new Roof("ROOF-005", "Via Test 5");
        final var mp1 = new MeasurementPoint("MP-001", roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.N);
        final var mp2 = new MeasurementPoint("MP-002", roof, VALID_LATITUDE, VALID_LONGITUDE, Orientation.N);

        assertNotEquals(mp1, mp2);
        assertNotEquals(mp1.hashCode(), mp2.hashCode());
    }

    @Test
    void testValidMeasurementPoint() {
        final var measurementPoint = createValidMeasurementPoint();

        final var violations = validator.validate(measurementPoint);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testCodeValidationWithNullCode() {
        final var measurementPoint = createValidMeasurementPoint();
        measurementPoint.setCode(null);

        final var violations = validator.validate(measurementPoint);

        assertEquals(1, violations.size());
    }

    @Test
    void testCodeValidationWithInvalidCode() {
        final var measurementPoint = createValidMeasurementPoint();
        measurementPoint.setCode("invalid code with spaces");

        final var violations = validator.validate(measurementPoint);

        assertEquals(1, violations.size());
    }

    @Test
    void testToString() {
        final var measurementPoint = createValidMeasurementPoint();

        final var toString = measurementPoint.toString();

        assertTrue(toString.contains("MeasurementPoint"));
        assertTrue(toString.contains(VALID_CODE));
    }

}
