package roofsense.lora.networkserver.simulator.fakes;

import org.apache.commons.lang3.Validate;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A mock implementation of the {@link IMqttClient} interface. For testing purposes only.
 */
public final class MqttClientMock implements IMqttClient {

    private static final String NOT_IMPLEMENTED_EX_MSG = "This method is not implemented";
    private final String serverURI;
    private final List<Message> messages;
    private boolean connected;

    /**
     * Creates a new instance of the {@link MqttClientMock} class.
     *
     * @param serverURI The URI of the MQTT server.
     */
    public MqttClientMock(final String serverURI) {
        this.connected = false;
        this.serverURI = serverURI;
        this.messages = new ArrayList<>();
    }

    @Override
    public void connect() {
        this.connected = true;
    }

    @Override
    public void connect(final MqttConnectOptions options) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken connectWithResult(final MqttConnectOptions options) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void disconnect(final long quiesceTimeout) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void disconnectForcibly() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void disconnectForcibly(final long disconnectTimeout) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void disconnectForcibly(final long quiesceTimeout, final long disconnectTimeout) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String topicFilter) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String[] topicFilters) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String topicFilter, final int qos) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String[] topicFilters, final int[] qos) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String topicFilter, final IMqttMessageListener messageListener) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String[] topicFilters, final IMqttMessageListener[] messageListeners) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String topicFilter, final int qos, final IMqttMessageListener messageListener) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void subscribe(final String[] topicFilters, final int[] qos, final IMqttMessageListener[] messageListeners) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(final String topicFilter) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(final String topicFilter, final IMqttMessageListener messageListener) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(final String topicFilter, final int qos) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(
            final String topicFilter,
            final int qos,
            final IMqttMessageListener messageListener
    ) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(final String[] topicFilters) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(
            final String[] topicFilters,
            final IMqttMessageListener[] messageListeners
    ) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(final String[] topicFilters, final int[] qos) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public IMqttToken subscribeWithResponse(
            final String[] topicFilters,
            final int[] qos,
            final IMqttMessageListener[] messageListeners
    ) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void unsubscribe(final String topicFilter) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void unsubscribe(final String[] topicFilters) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void publish(final String topic, final byte[] payload, final int qos, final boolean retained) {
        this.messages.add(new Message(LocalDateTime.now(), topic, payload, qos, retained));
    }

    /**
     * Returns the messages published using this MQTT client.
     *
     * @return The messages published using this MQTT client.
     */
    public List<Message> getPublishedMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    @Override
    public void publish(final String topic, final MqttMessage message) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void setCallback(final MqttCallback callback) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public MqttTopic getTopic(final String topic) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public String getClientId() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public String getServerURI() {
        return this.serverURI;
    }

    @Override
    public IMqttDeliveryToken[] getPendingDeliveryTokens() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void setManualAcks(final boolean manualAcks) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void reconnect() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void messageArrivedComplete(final int messageId, final int qos) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_EX_MSG);
    }

    /**
     * Represents a message published using the MQTT client.
     *
     * @param timestamp The timestamp of the message.
     * @param topic     The topic of the message.
     * @param payload   The payload of the message.
     * @param qos       The quality of service of the message.
     * @param retained  Whether the message is retained or not.
     */
    public record Message(LocalDateTime timestamp, String topic, byte[] payload, int qos, boolean retained) {

        /**
         * Constructor.
         *
         * @param timestamp The timestamp of the message.
         * @param topic     The topic of the message.
         * @param payload   The payload of the message.
         * @param qos       The quality of service of the message.
         * @param retained  Whether the message is retained or not.
         */
        public Message(
                final LocalDateTime timestamp,
                final String topic,
                final byte[] payload,
                final int qos,
                final boolean retained
        ) {
            this.timestamp = Validate.notNull(timestamp, "timestamp must not be null");
            this.topic = Validate.notEmpty(topic, "topic must not be null or empty");
            this.payload = Validate.notNull(payload, "payload must not be null").clone();
            this.qos = qos;
            this.retained = retained;
        }

        /**
         * @return The timestamp of the message.
         */
        @Override
        public byte[] payload() {
            return payload.clone();
        }

    }

}
