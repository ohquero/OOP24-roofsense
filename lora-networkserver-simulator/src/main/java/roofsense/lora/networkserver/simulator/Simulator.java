package roofsense.lora.networkserver.simulator;

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

public class Simulator implements Runnable {

    private static final String MQTT_CLIENT_ID = "lora-networkserver-simulator";
    private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);

    private final MemoryPersistence persistence = new MemoryPersistence();

    @Option(names = {"-m", "--mqtt-server"}, defaultValue = "tcp://localhost:1883", description = "MQTT server URI")
    private String mqttServerURI;

    @Option(
            names = {"-r", "--rate"},
            defaultValue = "10",
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

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Simulator()).execute(args);
        System.exit(exitCode);
    }

    public void run() {
        LOG.info("Creating the sensors to simulate...");
        final var sensorsSamplingRate = Duration.ofSeconds(sensorsSamplingRateSeconds);
        final List<SimulatedLoRaSensor> sensors = new ArrayList<>();
        for (int i = 0; i < airTemperatureSensorsCount; i++) {
            final var devEui = String.format("airtemp%09X", i);
            sensors.add(new SimulatedLoRaTemperatureSensor.Builder(devEui)
                    .baselineTemperature(2)
                    .dayTemperatureDelta(10)
                    .samplingRate(sensorsSamplingRate)
                    .build());
        }
        for (int i = 0; i < externalTemperatureSensorsCount; i++) {
            final var devEui = String.format("extemp%010X", i);
            sensors.add(new SimulatedLoRaTemperatureSensor.Builder(devEui)
                    .baselineTemperature(0)
                    .dayTemperatureDelta(10)
                    .samplingRate(sensorsSamplingRate)
                    .build());
        }
        for (int i = 0; i < internalTemperatureSensorsCount; i++) {
            final var devEui = String.format("intemp%010X", i);
            sensors.add(new SimulatedLoRaTemperatureSensor.Builder(devEui)
                    .baselineTemperature(0)
                    .dayTemperatureDelta(4)
                    .samplingRate(sensorsSamplingRate)
                    .build());
        }
        LOG.debug("Created {} sensors", sensors.size());

        LOG.info("Connecting to the MQTT broker...");
        final MqttClient mqttClient;
        try {
            mqttClient = new MqttClient(mqttServerURI, MQTT_CLIENT_ID, persistence);
            mqttClient.connect();
        } catch (final MqttException e) {
            LOG.error("Connection to the MQTT broker failed", e);
            return;
        }

        LOG.info("Creating the network server to simulate...");
        final var networkServer = new SimulatedLoRaNetworkServer(mqttClient, sensors);

        LOG.info("Starting the network server...");
        networkServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Stopping the network server...");
            networkServer.stop();
        }));

        try {
            networkServer.await();
        } catch (final InterruptedException e) {
            LOG.error("Network server termination await interrupted", e);
        }

        LOG.info("The simulation has finished, bye!");
    }

}