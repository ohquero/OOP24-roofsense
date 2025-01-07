package roofsense.chirpstacksimulator;

import io.reactivex.rxjava3.core.Observable;
import org.apache.commons.lang3.Validate;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The FakeLoRaSensor class represents a dummy LoRa sensor.
 * <p>
 * The sensor emits data at a specified sampling rate. The data stream is represented by an {@link Observable} of
 * {@link Data} objects.
 */
public abstract class FakeLoRaSensor {

    private final String devEui;
    private final Duration samplingRate;

    /**
     * Constructor.
     *
     * @param builder the builder
     */
    protected FakeLoRaSensor(final Builder<? extends Builder<?, ?>, ? extends FakeLoRaSensor> builder) {
        this.devEui = builder.devEui;
        this.samplingRate = builder.samplingRate;
    }

    /**
     * Returns the device EUI.
     *
     * @return the device EUI
     */
    public String getDevEui() {
        return devEui;
    }

    /**
     * Returns the sampling rate.
     *
     * @return the sampling rate
     */
    public Duration getSamplingRate() {
        return samplingRate;
    }

    /**
     * Returns an {@link Observable} that emits {@link Data} objects at a specified sampling rate.
     *
     * @return the data stream
     */
    public abstract Observable<Data> getDataStream();

    /**
     * The data class represents the data emitted by a sensor as it would be received by a client from the network
     * server. The data will contain the device EUI, the device payload in base64 and JSON format.
     *
     * @param timestamp the timestamp.
     * @param devEui    the device EUI.
     * @param base64    the base64 encoded payload.
     * @param json      the JSON encoded payload.
     */
    public record Data(LocalDateTime timestamp, String devEui, String base64, String json) {

        /**
         * Constructor.
         */
        public Data {
            Validate.notNull(timestamp, "timestamp must not be null");
            Validate.notEmpty(devEui, "devEui must not be null or empty");
            Validate.notEmpty(base64, "base64 must not be null or empty");
            Validate.notEmpty(json, "json must not be null or empty");
        }

    }

    /**
     * The builder class for {@link FakeLoRaSensor}.
     *
     * @param <B> the builder type
     * @param <T> the sensor type
     */
    public abstract static class Builder<B extends Builder<B, T>, T> {

        private static final Duration DEFAULT_SAMPLING_RATE = Duration.ofSeconds(30);

        private final String devEui;
        private Duration samplingRate = DEFAULT_SAMPLING_RATE;

        /**
         * Constructor.
         *
         * @param devEui the device EUI
         */
        public Builder(final String devEui) {
            Validate.notEmpty(devEui, "devEui must not be null or empty");
            Validate.isTrue(devEui.length() == 16, "devEui must be 16 characters long");
            this.devEui = devEui;
        }

        /**
         * Sets the sampling rate.
         *
         * @param samplingRate the sampling rate
         * @return the builder
         */
        public B samplingRate(final Duration samplingRate) {
            Validate.notNull(samplingRate, "samplingRate must not be null");
            Validate.isTrue(samplingRate.toNanos() > 0, "samplingRate must be greater than 0");
            this.samplingRate = samplingRate;
            return self();
        }

        /**
         * Returns the builder instance.
         *
         * @return the builder instance
         */
        protected abstract B self();

        /**
         * Builds the sensor.
         *
         * @return the sensor
         */
        public abstract T build();

    }

}
