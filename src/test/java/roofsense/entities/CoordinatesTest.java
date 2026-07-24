package roofsense.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoordinatesTest {

    private static final double VALID_LATITUDE = 44.14;
    private static final double VALID_LONGITUDE = 12.34;
    private static final Coordinates VALID_COORDINATES = new Coordinates(VALID_LATITUDE, VALID_LONGITUDE);
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

    @Test
    void testEmptyConstructor() {
        final var coordinates = new Coordinates();

        assertNull(coordinates.getLatitude());
        assertNull(coordinates.getLongitude());
    }

    @Test
    void testParametrizedConstructor() {
        final var coordinates = new Coordinates(VALID_LATITUDE, VALID_LONGITUDE);

        assertEquals(VALID_LATITUDE, coordinates.getLatitude());
        assertEquals(VALID_LONGITUDE, coordinates.getLongitude());
    }

    @Test
    void testCopyConstructor() {
        final var original = VALID_COORDINATES;
        final var copy = new Coordinates(original);

        assertEquals(original.getLatitude(), copy.getLatitude());
        assertEquals(original.getLongitude(), copy.getLongitude());
    }

    @Test
    void testCopyConstructorRequiresNonNull() {
        final var exception = assertThrows(NullPointerException.class, () -> new Coordinates(null));
        assertEquals("other must not be null", exception.getMessage());
    }

    @Test
    void testLatitude() {
        final var coordinates = new Coordinates();
        assertNull(coordinates.getLatitude());

        coordinates.setLatitude(VALID_LATITUDE);
        assertEquals(VALID_LATITUDE, coordinates.getLatitude());
    }

    @Test
    void testLongitude() {
        final var coordinates = new Coordinates();
        assertNull(coordinates.getLongitude());

        coordinates.setLongitude(VALID_LONGITUDE);
        assertEquals(VALID_LONGITUDE, coordinates.getLongitude());
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(VALID_COORDINATES, null);
    }

    @Test
    void testEqualsWithDifferentType() {
        assertNotEquals(VALID_COORDINATES, new Object());
    }

    @Test
    void testEqualsHashCodeWithSameValues() {
        final var coords1 = new Coordinates(VALID_LATITUDE, VALID_LONGITUDE);
        final var coords2 = new Coordinates(VALID_LATITUDE, VALID_LONGITUDE);

        assertEquals(coords1, coords2);
        assertEquals(coords1.hashCode(), coords2.hashCode());
    }

    @Test
    void testEqualsHashCodeWithDifferentValues() {
        final var coords1 = new Coordinates(VALID_LATITUDE, VALID_LONGITUDE);
        final var coords2 = new Coordinates(VALID_LATITUDE + 1, VALID_LONGITUDE + 1);

        assertNotEquals(coords1, coords2);
        assertNotEquals(coords1.hashCode(), coords2.hashCode());
    }

    @Test
    void testValidCoordinates() {
        final var violations = validator.validate(VALID_COORDINATES);

        assertTrue(violations.isEmpty(), "Valid coordinates should have no violations but got: " + violations);
    }

    @Test
    void testInvalidLatitudeValues() {
        final Double[] invalidLatitudes = {null, -91.0, 91.0, -100.0, 100.0};

        for (final var lat : invalidLatitudes) {
            final var coordinates = new Coordinates(lat, VALID_LONGITUDE);
            final var violations = validator.validate(coordinates);

            assertFalse(violations.isEmpty(), "Latitude " + lat + " should be invalid");
        }
    }

    @Test
    void testInvalidLongitudeValues() {
        final Double[] invalidLongitudes = {null, -181.0, 181.0, -200.0, 200.0};

        for (final var lng : invalidLongitudes) {
            final var coordinates = new Coordinates(VALID_LATITUDE, lng);
            final var violations = validator.validate(coordinates);

            assertFalse(violations.isEmpty(), "Longitude " + lng + " should be invalid");
        }
    }

    @Test
    void testBoundaryCoordinates() {
        final double[][] boundaryCoords = {
                {-90.0, -180.0}, {90.0, 180.0}, {0.0, 0.0}, {-90.0, 180.0}, {90.0, -180.0},
        };

        for (final double[] coords : boundaryCoords) {
            final var coordinates = new Coordinates(coords[0], coords[1]);
            final var violations = validator.validate(coordinates);

            assertTrue(
                    violations.isEmpty(),
                    "Coordinates [" + coords[0] + ", " + coords[1] + "] should be valid" + " but got: " + violations
            );
        }
    }

}
