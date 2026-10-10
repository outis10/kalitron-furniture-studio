package com.kalitron.studio.domain.enumeration;

/**
 * Lifecycle of an on-site measurement (E12 #107).
 */
public enum SiteMeasurementStatus {
    DRAFT("Borrador"),
    CONFIRMED("Confirmada"),
    SUPERSEDED("Reemplazada");

    private final String value;

    SiteMeasurementStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
