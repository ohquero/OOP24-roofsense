package roofsense.lora.networkserver.simulator;

import org.junit.jupiter.api.Test;
import roofsense.lora.networkserver.simulator.fakes.FakeSimulatedLoRaSensor;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedLoRaSensorTest {

    @Test
    void builder() {
        assertThrows(NullPointerException.class, () -> new FakeSimulatedLoRaSensor.Builder(null));
        assertThrows(IllegalArgumentException.class, () -> new FakeSimulatedLoRaSensor.Builder(""));

        final var devEui = "0000000000000001";

        assertDoesNotThrow(() -> new FakeSimulatedLoRaSensor.Builder(devEui).build());
    }

    @Test
    void getDevEui() {
        String devEui = "0000000000000001";
        final var sensor = new FakeSimulatedLoRaSensor.Builder(devEui).build();

        assertEquals(devEui, sensor.getDevEui());
    }

    @Test
    void getDataStream() {
        final var devEui = "0000000000000001";
        final var sensor = new FakeSimulatedLoRaSensor.Builder(devEui).build();

        assertNotNull(sensor.getDataStream());

        final var sensorData = sensor.getDataStream().blockingFirst();

        assertEquals(devEui, sensorData.devEui());
        assertEquals("base64", sensorData.measurement().base64());
        assertEquals("json", sensorData.measurement().json());
    }

}