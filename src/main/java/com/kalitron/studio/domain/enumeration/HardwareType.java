package com.kalitron.studio.domain.enumeration;

/**
 * The HardwareType enumeration.
 */
public enum HardwareType {
    HINGE("Bisagra"),
    SLIDE("Corredera"),
    HANDLE("Jaladera"),
    PULL("Tirador"),
    LEG("Pata"),
    SCREW("Tornillo"),
    BRACKET("Escuadra"),
    OTHER("Otro");

    private final String value;

    HardwareType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
