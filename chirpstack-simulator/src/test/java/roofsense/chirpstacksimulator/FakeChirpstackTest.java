package roofsense.chirpstacksimulator;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofsense.chirpstacksimulator.mocks.FakeLoRaSensorMock;
import roofsense.chirpstacksimulator.mocks.MqttClientMock;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class FakeChirpstackTest {

    private MqttClientMock mqttClient;

    @BeforeEach
    void setUp() {
        mqttClient = new MqttClientMock("MqttClientMock-broker-uri");
    }

    @Test
    void constructor() {
        final var sensors = List.of(
                new FakeLoRaSensorMock.Builder("0000000000000001").build()
        );

        assertThrows(NullPointerException.class, () -> new FakeChirpstack(null, null));
        assertThrows(NullPointerException.class, () -> new FakeChirpstack(null, sensors));
        assertThrows(NullPointerException.class, () -> new FakeChirpstack(mqttClient, null));
        assertThrows(IllegalArgumentException.class, () -> new FakeChirpstack(mqttClient, List.of()));
        assertDoesNotThrow(() -> new FakeChirpstack(mqttClient, sensors));
    }

    @Test
    void start() throws InterruptedException, MqttException {
        final var samplingRate = Duration.ofSeconds(1);
        final var sensor1measurementsToEmitCount = 5;
        final var sensor2measurementsToEmitCount = 3;
        final var sensorsMeasurementsToEmitCount = sensor1measurementsToEmitCount + sensor2measurementsToEmitCount;
        final var sensors = List.of(
                new FakeLoRaSensorMock.Builder("0000000000000001")
                        .measurementsToEmit(sensor1measurementsToEmitCount)
                        .samplingRate(samplingRate)
                        .build(),
                new FakeLoRaSensorMock.Builder("0000000000000002")
                        .measurementsToEmit(sensor2measurementsToEmitCount)
                        .samplingRate(samplingRate)
                        .build()
        );
        final var loraNetworkServer = new FakeChirpstack(mqttClient, sensors);

        loraNetworkServer.start();
        assertDoesNotThrow(loraNetworkServer::start);

        loraNetworkServer.await();

        assertFalse(loraNetworkServer.isRunning());
        assertEquals(sensorsMeasurementsToEmitCount, mqttClient.getPublishedMessages().size());
    }

    @Test
    void startWithMqttClientAlreadyConnected() throws MqttException {
        final var sensors = List.of(new FakeLoRaSensorMock.Builder("0000000000000001").build());
        final var loraNetworkServer = new FakeChirpstack(mqttClient, sensors);
        mqttClient.connect();

        loraNetworkServer.start();
        assertTrue(loraNetworkServer.isRunning());
    }

    @Test
    void stop() throws InterruptedException {
        final var sensors = List.of(
                new FakeLoRaSensorMock.Builder("0000000000000001")
                        .samplingRate(Duration.ofSeconds(1))
                        .build()
        );
        final var loraNetworkServer = new FakeChirpstack(mqttClient, sensors);

        assertDoesNotThrow(loraNetworkServer::stop);
        assertDoesNotThrow(loraNetworkServer::stop);

        assertDoesNotThrow(loraNetworkServer::start);
        assertTrue(loraNetworkServer.isRunning());

        new Thread(() -> {
            try {
                // CHECKSTYLE: MagicNumber OFF
                Thread.sleep(2000);
                // CHECKSTYLE: MagicNumber ON
                loraNetworkServer.stop();
            } catch (final InterruptedException e) {
                fail();
            }
        }).start();
        loraNetworkServer.await();

        assertFalse(loraNetworkServer.isRunning());
    }

    @Test
    void isRunning() throws MqttException {
        final var sensors = List.of(new FakeLoRaSensorMock.Builder("0000000000000001").build());
        final var loraNetworkServer = new FakeChirpstack(mqttClient, sensors);

        assertFalse(loraNetworkServer.isRunning());

        loraNetworkServer.start();

        assertTrue(loraNetworkServer.isRunning());

        loraNetworkServer.stop();

        assertFalse(loraNetworkServer.isRunning());
    }

    @Test
    void await() throws MqttException {
        final var sensorsSamplingRate = Duration.ofSeconds(1);
        final var sensorsMeasurementsToEmitCount = 5;
        final var sensors = List.of(
                new FakeLoRaSensorMock.Builder("0000000000000001")
                        .samplingRate(sensorsSamplingRate)
                        .measurementsToEmit(sensorsMeasurementsToEmitCount)
                        .build()
        );
        final var loraNetworkServer = new FakeChirpstack(mqttClient, sensors);

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
