package roofsense.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementTest {

    private static final double VALID_VALUE = 25.5;

    private static Measurement createValidMeasurement() {
        final var measurement = new Measurement();
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var sensor = new Sensor("SENSOR-001", SensorType.OMEGA_PR_10, LocalDateTime.now(), measurementPoint);
        measurement.setSensor(sensor);
        measurement.setTimestamp(LocalDateTime.now());
        measurement.setMeasurementType(MeasurementType.TEMPERATURE_10CM);
        measurement.setValue(VALID_VALUE);
        measurement.setUnit("°C");
        return measurement;
    }

    @Test
    void testDefaultConstructor() {
        final var measurement = new Measurement();

        assertNull(measurement.getId());
        assertNull(measurement.getSensor());
        assertNull(measurement.getTimestamp());
        assertNull(measurement.getMeasurementType());
        assertNull(measurement.getValue());
        assertNull(measurement.getUnit());
    }

    @Test
    void testConstructorWithParameters() {
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var sensor = new Sensor("SENSOR-001", SensorType.HUKSeflux_HFP01, LocalDateTime.now(), measurementPoint);
        final var timestamp = LocalDateTime.of(2024, 6, 1, 12, 0);
        final var measurement = new Measurement(sensor, timestamp, MeasurementType.HEAT_FLUX, 150.0, "W/m²");

        assertEquals(sensor, measurement.getSensor());
        assertEquals(timestamp, measurement.getTimestamp());
        assertEquals(MeasurementType.HEAT_FLUX, measurement.getMeasurementType());
        assertEquals(150.0, measurement.getValue());
        assertEquals("W/m²", measurement.getUnit());
    }

    @Test
    void testSensor() {
        final var measurement = new Measurement();
        assertNull(measurement.getSensor());

        final var measurementPoint = new MeasurementPoint("MP-002", 44.5, 10.6, Orientation.E,
                new Roof("ROOF-002", "Via Test 456"));
        final var sensor = new Sensor("SENSOR-002", SensorType.DRAGINO_LHT65, LocalDateTime.now(), measurementPoint);
        measurement.setSensor(sensor);
        assertEquals(sensor, measurement.getSensor());
    }

    @Test
    void testTimestamp() {
        final var measurement = new Measurement();
        assertNull(measurement.getTimestamp());

        final var timestamp = LocalDateTime.of(2024, 6, 1, 14, 30);
        measurement.setTimestamp(timestamp);
        assertEquals(timestamp, measurement.getTimestamp());
    }

    @Test
    void testMeasurementType() {
        final var measurement = new Measurement();
        assertNull(measurement.getMeasurementType());

        measurement.setMeasurementType(MeasurementType.TEMPERATURE_INTERNAL_SURFACE);
        assertEquals(MeasurementType.TEMPERATURE_INTERNAL_SURFACE, measurement.getMeasurementType());
    }

    @Test
    void testValue() {
        final var measurement = new Measurement();
        assertNull(measurement.getValue());

        measurement.setValue(30.0);
        assertEquals(30.0, measurement.getValue());
    }

    @Test
    void testUnit() {
        final var measurement = new Measurement();
        assertNull(measurement.getUnit());

        measurement.setUnit("°C");
        assertEquals("°C", measurement.getUnit());
    }

    @Test
    void testEqualsWithNull() {
        final var measurement = createValidMeasurement();
        assertNotEquals(measurement, null);
    }

    @Test
    void testEqualsWithDifferentType() {
        final var measurement = createValidMeasurement();
        assertNotEquals(measurement, new Object());
    }

    @Test
    void testEqualsHashCodeWithSameId() {
        final var measurement1 = createValidMeasurement();
        final var measurement2 = createValidMeasurement();

        // Both have null id, so they should be equal
        assertEquals(measurement1, measurement2);
        assertEquals(measurement1.hashCode(), measurement2.hashCode());
    }

    @Test
    void testToString() {
        final var measurement = createValidMeasurement();

        final var toString = measurement.toString();

        assertTrue(toString.contains("Measurement"));
        assertTrue(toString.contains("timestamp="));
    }
}
