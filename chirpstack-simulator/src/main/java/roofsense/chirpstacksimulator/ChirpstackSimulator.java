package roofsense.chirpstacksimulator;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.commons.lang3.Validate;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * This class simulates the ChirpStack LoRa network sever. At the moment only the down-link messages MQTT forwarding is
 * simulated.
 * <p>
 * The business logic will be executed in a separate thread, so the calling thread will not be blocked.
 */
public class ChirpstackSimulator {

    private static final Logger LOG = LoggerFactory.getLogger(ChirpstackSimulator.class);

    private final IMqttClient mqttClient;
    private final String loraApplicationID;
    private final Collection<? extends LoRaSensorSimulator> sensors;
    private Disposable disposable;
    private volatile CountDownLatch latch;

    /**
     * Creates a new instance of ChirpstackSimulator.
     *
     * @param mqttClient        the MQTT client which will be used to send data to the MQTT broker
     * @param loraApplicationID the LoRa application ID under which the sensors will be registered
     * @param sensors           the collection of sensors which will be monitored
     */
    public ChirpstackSimulator(
            final IMqttClient mqttClient,
            final String loraApplicationID,
            final Collection<? extends LoRaSensorSimulator> sensors
    ) {
        this.mqttClient = Validate.notNull(mqttClient, "mqttClient must not be null");
        this.loraApplicationID = Validate.notEmpty(loraApplicationID, "loraApplicationID must not be empty or null");
        this.sensors = Validate.notEmpty(sensors, "sensors must not be empty or null");
    }

    /**
     * Starts the network server. If the network server is already running, this method does nothing.
     *
     * @throws MqttException if an error occurs while connecting to the MQTT broker
     */
    public void start() throws MqttException {
        if (isRunning()) {
            return;
        }

        if (!mqttClient.isConnected()) {
            LOG.debug("Connecting to MQTT broker {}... ", mqttClient.getServerURI());
            mqttClient.connect();
            LOG.info("Connected to MQTT broker {}", mqttClient.getServerURI());
        }

        // Merging all sensors data streams into a single stream
        var sensorsDataStream = sensors.stream()
                .map(LoRaSensorSimulator::getDataStream)
                .reduce(Observable::merge)
                .orElseThrow();

        // Latch which will be used by other threads to wait for the ChirpstackSimulator signal that no other
        // data will be sent to the MQTT broker
        latch = new CountDownLatch(1);
        sensorsDataStream = sensorsDataStream.doOnDispose(latch::countDown);

        disposable = sensorsDataStream.subscribeOn(Schedulers.io()).subscribe(
                sensorData -> {
                    LOG.info("Sending sensor {} data to the MQTT broker...", sensorData.devEui());

                    // Creating a Json object with the sensor data
                    final var message = JsonNodeFactory.instance.objectNode();
                    message.put("devEui", sensorData.devEui());
                    message.put("confirmed", true);
                    message.put("fPort", 10);
                    message.put("data", sensorData.base64());
                    message.put("object", sensorData.json());

                    // Publishing sensorData to MQTT broker
                    mqttClient.publish(
                            "application/" + loraApplicationID + "/device/" + sensorData.devEui() + "/command/down",
                            message.toString().getBytes(StandardCharsets.UTF_8),
                            0,
                            false
                    );
                    LOG.debug("Sensor {} sent data to MQTT broker", sensorData.devEui());
                },
                (e) -> {
                    LOG.error("Unexpected exception thrown by a sensor", e);
                    throw new IllegalStateException("Unexpected exception thrown by a sensor", e);
                },
                () -> {
                    LOG.info("All sensors have sent their data to the MQTT broker");
                    latch.countDown();
                }
        );
    }

    /**
     * Stops the network server. If the network server is not running, this method does nothing.
     */
    public void stop() {
        if (isRunning()) {
            disposable.dispose();
        }
    }

    /**
     * Returns {@code true} if the network server is running.
     *
     * @return true if the simulated ChirpStack is running, false otherwise
     */
    public Boolean isRunning() {
        return disposable != null && !disposable.isDisposed();
    }

    /**
     * Causes the calling thread to be halted until the network server has been stopped.
     *
     * @throws InterruptedException if the thread wait is interrupted
     */
    public void await() throws InterruptedException {
        if (latch != null) {
            latch.await();
        }
    }

}
