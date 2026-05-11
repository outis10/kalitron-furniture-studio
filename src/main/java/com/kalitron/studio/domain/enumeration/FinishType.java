package com.kalitron.studio.domain.enumeration;

/**
 * The FinishType enumeration.
 */
public enum FinishType {
    MATTE_WHITE("Blanco mate"),
    MATTE_GRAY("Gris mate"),
    MATTE_BLACK("Negro mate"),
    WOOD_OAK("Madera roble"),
    WOOD_WALNUT("Madera nogal"),
    WOOD_PINE("Madera pino"),
    HIGH_GLOSS_WHITE("Blanco alto brillo"),
    CUSTOM("Personalizado");

    private final String value;

    FinishType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
