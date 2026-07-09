package roofsense.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SensorTest {

    private static final String VALID_SENSOR_CODE = "SENSOR-001";

    private static Sensor createValidSensor() {
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var sensor = new Sensor();
        sensor.setSensorCode(VALID_SENSOR_CODE);
        sensor.setSensorType(SensorType.OMEGA_PR_10);
        sensor.setInstallationDate(LocalDateTime.now());
        sensor.setMeasurementPoint(measurementPoint);
        return sensor;
    }

    @Test
    void testDefaultConstructor() {
        final var sensor = new Sensor();

        assertNull(sensor.getId());
        assertNull(sensor.getSensorCode());
        assertNull(sensor.getSensorType());
        assertNull(sensor.getInstallationDate());
        assertNull(sensor.getRemovalDate());
        assertNull(sensor.getMeasurementPoint());
    }

    @Test
    void testConstructorWithParameters() {
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var installationDate = LocalDateTime.of(2024, 1, 1, 10, 0);
        final var sensor = new Sensor(VALID_SENSOR_CODE, SensorType.HUKSeflux_HFP01, installationDate, measurementPoint);

        assertEquals(VALID_SENSOR_CODE, sensor.getSensorCode());
        assertEquals(SensorType.HUKSeflux_HFP01, sensor.getSensorType());
        assertEquals(installationDate, sensor.getInstallationDate());
        assertNull(sensor.getRemovalDate());
        assertEquals(measurementPoint, sensor.getMeasurementPoint());
        assertTrue(sensor.isActive());
    }

    @Test
    void testSensorCode() {
        final var sensor = new Sensor();
        assertNull(sensor.getSensorCode());

        final var anotherCode = "SENSOR-002";
        sensor.setSensorCode(anotherCode);
        assertEquals(anotherCode, sensor.getSensorCode());
    }

    @Test
    void testSensorType() {
        final var sensor = new Sensor();
        assertNull(sensor.getSensorType());

        sensor.setSensorType(SensorType.DRAGINO_LHT65);
        assertEquals(SensorType.DRAGINO_LHT65, sensor.getSensorType());
    }

    @Test
    void testInstallationDate() {
        final var sensor = new Sensor();
        assertNull(sensor.getInstallationDate());

        final var date = LocalDateTime.of(2024, 6, 1, 14, 30);
        sensor.setInstallationDate(date);
        assertEquals(date, sensor.getInstallationDate());
    }

    @Test
    void testRemovalDate() {
        final var sensor = new Sensor();
        assertNull(sensor.getRemovalDate());

        final var date = LocalDateTime.of(2024, 12, 1, 14, 30);
        sensor.setRemovalDate(date);
        assertEquals(date, sensor.getRemovalDate());
        assertFalse(sensor.isActive());
    }

    @Test
    void testIsActive() {
        final var sensor = createValidSensor();
        assertTrue(sensor.isActive());

        sensor.setRemovalDate(LocalDateTime.now());
        assertFalse(sensor.isActive());
    }

    @Test
    void testMeasurementPoint() {
        final var sensor = new Sensor();
        assertNull(sensor.getMeasurementPoint());

        final var measurementPoint = new MeasurementPoint("MP-002", 44.5, 10.6, Orientation.E,
                new Roof("ROOF-002", "Via Test 456"));
        sensor.setMeasurementPoint(measurementPoint);
        assertEquals(measurementPoint, sensor.getMeasurementPoint());
    }

    @Test
    void testEqualsWithNull() {
        final var sensor = createValidSensor();
        assertNotEquals(sensor, null);
    }

    @Test
    void testEqualsWithDifferentType() {
        final var sensor = createValidSensor();
        assertNotEquals(sensor, new Object());
    }

    @Test
    void testEqualsHashCodeWithSameCode() {
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var sensor1 = new Sensor("SENSOR-001", SensorType.OMEGA_PR_10, LocalDateTime.now(), measurementPoint);
        final var sensor2 = new Sensor("SENSOR-001", SensorType.HUKSeflux_HFP01, LocalDateTime.now(), measurementPoint);

        assertEquals(sensor1, sensor2);
        assertEquals(sensor1.hashCode(), sensor2.hashCode());
    }

    @Test
    void testEqualsHashCodeWithDifferentCodes() {
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var sensor1 = new Sensor("SENSOR-001", SensorType.OMEGA_PR_10, LocalDateTime.now(), measurementPoint);
        final var sensor2 = new Sensor("SENSOR-002", SensorType.OMEGA_PR_10, LocalDateTime.now(), measurementPoint);

        assertNotEquals(sensor1, sensor2);
        assertNotEquals(sensor1.hashCode(), sensor2.hashCode());
    }

    @Test
    void testToString() {
        final var sensor = createValidSensor();

        final var toString = sensor.toString();

        assertTrue(toString.contains("Sensor"));
        assertTrue(toString.contains(VALID_SENSOR_CODE));
    }
}
