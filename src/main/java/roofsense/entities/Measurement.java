package roofsense.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a decoded measurement from a sensor.
 */
@Entity
@Table(name = "measurements")
public class Measurement {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false, length = 30)
    private MeasurementType measurementType;

    @NotNull
    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "unit", length = 20)
    private String unit;

    /**
     * Default constructor for JPA.
     */
    protected Measurement() {
        // Required by JPA
    }

    /**
     * Creates a new instance of {@code Measurement}.
     *
     * @param sensor          the sensor that produced the measurement
     * @param timestamp       the timestamp of the measurement
     * @param measurementType the type of measurement
     * @param value           the measured value
     * @param unit            the unit of measurement
     */
    public Measurement(final Sensor sensor, final LocalDateTime timestamp, final MeasurementType measurementType,
                       final Double value, final String unit) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.measurementType = measurementType;
        this.value = value;
        this.unit = unit;
    }

    /**
     * Returns the id of the associated database record, or {@code null} if this entity has not been persisted yet.
     *
     * @return the id of the associated database record.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the sensor that produced the measurement.
     *
     * @return the sensor
     */
    public final Sensor getSensor() {
        return sensor;
    }

    /**
     * Sets the sensor that produced the measurement.
     *
     * @param sensor the sensor to set
     */
    public final void setSensor(final Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * Returns the timestamp of the measurement.
     *
     * @return the timestamp
     */
    public final LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the measurement.
     *
     * @param timestamp the timestamp to set
     */
    public final void setTimestamp(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the type of measurement.
     *
     * @return the measurement type
     */
    public final MeasurementType getMeasurementType() {
        return measurementType;
    }

    /**
     * Sets the type of measurement.
     *
     * @param measurementType the measurement type to set
     */
    public final void setMeasurementType(final MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    /**
     * Returns the measured value.
     *
     * @return the value
     */
    public final Double getValue() {
        return value;
    }

    /**
     * Sets the measured value.
     *
     * @param value the value to set
     */
    public final void setValue(final Double value) {
        this.value = value;
    }

    /**
     * Returns the unit of measurement.
     *
     * @return the unit
     */
    public final String getUnit() {
        return unit;
    }

    /**
     * Sets the unit of measurement.
     *
     * @param unit the unit to set
     */
    public final void setUnit(final String unit) {
        this.unit = unit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof final Measurement measurement)) {
            return false;
        }
        return Objects.equals(getId(), measurement.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return new StringJoiner(", ", Measurement.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("timestamp=" + timestamp)
                .add("measurementType=" + measurementType)
                .add("value=" + value)
                .add("unit='" + unit + "'")
                .toString();
    }
}
