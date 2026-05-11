package com.kalitron.studio.domain.enumeration;

/**
 * The ImageType enumeration.
 */
public enum ImageType {
    REFERENCE("Referencia"),
    CATALOG("Catálogo"),
    AI_RENDER("Render IA"),
    FUSION_RENDER("Render Fusion 360"),
    SKETCH("Boceto");

    private final String value;

    ImageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
