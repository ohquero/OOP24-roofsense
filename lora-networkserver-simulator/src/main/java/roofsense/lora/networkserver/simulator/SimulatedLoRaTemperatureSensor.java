package roofsense.lora.networkserver.simulator;

import org.apache.commons.lang3.Validate;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Base64;
import java.util.Calendar;

/**
 * The SimulatedLoRaTemperatureSensor class simulates a {@link SimulatedLoRaSensor} sensing temperature.
 */
public class SimulatedLoRaTemperatureSensor extends SimulatedLoRaSensor {

    private final Long dischargeTimeMillis;
    private final Integer baseTemperature;
    private final Integer dayTemperatureDelta;

    private SimulatedLoRaTemperatureSensor(final Builder builder) {
        super(builder);
        this.dischargeTimeMillis = builder.dischargeTime.toMillis();
        this.baseTemperature = builder.baselineTemperature;
        this.dayTemperatureDelta = builder.dayTemperatureDelta;
    }

    private Integer getBatteryLevel() {
        final var calendar = Calendar.getInstance();
        long currentTimeMillis = calendar.getTimeInMillis();
        long millisSinceLastFullCharge = currentTimeMillis % dischargeTimeMillis;

        return 100 - (int) (100 * ((double) millisSinceLastFullCharge / (double) dischargeTimeMillis));
    }

    private Float getTemperature() {
        final var calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int month = calendar.get(Calendar.MONTH);

        // This temperature simulates the difference of temperature between seasons
        double seasonBaselineTemperature;
        if (month >= 2 && month <= 4) {
            seasonBaselineTemperature = 10;
        } else if (month >= 5 && month <= 7) {
            seasonBaselineTemperature = 20;
        } else if (month >= 8 && month <= 10) {
            seasonBaselineTemperature = 15;
        } else {
            seasonBaselineTemperature = 5;
        }

        double temperatureFluctuation = Math.sin(Math.PI * hour / 24) * dayTemperatureDelta;
        return (float) (baseTemperature + seasonBaselineTemperature + temperatureFluctuation);
    }

    @Override
    protected Measurement getMeasurement() {
        final var batteryLevel = getBatteryLevel();
        final var temperature = getTemperature();

        final var batteryLevelBase64 = Base64.getEncoder()
                .encodeToString(ByteBuffer.allocate(Integer.BYTES).putInt(batteryLevel).array());
        final var temperatureBase64 = Base64.getEncoder()
                .encodeToString(ByteBuffer.allocate(Float.BYTES).putFloat(temperature).array());

        final var base64Data = String.format("10%b20%b", batteryLevelBase64, temperatureBase64);
        final var jsonData = String.format("{\"battery\":%d,\"temperature\":%f}", getBatteryLevel(), getTemperature());
        return new Measurement(base64Data, jsonData);
    }

    public static class Builder extends SimulatedLoRaSensor.Builder<Builder, SimulatedLoRaTemperatureSensor> {

        private Duration dischargeTime = Duration.ofHours(24);
        private Integer baselineTemperature = 0;
        private Integer dayTemperatureDelta = 5;

        public Builder(String devEui) {
            super(devEui);
        }

        public Builder dischargeTime(final Duration dischargeTime) {
            this.dischargeTime = dischargeTime;
            return self();
        }

        public Builder baselineTemperature(final Integer baselineTemperature) {
            this.baselineTemperature = baselineTemperature;
            return self();
        }

        public Builder dayTemperatureDelta(final Integer dayTemperatureDelta) {
            Validate.isTrue(dayTemperatureDelta >= 0, "dayTemperatureDelta must be a positive integer");
            this.dayTemperatureDelta = dayTemperatureDelta;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public SimulatedLoRaTemperatureSensor build() {
            return new SimulatedLoRaTemperatureSensor(this);
        }

    }

}
