package roofsense.lora.networkserver.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedLoRaTemperatureSensorTest {

    public static final String DEVEUI = "0000000000000000";

    @Test
    void builderCreationTest() {
        assertThrows(NullPointerException.class, () -> new SimulatedLoRaTemperatureSensor.Builder(null));
        assertThrows(IllegalArgumentException.class, () -> new SimulatedLoRaTemperatureSensor.Builder(""));
        assertDoesNotThrow(() -> new SimulatedLoRaTemperatureSensor.Builder(DEVEUI));
    }

    @Test
    void buildSensorWithCustomDischargeTimeTest() {
        final var builder = new SimulatedLoRaTemperatureSensor.Builder(DEVEUI);

        assertThrows(NullPointerException.class, () -> builder.dischargeTime(null));
        assertThrows(IllegalArgumentException.class, () -> builder.dischargeTime(Duration.ofSeconds(-1)));
        assertThrows(IllegalArgumentException.class, () -> builder.dischargeTime(Duration.ofSeconds(0)));

        final var correctDischargeTime = Duration.ofSeconds(10);
        final var correctBuilder = assertDoesNotThrow(() -> builder.dischargeTime(correctDischargeTime));

        final var ticksForDischarge = 5;
        final var sensor = correctBuilder.samplingRate(correctDischargeTime.dividedBy(ticksForDischarge)).build();
        assertEquals(correctDischargeTime, sensor.getDischargeTime());

        final var completeDischargeToTestCount = 2;
        final var objectMapper = new ObjectMapper();
        final var batteryLevelValues = sensor.getDataStream()
                .take(ticksForDischarge * completeDischargeToTestCount)
                .map(SimulatedLoRaTemperatureSensor.Data::json)
                .map(json -> objectMapper.readTree(json).get("battery").asDouble())
                .toList()
                .blockingGet();
        assertEquals(ticksForDischarge * completeDischargeToTestCount, batteryLevelValues.size());
        // Check if the battery level decreases linearly
        for (int i = 0; i < batteryLevelValues.size() - 1; i++) {
            assertTrue(
                    batteryLevelValues.get(i) > batteryLevelValues.get(i + 1)
                            || batteryLevelValues.get(i + 1) == 100);
        }
    }

    @Test
    void buildSensorWithCustomBaseTemperatureTest() {
        final var builder = new SimulatedLoRaTemperatureSensor.Builder(DEVEUI);

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
        final var builder = new SimulatedLoRaTemperatureSensor.Builder(DEVEUI);

        assertThrows(NullPointerException.class, () -> builder.dayTemperatureDelta(null));
        assertThrows(IllegalArgumentException.class, () -> builder.dayTemperatureDelta(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.dayTemperatureDelta(0));

        final var correctDayTemperatureDelta = 10;
        final var correctBuilder = assertDoesNotThrow(() -> builder.dayTemperatureDelta(correctDayTemperatureDelta));

        final var sensor = correctBuilder.build();
        assertEquals(correctDayTemperatureDelta, sensor.getDayTemperatureDelta());
    }

}
