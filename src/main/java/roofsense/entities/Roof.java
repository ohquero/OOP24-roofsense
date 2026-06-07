package roofsense.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import roofsense.entities.validation.annotations.ValidCode;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a roof associated with a building.
 */
@Entity
@Table(name = "roofs")
public class Roof {

    @Id
    @GeneratedValue
    private Long id;

    @ValidCode
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotBlank(message = "{validation.not-blank}")
    @Column(name = "building_address", nullable = false)
    private String buildingAddress;

    /**
     * Creates a new instance of {@code Roof} with attributes initialized to {@code null}.
     */
    public Roof() {
        // Intentional empty constructor
    }

    /**
     * Creates a new instance of {@code Roof} with attributes initialized to provided value.
     *
     * @param code            the unique code identifying the roof
     * @param buildingAddress the address of the building associated with the roof
     */
    public Roof(final String code, final String buildingAddress) {
        this.code = code;
        this.buildingAddress = buildingAddress;
    }

    /**
     * Creates a new instance of {@link Roof} by copying the values from another {@link Roof} instance.
     *
     * @param other the {@link Roof} instance to copy values from
     */
    public Roof(final Roof other) {
        Objects.requireNonNull(other, "other must not be null");
        this.id = other.id;
        this.code = other.code;
        this.buildingAddress = other.buildingAddress;
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
     * Returns the roof code.
     *
     * @return the unique code of the roof
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the unique code associated with the roof.
     *
     * @param code the unique code to set
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * Returns the address of the building associated with the roof.
     *
     * @return the address of the building
     */
    public String getBuildingAddress() {
        return buildingAddress;
    }

    /**
     * Sets the address of the building associated with the roof.
     *
     * @param buildingAddress the address of the building to set
     */
    public void setBuildingAddress(final String buildingAddress) {
        this.buildingAddress = buildingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public boolean equals(final Object o) {
        if (!(o instanceof final Roof roof)) {
            return false;
        }
        return Objects.equals(getCode(), roof.getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", Roof.class.getSimpleName() + "[", "]").add("code='" + code + "'").toString();
    }

}
