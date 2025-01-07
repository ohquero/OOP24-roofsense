package roofsense.chirpstacksimulator;

import io.reactivex.rxjava3.core.Observable;
import org.apache.commons.lang3.Validate;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * The FakeLoRaTemperatureSensor class simulates a battery powered {@link FakeLoRaSensor} sensing
 * temperature.
 * <p>
 * The temperature will fluctuate depending on the time of the day. The fluctuation baseline is determined by the
 * current time of the year.
 * <p>
 * The battery level will decrease linearly over a specified time from the moment the sensor is started.
 */
public class FakeLoRaTemperatureSensor extends FakeLoRaSensor {

    private final Duration dischargeTime;
    private final Integer baseTemperature;
    private final Integer dayTemperatureDelta;

    /**
     * Constructor.
     *
     * @param builder the builder
     */
    protected FakeLoRaTemperatureSensor(final Builder builder) {
        super(builder);
        this.dischargeTime = builder.dischargeTime;
        this.baseTemperature = builder.baselineTemperature;
        this.dayTemperatureDelta = builder.dayTemperatureDelta;
    }

    // CHECKSTYLE: MagicNumber OFF
    // Hardcoded constants are not used in other parts of the code. Keeping them here for code readability.
    private static double getPeriodBaselineTemperature(final LocalDate date) {
        final int month = date.getMonthValue();
        final double seasonBaselineTemperature;
        if (month >= 2 && month <= 4) {
            seasonBaselineTemperature = 10;
        } else if (month >= 5 && month <= 7) {
            seasonBaselineTemperature = 20;
        } else if (month >= 8 && month <= 10) {
            seasonBaselineTemperature = 15;
        } else {
            seasonBaselineTemperature = 5;
        }
        return seasonBaselineTemperature;
    }
    // CHECKSTYLE: MagicNumber ON

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<Data> getDataStream() {
        final var ticksForDischarge = dischargeTime.dividedBy(getSamplingRate());

        return Observable.interval(getSamplingRate().get(ChronoUnit.NANOS), TimeUnit.MILLISECONDS).map(tick -> {
            // Calculating battery level
            final var batteryLevel = 100 - (int) (100 * ((double) tick % ticksForDischarge / ticksForDischarge));

            // Calculating temperature
            final var now = LocalDateTime.now();
            final var seasonBaselineTemperature = getPeriodBaselineTemperature(now.toLocalDate());
            final var temperatureFluctuation = Math.sin(Math.PI * now.getHour() / 24) * dayTemperatureDelta;
            final var temperature = (float) (baseTemperature + seasonBaselineTemperature + temperatureFluctuation);

            // Encoding and sending data
            final var batteryLevelBase64 = Base64.getEncoder()
                    .encodeToString(ByteBuffer.allocate(Integer.BYTES).putInt(batteryLevel).array());
            final var temperatureBase64 = Base64.getEncoder()
                    .encodeToString(ByteBuffer.allocate(Float.BYTES).putFloat(temperature).array());
            final var base64Data = String.format("10%b20%b", batteryLevelBase64, temperatureBase64);
            final var jsonData = String.format("{\"battery\":%d,\"temperature\":%f}", batteryLevel, temperature);
            return new Data(LocalDateTime.now(), getDevEui(), base64Data, jsonData);
        });
    }

    /**
     * Returns the discharge time of the battery.
     *
     * @return the discharge time of the battery.
     */
    public Duration getDischargeTime() {
        return dischargeTime;
    }

    /**
     * Returns the minimum temperature that the sensor will report, at any time of the year.
     *
     * @return the baseline temperature.
     */
    public Integer getBaseTemperature() {
        return baseTemperature;
    }

    /**
     * Returns the maximum temperature fluctuation that the sensor will report during the day.
     *
     * @return the temperature fluctuation.
     */
    public Integer getDayTemperatureDelta() {
        return dayTemperatureDelta;
    }

    /**
     * Builder for {@link FakeLoRaTemperatureSensor}.
     */
    public static class Builder extends FakeLoRaSensor.Builder<Builder, FakeLoRaTemperatureSensor> {

        private static final Duration DEFAULT_DISCHARGE_TIME = Duration.ofMinutes(30);
        private static final Integer DEFAULT_BASELINE_TEMPERATURE = 0;
        private static final Integer DEFAULT_DAY_TEMPERATURE_DELTA = 5;

        private Duration dischargeTime = DEFAULT_DISCHARGE_TIME;
        private Integer baselineTemperature = DEFAULT_BASELINE_TEMPERATURE;
        private Integer dayTemperatureDelta = DEFAULT_DAY_TEMPERATURE_DELTA;

        /**
         * Constructor.
         *
         * @param devEui the DevEUI of the sensor
         */
        public Builder(final String devEui) {
            super(devEui);
        }

        /**
         * Sets the discharge time of the battery.
         *
         * @param dischargeTime the discharge time of the battery
         * @return this builder
         */
        public Builder dischargeTime(final Duration dischargeTime) {
            Validate.notNull(dischargeTime, "dischargeTime must not be null");
            Validate.isTrue(dischargeTime.toNanos() > 0, "dischargeTime must be a positive duration");
            this.dischargeTime = dischargeTime;
            return self();
        }

        /**
         * Sets the minimum temperature that the sensor will report, at any time of the year.
         *
         * @param baselineTemperature the baseline temperature
         * @return this builder
         */
        public Builder baselineTemperature(final Integer baselineTemperature) {
            Validate.notNull(baselineTemperature, "baselineTemperature must not be null");
            Validate.isTrue(baselineTemperature >= 0, "baselineTemperature must be a positive integer");
            this.baselineTemperature = baselineTemperature;
            return self();
        }

        /**
         * Sets the maximum temperature fluctuation that the sensor will report during the day.
         *
         * @param dayTemperatureDelta the temperature fluctuation
         * @return this builder
         */
        public Builder dayTemperatureDelta(final Integer dayTemperatureDelta) {
            Validate.notNull(dayTemperatureDelta, "dayTemperatureDelta must not be null");
            Validate.isTrue(dayTemperatureDelta > 0, "dayTemperatureDelta must be greater than 0");
            this.dayTemperatureDelta = dayTemperatureDelta;
            return self();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Builder self() {
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FakeLoRaTemperatureSensor build() {
            return new FakeLoRaTemperatureSensor(this);
        }

    }

}
