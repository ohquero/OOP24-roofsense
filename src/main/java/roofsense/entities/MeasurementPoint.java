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
import roofsense.entities.validation.annotations.ValidCode;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Place on a {@link Roof} where {@link Sensor}s are installed.
 */
@Entity
@Table(name = "measurement_points")
public class MeasurementPoint {

    @Id
    @GeneratedValue
    private Long id;

    @ValidCode
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roof_id", nullable = false)
    private Roof roof;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "orientation", nullable = false, length = 2)
    private Orientation orientation;

    /**
     * Default constructor for JPA.
     */
    protected MeasurementPoint() {
        // Required by JPA
    }

    /**
     * Creates a new instance of {@code MeasurementPoint}.
     *
     * @param code        the unique code identifying the measurement point
     * @param roof        the roof where the measurement point is located
     * @param latitude    the geographic latitude
     * @param longitude   the geographic longitude
     * @param orientation the orientation of the measurement point
     */
    public MeasurementPoint(
            final String code, final Roof roof, final Double latitude, final Double longitude,
            final Orientation orientation
    ) {
        this.code = code;
        this.roof = roof;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orientation = orientation;
    }

    /**
     * Creates a new instance of {@link MeasurementPoint} by copying the values from another {@link MeasurementPoint}
     * instance.
     *
     * @param other the {@link MeasurementPoint} instance to copy values from
     */
    public MeasurementPoint(final MeasurementPoint other) {
        Objects.requireNonNull(other, "other must not be null");
        this.id = other.id;
        this.code = other.code;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.orientation = other.orientation;
        this.roof = other.roof;
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
     * Returns the measurement point code.
     *
     * @return the unique code of the measurement point
     */
    public final String getCode() {
        return code;
    }

    /**
     * Sets the unique code associated with the measurement point.
     *
     * @param code the unique code to set
     */
    public final void setCode(final String code) {
        this.code = code;
    }

    /**
     * Returns the geographic latitude.
     *
     * @return the latitude
     */
    public final Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the geographic latitude.
     *
     * @param latitude the latitude to set
     */
    public final void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the geographic longitude.
     *
     * @return the longitude
     */
    public final Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the geographic longitude.
     *
     * @param longitude the longitude to set
     */
    public final void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the orientation of the measurement point.
     *
     * @return the orientation
     */
    public final Orientation getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation of the measurement point.
     *
     * @param orientation the orientation to set
     */
    public final void setOrientation(final Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Returns the roof where the measurement point is located.
     *
     * @return the roof
     */
    public final Roof getRoof() {
        return roof;
    }

    /**
     * Sets the roof where the measurement point is located.
     *
     * @param roof the roof to set
     */
    public final void setRoof(final Roof roof) {
        this.roof = roof;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public final boolean equals(final Object o) {
        if (!(o instanceof final MeasurementPoint measurementPoint)) {
            return false;
        }
        return Objects.equals(getCode(), measurementPoint.getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return new StringJoiner(", ", MeasurementPoint.class.getSimpleName() + "[", "]")
                .add("code='" + code + "'")
                .add("latitude=" + latitude)
                .add("longitude=" + longitude)
                .add("orientation=" + orientation)
                .toString();
    }

}
