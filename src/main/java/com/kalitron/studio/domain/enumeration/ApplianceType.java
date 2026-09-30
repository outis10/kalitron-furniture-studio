package com.kalitron.studio.domain.enumeration;

/**
 * The ApplianceType enumeration.
 */
public enum ApplianceType {
    FRIDGE("Refrigerador"),
    RANGE("Estufa"),
    COOKTOP("Parrilla"),
    OVEN("Horno"),
    MICROWAVE("Microondas"),
    DISHWASHER("Lavavajillas"),
    SINK("Tarja (espacio)");

    private final String value;

    ApplianceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
