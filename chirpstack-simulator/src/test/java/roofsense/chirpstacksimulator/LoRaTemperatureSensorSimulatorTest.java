package roofsense.chirpstacksimulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoRaTemperatureSensorSimulatorTest {

    public static final String DEVEUI = "0000000000000000";

    @Test
    void builderCreationTest() {
        assertThrows(NullPointerException.class, () -> new LoRaTemperatureSensorSimulator.Builder(null));
        assertThrows(IllegalArgumentException.class, () -> new LoRaTemperatureSensorSimulator.Builder(""));
        assertDoesNotThrow(() -> new LoRaTemperatureSensorSimulator.Builder(DEVEUI));
    }

    @Test
    void buildSensorWithCustomDischargeTimeTest() {
        final var builder = new LoRaTemperatureSensorSimulator.Builder(DEVEUI);

        // Ensuring that builder method does not accept illegal arguments
        assertThrows(NullPointerException.class, () -> builder.dischargeTime(null));
        assertThrows(IllegalArgumentException.class, () -> builder.dischargeTime(Duration.ofSeconds(-1)));
        assertThrows(IllegalArgumentException.class, () -> builder.dischargeTime(Duration.ofSeconds(0)));

        // Creating a sensor with a custom discharge time and sampling rate tuned accordingly in order to check
        // the battery level multiple times during the discharge
        final var dischargeTime = Duration.ofNanos(6);
        final var samplingRate = Duration.ofNanos(1);
        final var sensor = builder.dischargeTime(dischargeTime).samplingRate(samplingRate).build();
        assertEquals(dischargeTime, sensor.getDischargeTime());

        // Ensuring that the sensor discharges the battery in the expected time
        final var jsonParser = new ObjectMapper();
        final var batteryLevelValuesToValidate = dischargeTime.dividedBy(sensor.getSamplingRate()) + 1;
        final var batteryLevelValues = sensor.getDataStream()
                .take(batteryLevelValuesToValidate)
                .map(LoRaTemperatureSensorSimulator.Data::json)
                .map(json -> jsonParser.readTree(json).get("battery").asDouble())
                .toList()
                .blockingGet();
        for (int i = 0; i < batteryLevelValues.size() - 2; i++) {
            assertTrue(batteryLevelValues.get(i) > batteryLevelValues.get(i + 1));
        }
        assertEquals(100, batteryLevelValues.get(batteryLevelValues.size() - 1));
    }

    @Test
    void buildSensorWithCustomBaseTemperatureTest() {
        final var builder = new LoRaTemperatureSensorSimulator.Builder(DEVEUI);

        assertThrows(NullPointerException.class, () -> builder.baselineTemperature(null));
        assertThrows(IllegalArgumentException.class, () -> builder.baselineTemperature(-1));
        assertDoesNotThrow(() -> builder.baselineTemperature(0));

        final var correctBaseTemperature = 10;
        final var correctBuilder = assertDoesNotThrow(() -> builder.baselineTemperature(correctBaseTemperature));

        final var sensor = correctBuilder.build();
        assertEquals(correctBaseTemperature, sensor.getBaseTemperature());
    }

    @Test
    void buildSensorWithCustomDayTemperatureDeltaTest() {
        final var builder = new LoRaTemperatureSensorSimulator.Builder(DEVEUI);

        assertThrows(NullPointerException.class, () -> builder.dayTemperatureDelta(null));
        assertThrows(IllegalArgumentException.class, () -> builder.dayTemperatureDelta(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.dayTemperatureDelta(0));

        final var correctDayTemperatureDelta = 10;
        final var correctBuilder = assertDoesNotThrow(() -> builder.dayTemperatureDelta(correctDayTemperatureDelta));

        final var sensor = correctBuilder.build();
        assertEquals(correctDayTemperatureDelta, sensor.getDayTemperatureDelta());
    }

}
