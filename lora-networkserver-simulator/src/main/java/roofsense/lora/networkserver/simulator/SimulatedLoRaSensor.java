package roofsense.lora.networkserver.simulator;

import io.reactivex.rxjava3.core.Observable;
import org.apache.commons.lang3.Validate;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class SimulatedLoRaSensor {

    private final Duration samplingRate;
    private final String devEui;

    protected SimulatedLoRaSensor(final Builder<? extends Builder<?, ?>, ? extends SimulatedLoRaSensor> builder) {
        Validate.validState(builder.devEui != null, "devEui must be specified");
        this.devEui = builder.devEui;
        this.samplingRate = builder.samplingRate;
    }

    public String getDevEui() {
        return devEui;
    }

    public Observable<Data> getDataStream() {
        return Observable.interval(0, samplingRate.getSeconds(), SECONDS).map(tick -> {
            final var measurement = getMeasurement();
            return new Data(devEui, measurement);
        });
    }

    protected abstract Measurement getMeasurement();

    public static abstract class Builder<BUILDER extends Builder<BUILDER, RETURN_TYPE>, RETURN_TYPE> {

        private final String devEui;
        private Duration samplingRate = Duration.ofSeconds(30);

        public Builder(final String devEui) {
            Validate.notEmpty(devEui, "devEui must not be null or empty");
            Validate.isTrue(devEui.length() == 16, "devEui must be 16 characters long");
            this.devEui = devEui;
        }

        public BUILDER samplingRate(Duration samplingRate) {
            Validate.notNull(samplingRate, "samplingRate must not be null");
            this.samplingRate = samplingRate;
            return self();
        }

        protected abstract BUILDER self();

        public abstract RETURN_TYPE build();

    }

    public record Data(String devEui, Measurement measurement) {

    }

    public record Measurement(String base64, String json) {

    }

}
