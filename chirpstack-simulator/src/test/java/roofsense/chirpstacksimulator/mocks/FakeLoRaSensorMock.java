package roofsense.chirpstacksimulator.mocks;

import io.reactivex.rxjava3.core.Observable;
import roofsense.chirpstacksimulator.FakeLoRaSensor;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * A mock implementation of {@link FakeLoRaSensor} that allows to emit a specific number of measurements.
 */
public final class FakeLoRaSensorMock extends FakeLoRaSensor {

    private final Integer measurementsToEmitCount;

    private FakeLoRaSensorMock(final Builder builder) {
        super(builder);
        this.measurementsToEmitCount = builder.measurementsToEmitCount;
    }

    @Override
    public Observable<Data> getDataStream() {
        var dataStream = Observable.interval(0, getSamplingRate().toNanos(), TimeUnit.NANOSECONDS)
                .map(tick -> new Data(LocalDateTime.now(), getDevEui(), "base64", "json"));
        if (measurementsToEmitCount != null) {
            dataStream = dataStream.take(measurementsToEmitCount);
        }
        return dataStream;
    }

    /**
     * Builder for {@link FakeLoRaSensorMock}.
     */
    public static final class Builder extends FakeLoRaSensor.Builder<Builder, FakeLoRaSensorMock> {

        private Integer measurementsToEmitCount;

        /**
         * Constructor.
         *
         * @param devEui the DevEUI of the sensor
         */
        public Builder(final String devEui) {
            super(devEui);
        }

        /**
         * Sets the number of measurements to emit.
         *
         * @param count the number of measurements to emit
         * @return this builder
         */
        public Builder measurementsToEmit(final Integer count) {
            this.measurementsToEmitCount = count;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public FakeLoRaSensorMock build() {
            return new FakeLoRaSensorMock(this);
        }

    }

}
