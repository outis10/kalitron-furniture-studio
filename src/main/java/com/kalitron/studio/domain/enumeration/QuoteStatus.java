package com.kalitron.studio.domain.enumeration;

/**
 * The QuoteStatus enumeration.
 */
public enum QuoteStatus {
    DRAFT("Borrador"),
    SENT("Enviada"),
    APPROVED("Aprobada"),
    REJECTED("Rechazada"),
    EXPIRED("Expirada");

    private final String value;

    QuoteStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
