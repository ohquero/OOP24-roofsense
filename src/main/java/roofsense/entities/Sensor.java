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
 * Represents a sensor installed at a measurement point.
 * Each sensor has a unique code that remains associated with it even after decommissioning.
 */
@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(name = "sensor_code", nullable = false, unique = true, length = 100)
    private String sensorCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_type", nullable = false, length = 50)
    private SensorType sensorType;

    @NotNull
    @Column(name = "installation_date", nullable = false)
    private LocalDateTime installationDate;

    @Column(name = "removal_date")
    private LocalDateTime removalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_point_id", nullable = false)
    private MeasurementPoint measurementPoint;

    /**
     * Default constructor for JPA.
     */
    protected Sensor() {
        // Required by JPA
    }

    /**
     * Creates a new instance of {@code Sensor}.
     *
     * @param sensorCode       the unique code identifying the sensor
     * @param sensorType       the type of sensor
     * @param installationDate the date when the sensor was installed
     * @param measurementPoint the measurement point where the sensor is installed
     */
    public Sensor(final String sensorCode, final SensorType sensorType, final LocalDateTime installationDate,
                  final MeasurementPoint measurementPoint) {
        this.sensorCode = sensorCode;
        this.sensorType = sensorType;
        this.installationDate = installationDate;
        this.measurementPoint = measurementPoint;
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
     * Returns the unique sensor code.
     *
     * @return the sensor code
     */
    public final String getSensorCode() {
        return sensorCode;
    }

    /**
     * Sets the unique sensor code.
     *
     * @param sensorCode the sensor code to set
     */
    public final void setSensorCode(final String sensorCode) {
        this.sensorCode = sensorCode;
    }

    /**
     * Returns the type of sensor.
     *
     * @return the sensor type
     */
    public final SensorType getSensorType() {
        return sensorType;
    }

    /**
     * Sets the type of sensor.
     *
     * @param sensorType the sensor type to set
     */
    public final void setSensorType(final SensorType sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * Returns the installation date of the sensor.
     *
     * @return the installation date
     */
    public final LocalDateTime getInstallationDate() {
        return installationDate;
    }

    /**
     * Sets the installation date of the sensor.
     *
     * @param installationDate the installation date to set
     */
    public final void setInstallationDate(final LocalDateTime installationDate) {
        this.installationDate = installationDate;
    }

    /**
     * Returns the removal date of the sensor, or {@code null} if the sensor is still active.
     *
     * @return the removal date, or {@code null}
     */
    public final LocalDateTime getRemovalDate() {
        return removalDate;
    }

    /**
     * Sets the removal date of the sensor.
     *
     * @param removalDate the removal date to set
     */
    public final void setRemovalDate(final LocalDateTime removalDate) {
        this.removalDate = removalDate;
    }

    /**
     * Returns the measurement point where the sensor is installed.
     *
     * @return the measurement point
     */
    public final MeasurementPoint getMeasurementPoint() {
        return measurementPoint;
    }

    /**
     * Sets the measurement point where the sensor is installed.
     *
     * @param measurementPoint the measurement point to set
     */
    public final void setMeasurementPoint(final MeasurementPoint measurementPoint) {
        this.measurementPoint = measurementPoint;
    }

    /**
     * Checks if the sensor is currently active (not removed).
     *
     * @return {@code true} if the sensor is active, {@code false} otherwise
     */
    public boolean isActive() {
        return removalDate == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof final Sensor sensor)) {
            return false;
        }
        return Objects.equals(getSensorCode(), sensor.getSensorCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getSensorCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return new StringJoiner(", ", Sensor.class.getSimpleName() + "[", "]")
                .add("sensorCode='" + sensorCode + "'")
                .add("sensorType=" + sensorType)
                .add("installationDate=" + installationDate)
                .add("removalDate=" + removalDate)
                .toString();
    }
}
