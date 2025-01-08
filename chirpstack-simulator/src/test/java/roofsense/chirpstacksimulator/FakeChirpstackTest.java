package roofsense.chirpstacksimulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofsense.chirpstacksimulator.mocks.FakeLoRaSensorMock;
import roofsense.chirpstacksimulator.mocks.MqttClientMock;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//CHECKSTYLE: MagicNumber OFF
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
        final var sensors = List.of(
                new FakeLoRaSensorMock.Builder("0000000000000001")
                        .measurementsToEmit(5)
                        .samplingRate(samplingRate)
                        .build(),
                new FakeLoRaSensorMock.Builder("0000000000000002")
                        .measurementsToEmit(3)
                        .samplingRate(samplingRate)
                        .build()
        );
        final var loraNetworkServer = new FakeChirpstack(mqttClient, sensors);

        loraNetworkServer.start();
        assertDoesNotThrow(loraNetworkServer::start);

        loraNetworkServer.await();

        assertFalse(loraNetworkServer.isRunning());
        final var measurementsCount = sensors.stream()
                .map(FakeLoRaSensorMock::getMeasurementsToEmitCount)
                .reduce(0, Integer::sum);
        assertEquals(measurementsCount, mqttClient.getPublishedMessages().size());

        // Testing that every message sent is correctly formed
        for (final var message : mqttClient.getPublishedMessages()) {
            final var topicRegex = Pattern.compile("application/1/device/(\\d{16})/command/down");
            final var topicMatcher = topicRegex.matcher(message.topic());

            assertTrue(topicMatcher.matches());

            final var devEuiFromTopic = topicMatcher.group(1);
            assertTrue(sensors.stream().anyMatch(sensor -> sensor.getDevEui().equals(devEuiFromTopic)));

            final var decodedPayloadString = new String(message.payload(), StandardCharsets.UTF_8);
            final var decodedPayload = assertDoesNotThrow(() -> new ObjectMapper().readTree(decodedPayloadString));
            assertEquals(decodedPayload.get("devEui").asText(), devEuiFromTopic);
            assertTrue(decodedPayload.get("confirmed").asBoolean());
            assertEquals(10, decodedPayload.get("fPort").asInt());
            assertEquals("base64", decodedPayload.get("data").asText());
            assertEquals("json", decodedPayload.get("object").asText());
        }
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
