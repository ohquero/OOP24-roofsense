package roofsense.lora.networkserver.simulator;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofsense.lora.networkserver.simulator.fakes.FakeMqttClient;
import roofsense.lora.networkserver.simulator.fakes.FakeSimulatedLoRaSensor;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedLoRaNetworkServerTest {

    private FakeMqttClient mqttClient;

    @BeforeEach
    void setUp() {
        mqttClient = new FakeMqttClient("FakeMqttClient-broker-uri");
    }

    @Test
    void constructor() {
        final var sensors = List.of(
                new FakeSimulatedLoRaSensor.Builder("0000000000000001").measurementsToEmit(5).build()
        );

        assertThrows(NullPointerException.class, () -> new SimulatedLoRaNetworkServer(null, null));
        assertThrows(NullPointerException.class, () -> new SimulatedLoRaNetworkServer(null, sensors));
        assertThrows(NullPointerException.class, () -> new SimulatedLoRaNetworkServer(mqttClient, null));
        assertThrows(IllegalArgumentException.class, () -> new SimulatedLoRaNetworkServer(mqttClient, List.of()));
        assertDoesNotThrow(() -> new SimulatedLoRaNetworkServer(mqttClient, sensors));
    }

    @Test
    void start() throws InterruptedException {
        final var samplingRate = Duration.ofSeconds(1);
        final var sensor1measurementsToEmitCount = 5;
        final var sensor2measurementsToEmitCount = 3;
        final var sensorsMeasurementsToEmitCount = sensor1measurementsToEmitCount + sensor2measurementsToEmitCount;
        final var sensors = List.of(
                new FakeSimulatedLoRaSensor.Builder("0000000000000001")
                        .measurementsToEmit(sensor1measurementsToEmitCount)
                        .samplingRate(samplingRate)
                        .build(),
                new FakeSimulatedLoRaSensor.Builder("0000000000000002")
                        .measurementsToEmit(sensor2measurementsToEmitCount)
                        .samplingRate(samplingRate)
                        .build()
        );
        final var loraNetworkServer = new SimulatedLoRaNetworkServer(mqttClient, sensors);

        loraNetworkServer.start();

        loraNetworkServer.await();

        assertFalse(loraNetworkServer.isRunning());
        assertEquals(sensorsMeasurementsToEmitCount, mqttClient.getPublishedMessages().size());
    }

    @Test
    void start_mqttAlreadyConnected() {
        final var sensors = List.of(new FakeSimulatedLoRaSensor.Builder("0000000000000001").build());
        final var loraNetworkServer = new SimulatedLoRaNetworkServer(mqttClient, sensors);
        mqttClient.connect();

        loraNetworkServer.start();
        assertTrue(loraNetworkServer.isRunning());
    }

    @Test
    void stop() throws InterruptedException {
        final var sensors = List.of(
                new FakeSimulatedLoRaSensor.Builder("0000000000000001")
                        .samplingRate(Duration.ofSeconds(1))
                        .build()
        );
        final var loraNetworkServer = new SimulatedLoRaNetworkServer(mqttClient, sensors);

        assertDoesNotThrow(loraNetworkServer::stop);
        assertDoesNotThrow(loraNetworkServer::stop);

        assertDoesNotThrow(loraNetworkServer::start);
        assertTrue(loraNetworkServer.isRunning());

        //noinspection ResultOfMethodCallIgnored
        Observable.just(1)
                .delay(2, TimeUnit.SECONDS)
                .subscribe((i) -> loraNetworkServer.stop());
        loraNetworkServer.await();

        assertFalse(loraNetworkServer.isRunning());
    }

    @Test
    void isRunning() {
        final var sensors = List.of(new FakeSimulatedLoRaSensor.Builder("0000000000000001").build());
        final var loraNetworkServer = new SimulatedLoRaNetworkServer(mqttClient, sensors);

        assertFalse(loraNetworkServer.isRunning());

        loraNetworkServer.start();

        assertTrue(loraNetworkServer.isRunning());

        loraNetworkServer.stop();

        assertFalse(loraNetworkServer.isRunning());
    }

    @Test
    void await() {
        final var sensorsSamplingRate = Duration.ofSeconds(1);
        final var sensorsMeasurementsToEmitCount = 5;
        final var sensors = List.of(
                new FakeSimulatedLoRaSensor.Builder("0000000000000001")
                        .samplingRate(sensorsSamplingRate)
                        .measurementsToEmit(sensorsMeasurementsToEmitCount)
                        .build()
        );
        final var loraNetworkServer = new SimulatedLoRaNetworkServer(mqttClient, sensors);

        assertDoesNotThrow(loraNetworkServer::await);

        loraNetworkServer.start();

        assertTimeout(
                sensorsSamplingRate.multipliedBy(sensorsMeasurementsToEmitCount).plusSeconds(1),
                loraNetworkServer::await
        );

        loraNetworkServer.stop();

        assertDoesNotThrow(loraNetworkServer::await);

    }

}