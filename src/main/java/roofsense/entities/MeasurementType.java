package roofsense.entities;

/**
 * Represents the type of environmental measurement.
 */
public enum MeasurementType {

    /**
     * Temperature at 10 cm from the external roof surface in degrees Celsius.
     */
    TEMPERATURE_10CM("T_10cm", "°C"),

    /**
     * Temperature of the external roof surface in degrees Celsius.
     */
    TEMPERATURE_EXTERNAL_SURFACE("T_ext", "°C"),

    /**
     * Temperature of the internal roof surface (ceiling of the room below) in degrees Celsius.
     */
    TEMPERATURE_INTERNAL_SURFACE("T_int", "°C"),

    /**
     * Heat flux entering/leaving the roof in W/m².
     */
    HEAT_FLUX("Flux", "W/m²");

    private final String code;
    private final String unit;

    MeasurementType(final String code, final String unit) {
        this.code = code;
        this.unit = unit;
    }

    /**
     * Returns the code of the measurement type.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the unit of measurement.
     *
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }
}
