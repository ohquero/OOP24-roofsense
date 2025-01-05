package roofsense.lora.networkserver.simulator.fakes;

import io.reactivex.rxjava3.core.Observable;
import roofsense.lora.networkserver.simulator.SimulatedLoRaSensor;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * A mock implementation of {@link SimulatedLoRaSensor} that allows to emit a specific number of measurements.
 */
public final class SimulatedLoRaSensorMock extends SimulatedLoRaSensor {

    private final Integer measurementsToEmitCount;

    private SimulatedLoRaSensorMock(final Builder builder) {
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
     * Builder for {@link SimulatedLoRaSensorMock}.
     */
    public static final class Builder extends SimulatedLoRaSensor.Builder<Builder, SimulatedLoRaSensorMock> {

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
        public SimulatedLoRaSensorMock build() {
            return new SimulatedLoRaSensorMock(this);
        }

    }

}
