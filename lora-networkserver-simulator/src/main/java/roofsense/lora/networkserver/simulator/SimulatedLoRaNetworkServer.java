package roofsense.lora.networkserver.simulator;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.commons.lang3.Validate;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class SimulatedLoRaNetworkServer {

    private static final Logger LOG = LoggerFactory.getLogger(SimulatedLoRaNetworkServer.class);

    private final IMqttClient mqttClient;
    private final Collection<? extends SimulatedLoRaSensor> sensors;
    private Disposable disposable = null;
    private CountDownLatch latch;

    public SimulatedLoRaNetworkServer(final IMqttClient mqttClient, final Collection<? extends SimulatedLoRaSensor> sensors) {
        this.mqttClient = Validate.notNull(mqttClient, "mqttClient must not be null");
        this.sensors = Validate.notEmpty(sensors, "sensors must not be empty or null");
    }

    public void start() {
        if (!mqttClient.isConnected()) {
            LOG.debug("Connecting to MQTT broker {}... ", mqttClient.getServerURI());
            try {
                mqttClient.connect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
            LOG.info("Connected to MQTT broker {}", mqttClient.getServerURI());
        }

        // Merging all sensors data streams into a single stream
        var sensorsDataStream = sensors.stream()
                .map(SimulatedLoRaSensor::getDataStream)
                .reduce(Observable::merge)
                .orElseThrow();

        // Count down the latch when the stream is disposed
        latch = new CountDownLatch(1);
        sensorsDataStream = sensorsDataStream.doOnDispose(latch::countDown);

        disposable = sensorsDataStream.subscribeOn(Schedulers.io()).subscribe(
                sensorData -> {
                    LOG.info(
                            "Sensor {} sending data to MQTT broker...", sensorData.devEui());
                    try {
                        // Publishing sensorData to MQTT broker
                        mqttClient.publish(
                                "application/1/device/" + sensorData.devEui() + "/command/down",
                                sensorData.measurement().json().getBytes(),
                                0,
                                false
                        );
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    LOG.debug("Sensor {} sent data to MQTT broker", sensorData.devEui());
                },
                (e) -> {
                    throw new RuntimeException(e);
                },
                latch::countDown
        );
    }

    public void stop() {
        if (isRunning()) {
            disposable.dispose();
        }
    }

    public Boolean isRunning() {
        return disposable != null && !disposable.isDisposed();
    }

    public void await() throws InterruptedException {
        if (latch != null) {
            latch.await();
        }
    }

}