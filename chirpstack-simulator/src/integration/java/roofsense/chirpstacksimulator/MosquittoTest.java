package roofsense.chirpstacksimulator;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import picocli.CommandLine;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the simulator against a mosquitto server.
 */
public class MosquittoTest {

    private static final Logger LOG = LoggerFactory.getLogger(MosquittoTest.class);

    /**
     * Test the simulator against a mosquitto server.
     */
    @Test
    public void test() {
        final var simulatorMqttClientId = "simulator";
        final var testMqttClientId = "junit-test";
        final var sensorsStateUpdatesRate = Duration.ofSeconds(1);
        final var sensorsStateUpdatesCount = 3;
        final var cooldownTime = Duration.ofMillis(500); // Time to wait to let components running in different threads reach desired state

        try {
            // Starting the mosquitto server container
            @SuppressWarnings("resource") final var container = new GenericContainer<>("eclipse-mosquitto:latest")
                    .withExposedPorts(1883)
                    .withCommand("mosquitto -c /mosquitto-no-auth.conf")
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("eclipse-mosquitto")));
            container.start();

            // Setting up collection of messages sent by the simulator to the MQTT broker
            record MqttMessage(String topic, String payload) {
            }
            final var simulatedDevicesMessages = new ArrayList<MqttMessage>();
            final var mqttServerUri = "tcp://" + container.getHost() + ":" + container.getMappedPort(1883);
            final var mqttClient = new MqttClient(mqttServerUri, testMqttClientId, new MemoryPersistence());
            mqttClient.connect();
            mqttClient.subscribe(
                    "application/" + simulatorMqttClientId + "/device/#", (topic, message) -> {
                        final var payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                        LOG.debug("Received message on topic {}: {}", topic, payload);
                        simulatedDevicesMessages.add(new MqttMessage(topic, payload));
                    }
            );

            // Running the simulator
            final var simulatedDevicesCount = 3;
            final var simulationDuration = sensorsStateUpdatesRate.multipliedBy(sensorsStateUpdatesCount).plus(cooldownTime);
            final var cliArgs = new String[]{
                    "--application-id", simulatorMqttClientId,
                    "-m", mqttServerUri,
                    "--rate", String.valueOf(sensorsStateUpdatesRate.toSeconds()),
                    "--airtemp-sensors", "1",
                    "--extemp-sensors", "1",
                    "--intemp-sensors", "1"
            };
            final var cli = new CommandLine(new CLI());
            final var cliThread = new Thread(() -> cli.execute(cliArgs));
            cliThread.start();
            Thread.sleep(simulationDuration.toMillis());

            // Check received messages correctness
            for (final var message : simulatedDevicesMessages) {
                assertTrue(message.topic().endsWith("command/down"),
                        "Message topic does not end with 'command/down': " + message.topic());
            }
            assertEquals(simulationDuration.dividedBy(sensorsStateUpdatesRate) * simulatedDevicesCount, simulatedDevicesMessages.size());

            mqttClient.disconnect();
            mqttClient.close();
            container.close();
        } catch (final MqttException | InterruptedException e) {
            fail(e);
        }

    }

}
