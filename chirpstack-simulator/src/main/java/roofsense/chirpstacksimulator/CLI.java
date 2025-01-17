package roofsense.chirpstacksimulator;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * The Command Line Interface (CLI) for this application.
 */
public final class CLI implements Runnable {

    private static final String MQTT_CLIENT_ID = "chirpstack-simulator";
    private static final Logger LOG = LoggerFactory.getLogger(CLI.class);

    private final MemoryPersistence persistence = new MemoryPersistence();

    @Option(names = {"-m", "--mqtt-server"}, defaultValue = "tcp://localhost:1883", description = "MQTT server URI")
    private String mqttServerURI;

    @Option(names = {"--application-id"}, defaultValue = MQTT_CLIENT_ID, description = "LoRa sensors application ID")
    private String loraApplicationID;

    @Option(
            names = {"-r", "--rate"},
            defaultValue = "5",
            description = "Rate at which each sensor emits measurements (in seconds)"
    )
    private Integer sensorsSamplingRateSeconds;

    @Option(
            names = {"--airtemp-sensors"},
            defaultValue = "1",
            description = "Number of air temperature sensors to simulate"
    )
    private Integer airTemperatureSensorsCount;

    @Option(
            names = {"--extemp-sensors"},
            defaultValue = "1",
            description = "Number of external temperature sensors to simulate"
    )
    private Integer externalTemperatureSensorsCount;

    @Option(
            names = {"--intemp-sensors"},
            defaultValue = "1",
            description = "Number of internal temperature sensors to simulate"
    )
    private Integer internalTemperatureSensorsCount;

    /**
     * The main method of the simulator.
     *
     * @param args the command-line arguments
     */
    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new CLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        LOG.info("Creating sensors simulators...");
        final var sensorsSamplingRate = Duration.ofSeconds(sensorsSamplingRateSeconds);
        final List<LoRaSensorSimulator> sensors = new ArrayList<>();
        for (int i = 0; i < airTemperatureSensorsCount; i++) {
            final var devEui = String.format("airtemp%09X", i);
            sensors.add(
                    new LoRaTemperatureSensorSimulator.Builder(devEui).baselineTemperature(2)
                        .dayTemperatureDelta(10)
                        .samplingRate(sensorsSamplingRate)
                        .build()
            );
        }
        for (int i = 0; i < externalTemperatureSensorsCount; i++) {
            final var devEui = String.format("extemp%010X", i);
            sensors.add(new LoRaTemperatureSensorSimulator.Builder(devEui).baselineTemperature(0)
                    .dayTemperatureDelta(10)
                    .samplingRate(sensorsSamplingRate)
                    .build());
        }
        for (int i = 0; i < internalTemperatureSensorsCount; i++) {
            final var devEui = String.format("intemp%010X", i);
            sensors.add(new LoRaTemperatureSensorSimulator.Builder(devEui).baselineTemperature(0)
                    .dayTemperatureDelta(4)
                    .samplingRate(sensorsSamplingRate)
                    .build());
        }
        LOG.debug("Created {} sensors simulators", sensors.size());

        try {
            LOG.info("Connecting to the MQTT broker...");
            final var mqttClient = new MqttClient(mqttServerURI, MQTT_CLIENT_ID, persistence);
            mqttClient.connect();

            LOG.info("Creating the network server simulator...");
            final var networkServer = new ChirpstackSimulator(mqttClient, loraApplicationID, sensors);

            LOG.info("Starting the network server simulator...");
            networkServer.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("Stopping the network server simulator...");
                networkServer.stop();
            }));

            networkServer.await();
        } catch (final MqttException e) {
            LOG.error("A problem with the MQTT broker occurred", e);
        } catch (final InterruptedException e) {
            LOG.error("Problem occurred during the network server simulator operations", e);
        }
    }

}
