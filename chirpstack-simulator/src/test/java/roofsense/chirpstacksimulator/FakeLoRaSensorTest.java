package roofsense.chirpstacksimulator;

import org.junit.jupiter.api.Test;
import roofsense.chirpstacksimulator.mocks.FakeLoRaSensorMock;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FakeLoRaSensorTest {

    @Test
    void builderConstructorTest() {
        assertThrows(NullPointerException.class, () -> new FakeLoRaSensorMock.Builder(null));
        assertThrows(IllegalArgumentException.class, () -> new FakeLoRaSensorMock.Builder(""));
        assertThrows(IllegalArgumentException.class, () -> new FakeLoRaSensorMock.Builder("wrong-size-deui"));

        final var devEui = "0000000000000001";

        final var sensor = assertDoesNotThrow(() -> new FakeLoRaSensorMock.Builder(devEui).build());
        assertEquals(devEui, sensor.getDevEui());
    }

    @Test
    void buildSensorWithCustomSamplingRateTest() {
        final var devEui = "0000000000000001";

        final var builder = new FakeLoRaSensorMock.Builder(devEui);
        assertThrows(NullPointerException.class, () -> builder.samplingRate(null));
        assertThrows(IllegalArgumentException.class, () -> builder.samplingRate(Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class, () -> builder.samplingRate(Duration.ofSeconds(-1)));

        final var correctSamplingRate = Duration.ofSeconds(1);
        final var sensor = assertDoesNotThrow(() -> builder.samplingRate(correctSamplingRate).build());
        assertEquals(correctSamplingRate, sensor.getSamplingRate());
    }

}
