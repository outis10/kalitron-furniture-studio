package com.kalitron.studio.domain.enumeration;

/**
 * The SessionStatus enumeration.
 */
public enum SessionStatus {
    DRAFT("Borrador"),
    CHATTING("En conversación"),
    SPECS_READY("Especificaciones listas"),
    VISUAL_GENERATED("Visual generado"),
    LAYOUT_CONFIRMED("Layout confirmado"),
    BOM_GENERATED("BOM generado"),
    QUOTE_GENERATED("Cotización generada"),
    FUSION_GENERATED("Fusion 360 generado"),
    COMPLETED("Completado"),
    ARCHIVED("Archivado");

    private final String value;

    SessionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
