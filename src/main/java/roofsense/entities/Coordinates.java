package roofsense.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a pair of geographic coordinates (latitude and longitude).
 */
@Embeddable
public class Coordinates {

    @NotNull(message = "{validation.not-blank}")
    @DecimalMin(value = "-90.0", message = "{validation.latitude.range}")
    @DecimalMax(value = "90.0", message = "{validation.latitude.range}")
    @Column(name = "latitude")
    private Double latitude;

    @NotNull(message = "{validation.not-blank}")
    @DecimalMin(value = "-180.0", message = "{validation.longitude.range}")
    @DecimalMax(value = "180.0", message = "{validation.longitude.range}")
    @Column(name = "longitude")
    private Double longitude;

    /**
     * Creates a new instance of {@code Coordinates} with attributes initialized to {@code null}.
     */
    public Coordinates() {
        // Intentional empty constructor
    }

    /**
     * Creates a new instance of {@code Coordinates} with attributes initialized to provided value.
     *
     * @param latitude  the latitude coordinate
     * @param longitude the longitude coordinate
     */
    public Coordinates(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Creates a new instance of {@link Coordinates} by copying the values from another instance.
     *
     * @param other the {@link Coordinates} instance to copy values from
     */
    public Coordinates(final Coordinates other) {
        Objects.requireNonNull(other, "other must not be null");
        this.latitude = other.latitude;
        this.longitude = other.longitude;
    }

    /**
     * Returns the latitude coordinate.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate.
     *
     * @param latitude the latitude to set
     */
    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude coordinate.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate.
     *
     * @param longitude the longitude to set
     */
    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Coordinates coordinates)) {
            return false;
        }
        return Objects.equals(getLatitude(), coordinates.getLatitude())
                && Objects.equals(getLongitude(), coordinates.getLongitude());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", Coordinates.class.getSimpleName() + "[", "]")
                .add("latitude=" + latitude)
                .add("longitude=" + longitude)
                .toString();
    }

}
