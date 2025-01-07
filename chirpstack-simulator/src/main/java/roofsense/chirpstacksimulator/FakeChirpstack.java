package roofsense.chirpstacksimulator;

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
public class FakeChirpstack {

    private static final Logger LOG = LoggerFactory.getLogger(FakeChirpstack.class);

    private final IMqttClient mqttClient;
    private final Collection<? extends FakeLoRaSensor> sensors;
    private Disposable disposable;
    private volatile CountDownLatch latch;

    /**
     * Creates a new instance of FakeChirpstack.
     *
     * @param mqttClient the MQTT client which will be used to send data to the MQTT broker
     * @param sensors    the collection of sensors which will be monitored
     */
    public FakeChirpstack(
            final IMqttClient mqttClient,
            final Collection<? extends FakeLoRaSensor> sensors
    ) {
        this.mqttClient = Validate.notNull(mqttClient, "mqttClient must not be null");
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
                .map(FakeLoRaSensor::getDataStream)
                .reduce(Observable::merge)
                .orElseThrow();

        // Latch which will be used by other threads to wait for the FakeChirpstack signal that no other
        // data will be sent to the MQTT broker
        latch = new CountDownLatch(1);
        sensorsDataStream = sensorsDataStream.doOnDispose(latch::countDown);

        disposable = sensorsDataStream.subscribeOn(Schedulers.io()).subscribe(
                sensorData -> {
                    LOG.info(
                            "Sensor {} sending data to MQTT broker...", sensorData.devEui());
                    // Publishing sensorData to MQTT broker
                    mqttClient.publish(
                            "application/1/device/" + sensorData.devEui() + "/command/down",
                            sensorData.json().getBytes(StandardCharsets.UTF_8),
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
