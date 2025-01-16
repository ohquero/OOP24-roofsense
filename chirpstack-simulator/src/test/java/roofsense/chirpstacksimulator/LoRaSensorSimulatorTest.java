package roofsense.chirpstacksimulator;

import org.junit.jupiter.api.Test;
import roofsense.chirpstacksimulator.mocks.LoRaSensorSimulatorMock;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoRaSensorSimulatorTest {

    @Test
    void builderConstructorTest() {
        assertThrows(NullPointerException.class, () -> new LoRaSensorSimulatorMock.Builder(null));
        assertThrows(IllegalArgumentException.class, () -> new LoRaSensorSimulatorMock.Builder(""));
        assertThrows(IllegalArgumentException.class, () -> new LoRaSensorSimulatorMock.Builder("wrong-size-deui"));

        final var devEui = "0000000000000001";

        final var sensor = assertDoesNotThrow(() -> new LoRaSensorSimulatorMock.Builder(devEui).build());
        assertEquals(devEui, sensor.getDevEui());
    }

    @Test
    void buildSensorWithCustomSamplingRateTest() {
        final var devEui = "0000000000000001";

        final var builder = new LoRaSensorSimulatorMock.Builder(devEui);
        assertThrows(NullPointerException.class, () -> builder.samplingRate(null));
        assertThrows(IllegalArgumentException.class, () -> builder.samplingRate(Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class, () -> builder.samplingRate(Duration.ofSeconds(-1)));

        final var correctSamplingRate = Duration.ofSeconds(1);
        final var sensor = assertDoesNotThrow(() -> builder.samplingRate(correctSamplingRate).build());
        assertEquals(correctSamplingRate, sensor.getSamplingRate());
    }

}
