package roofsense.lora.networkserver.simulator;

import org.junit.jupiter.api.Test;
import roofsense.lora.networkserver.simulator.fakes.SimulatedLoRaSensorMock;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimulatedLoRaSensorTest {

    @Test
    void builderConstructorTest() {
        assertThrows(NullPointerException.class, () -> new SimulatedLoRaSensorMock.Builder(null));
        assertThrows(IllegalArgumentException.class, () -> new SimulatedLoRaSensorMock.Builder(""));
        assertThrows(IllegalArgumentException.class, () -> new SimulatedLoRaSensorMock.Builder("wrong-size-deui"));

        final var devEui = "0000000000000001";

        final var sensor = assertDoesNotThrow(() -> new SimulatedLoRaSensorMock.Builder(devEui).build());
        assertEquals(devEui, sensor.getDevEui());
    }

    @Test
    void buildSensorWithCustomSamplingRateTest() {
        final var devEui = "0000000000000001";

        final var builder = new SimulatedLoRaSensorMock.Builder(devEui);
        assertThrows(NullPointerException.class, () -> builder.samplingRate(null));
        assertThrows(IllegalArgumentException.class, () -> builder.samplingRate(Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class, () -> builder.samplingRate(Duration.ofSeconds(-1)));

        final var correctSamplingRate = Duration.ofSeconds(1);
        final var sensor = assertDoesNotThrow(() -> builder.samplingRate(correctSamplingRate).build());
        assertEquals(correctSamplingRate, sensor.getSamplingRate());
    }

}
