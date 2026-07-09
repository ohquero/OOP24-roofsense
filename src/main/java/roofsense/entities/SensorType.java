package roofsense.entities;

/**
 * Represents the type of sensor used for measurements.
 */
public enum SensorType {

    /**
     * Omega PR-10 temperature probe.
     */
    OMEGA_PR_10("Omega PR-10"),

    /**
     * Hukseflux HFP01 heat flux sensor.
     */
    HUKSeflux_HFP01("Hukseflux HFP01"),

    /**
     * Dragino LHT65 LoRa temperature and humidity sensor.
     */
    DRAGINO_LHT65("Dragino LHT65"),

    /**
     * Milesight EM300-TH LoRa temperature and humidity sensor.
     */
    MILESIGHT_EM300_TH("Milesight EM300-TH");

    private final String description;

    SensorType(final String description) {
        this.description = description;
    }

    /**
     * Returns the description of the sensor type.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
