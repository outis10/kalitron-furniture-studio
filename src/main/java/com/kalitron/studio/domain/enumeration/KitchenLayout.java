package com.kalitron.studio.domain.enumeration;

/**
 * The KitchenLayout enumeration.
 */
public enum KitchenLayout {
    LINEAR("Lineal"),
    L_SHAPE("En L"),
    U_SHAPE("En U"),
    ISLAND("Con isla"),
    PENINSULA("Con península"),
    GALLEY("Pasillo"),
    CUSTOM("Personalizado");

    private final String value;

    KitchenLayout(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
