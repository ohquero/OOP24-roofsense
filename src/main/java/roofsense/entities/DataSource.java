package roofsense.entities;

/**
 * Represents the source of raw data.
 */
public enum DataSource {

    /**
     * Data from Lastem CSV files.
     */
    LASTEM_CSV,

    /**
     * Data from LoRa sensors via MQTT.
     */
    LORA_MQTT
}
