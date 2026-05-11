package com.kalitron.studio.domain.enumeration;

/**
 * The RoomObstacleType enumeration.
 */
public enum RoomObstacleType {
    WINDOW("Ventana"),
    DOOR("Puerta"),
    COLUMN("Columna"),
    OUTLET("Contacto eléctrico"),
    WATER("Toma de agua"),
    GAS("Toma de gas"),
    DRAIN("Drenaje"),
    RANGE_HOOD("Campana"),
    APPLIANCE("Electrodoméstico"),
    OTHER("Otro");

    private final String value;

    RoomObstacleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
