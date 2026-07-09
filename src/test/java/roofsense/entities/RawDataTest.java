package roofsense.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RawDataTest {

    private static RawData createValidRawData() {
        final var rawData = new RawData();
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var sensor = new Sensor("SENSOR-001", SensorType.OMEGA_PR_10, LocalDateTime.now(), measurementPoint);
        rawData.setSensor(sensor);
        rawData.setTimestamp(LocalDateTime.now());
        rawData.setSource(DataSource.LASTEM_CSV);
        rawData.setRawPayload("raw,payload,data");
        return rawData;
    }

    @Test
    void testDefaultConstructor() {
        final var rawData = new RawData();

        assertNull(rawData.getId());
        assertNull(rawData.getSensor());
        assertNull(rawData.getTimestamp());
        assertNull(rawData.getSource());
        assertNull(rawData.getRawPayload());
    }

    @Test
    void testConstructorWithParameters() {
        final var measurementPoint = new MeasurementPoint("MP-001", 44.4949, 10.6333, Orientation.N,
                new Roof("ROOF-001", "Via Test 123"));
        final var sensor = new Sensor("SENSOR-001", SensorType.OMEGA_PR_10, LocalDateTime.now(), measurementPoint);
        final var timestamp = LocalDateTime.of(2024, 6, 1, 12, 0);
        final var rawData = new RawData(sensor, timestamp, DataSource.LORA_MQTT, "{\"temp\": 25.5}");

        assertEquals(sensor, rawData.getSensor());
        assertEquals(timestamp, rawData.getTimestamp());
        assertEquals(DataSource.LORA_MQTT, rawData.getSource());
        assertEquals("{\"temp\": 25.5}", rawData.getRawPayload());
    }

    @Test
    void testSensor() {
        final var rawData = new RawData();
        assertNull(rawData.getSensor());

        final var measurementPoint = new MeasurementPoint("MP-002", 44.5, 10.6, Orientation.E,
                new Roof("ROOF-002", "Via Test 456"));
        final var sensor = new Sensor("SENSOR-002", SensorType.DRAGINO_LHT65, LocalDateTime.now(), measurementPoint);
        rawData.setSensor(sensor);
        assertEquals(sensor, rawData.getSensor());
    }

    @Test
    void testTimestamp() {
        final var rawData = new RawData();
        assertNull(rawData.getTimestamp());

        final var timestamp = LocalDateTime.of(2024, 6, 1, 14, 30);
        rawData.setTimestamp(timestamp);
        assertEquals(timestamp, rawData.getTimestamp());
    }

    @Test
    void testSource() {
        final var rawData = new RawData();
        assertNull(rawData.getSource());

        rawData.setSource(DataSource.LORA_MQTT);
        assertEquals(DataSource.LORA_MQTT, rawData.getSource());
    }

    @Test
    void testRawPayload() {
        final var rawData = new RawData();
        assertNull(rawData.getRawPayload());

        final var payload = "some,raw,csv,data";
        rawData.setRawPayload(payload);
        assertEquals(payload, rawData.getRawPayload());
    }

    @Test
    void testEqualsWithNull() {
        final var rawData = createValidRawData();
        assertNotEquals(rawData, null);
    }

    @Test
    void testEqualsWithDifferentType() {
        final var rawData = createValidRawData();
        assertNotEquals(rawData, new Object());
    }

    @Test
    void testEqualsHashCodeWithSameId() {
        final var rawData1 = createValidRawData();
        final var rawData2 = createValidRawData();

        // Both have null id, so they should be equal
        assertEquals(rawData1, rawData2);
        assertEquals(rawData1.hashCode(), rawData2.hashCode());
    }

    @Test
    void testToString() {
        final var rawData = createValidRawData();

        final var toString = rawData.toString();

        assertTrue(toString.contains("RawData"));
        assertTrue(toString.contains("timestamp="));
    }
}
