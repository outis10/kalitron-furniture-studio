package com.kalitron.studio.domain.enumeration;

/**
 * The CabinetCategory enumeration.
 */
public enum CabinetCategory {
    UPPER("Gabinete superior"),
    LOWER("Gabinete inferior"),
    CORNER("Esquinero"),
    TALL("Torre"),
    SINK("Tarja"),
    ISLAND("Isla"),
    DRAWER_BASE("Base con cajones"),
    APPLIANCE("Módulo de electrodoméstico"),
    FILLER("Relleno"),
    PANEL("Panel");

    private final String value;

    CabinetCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
