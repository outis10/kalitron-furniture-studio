package com.kalitron.studio.domain.enumeration;

/**
 * The ArtifactType enumeration.
 */
public enum ArtifactType {
    CSV,
    FUSION_SCRIPT("Script Fusion 360"),
    FUSION_MODEL("Modelo Fusion 360"),
    STEP,
    DXF,
    PDF,
    BOM_JSON("BOM JSON"),
    QUOTE_PDF("PDF de cotización"),
    OTHER("Otro");

    private String value;

    ArtifactType() {}

    ArtifactType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
