package roofsense.lora.networkserver.simulator.fakes;

import io.reactivex.rxjava3.core.Observable;
import roofsense.lora.networkserver.simulator.SimulatedLoRaSensor;

public class FakeSimulatedLoRaSensor extends SimulatedLoRaSensor {

    private final Integer measurementsToEmitCount;

    private FakeSimulatedLoRaSensor(final Builder builder) {
        super(builder);
        this.measurementsToEmitCount = builder.measurementsToEmitCount;
    }

    public static class Builder extends SimulatedLoRaSensor.Builder<Builder, FakeSimulatedLoRaSensor> {

        private Integer measurementsToEmitCount = null;

        public Builder measurementsToEmit(final Integer count) {
            this.measurementsToEmitCount = count;
            return self();
        }

        public Builder(String devEui) {
            super(devEui);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public FakeSimulatedLoRaSensor build() {
            return new FakeSimulatedLoRaSensor(this);
        }
    }

    @Override
    public Observable<Data> getDataStream() {
        var dataStream = super.getDataStream();
        if (measurementsToEmitCount != null) {
            dataStream = dataStream.take(measurementsToEmitCount);
        }
        return dataStream;
    }

    @Override
    protected Measurement getMeasurement() {
        return new Measurement("base64", "json");
    }

}
