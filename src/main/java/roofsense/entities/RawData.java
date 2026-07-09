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
 * Represents raw data acquired from a sensor before decoding.
 */
@Entity
@Table(name = "raw_data")
public class RawData {

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
    @Column(name = "source", nullable = false, length = 20)
    private DataSource source;

    @NotNull
    @Column(name = "raw_payload", nullable = false, columnDefinition = "TEXT")
    private String rawPayload;

    /**
     * Default constructor for JPA.
     */
    protected RawData() {
        // Required by JPA
    }

    /**
     * Creates a new instance of {@code RawData}.
     *
     * @param sensor      the sensor that produced the raw data
     * @param timestamp   the timestamp of the raw data
     * @param source      the source of the raw data
     * @param rawPayload  the raw payload data
     */
    public RawData(final Sensor sensor, final LocalDateTime timestamp, final DataSource source, final String rawPayload) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.source = source;
        this.rawPayload = rawPayload;
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
     * Returns the sensor that produced the raw data.
     *
     * @return the sensor
     */
    public final Sensor getSensor() {
        return sensor;
    }

    /**
     * Sets the sensor that produced the raw data.
     *
     * @param sensor the sensor to set
     */
    public final void setSensor(final Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * Returns the timestamp of the raw data.
     *
     * @return the timestamp
     */
    public final LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the raw data.
     *
     * @param timestamp the timestamp to set
     */
    public final void setTimestamp(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the source of the raw data.
     *
     * @return the source
     */
    public final DataSource getSource() {
        return source;
    }

    /**
     * Sets the source of the raw data.
     *
     * @param source the source to set
     */
    public final void setSource(final DataSource source) {
        this.source = source;
    }

    /**
     * Returns the raw payload data.
     *
     * @return the raw payload
     */
    public final String getRawPayload() {
        return rawPayload;
    }

    /**
     * Sets the raw payload data.
     *
     * @param rawPayload the raw payload to set
     */
    public final void setRawPayload(final String rawPayload) {
        this.rawPayload = rawPayload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof final RawData rawData)) {
            return false;
        }
        return Objects.equals(getId(), rawData.getId());
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
        return new StringJoiner(", ", RawData.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("timestamp=" + timestamp)
                .add("source=" + source)
                .toString();
    }
}
