package roofsense.lora.networkserver.simulator.fakes;

import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.List;

public class FakeMqttClient implements IMqttClient {

    private final String serverURI;
    private boolean connected;
    private final List<Message> messages;

    public FakeMqttClient(final String serverURI) {
        this.connected = false;
        this.serverURI = serverURI;
        this.messages = new ArrayList<>();
    }

    @Override
    public void connect() {
        this.connected = true;
    }

    @Override
    public void connect(MqttConnectOptions options) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken connectWithResult(MqttConnectOptions options) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void disconnect(long quiesceTimeout) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void disconnectForcibly() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void disconnectForcibly(long disconnectTimeout) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String topicFilter) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String[] topicFilters) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String topicFilter, int qos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String[] topicFilters, int[] qos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String topicFilter, IMqttMessageListener messageListener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String[] topicFilters, IMqttMessageListener[] messageListeners) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String topicFilter) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String topicFilter, IMqttMessageListener messageListener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String topicFilter, int qos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String topicFilter, int qos, IMqttMessageListener messageListener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String[] topicFilters) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String[] topicFilters, IMqttMessageListener[] messageListeners) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void unsubscribe(String topicFilter) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void unsubscribe(String[] topicFilters) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) {
        this.messages.add(new Message(topic, payload, qos, retained));
    }

    public List<Message> getPublishedMessages() {
        return this.messages;
    }

    @Override
    public void publish(String topic, MqttMessage message) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setCallback(MqttCallback callback) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MqttTopic getTopic(String topic) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public String getClientId() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getServerURI() {
        return this.serverURI;
    }

    @Override
    public IMqttDeliveryToken[] getPendingDeliveryTokens() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setManualAcks(boolean manualAcks) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void reconnect() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void messageArrivedComplete(int messageId, int qos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public record Message(String topic, byte[] payload, int qos, boolean retained) {

    }

}
