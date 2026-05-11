package com.kalitron.studio.domain.enumeration;

/**
 * The GenerationJobType enumeration.
 */
public enum GenerationJobType {
    AI_CHAT("Chat IA"),
    AI_SPEC_EXTRACTION("Extracción de especificaciones"),
    AI_RENDER("Render IA"),
    BOM,
    QUOTE("Cotización"),
    FUSION_MODEL("Modelo Fusion 360"),
    EXPORT("Exportación");

    private String value;

    GenerationJobType() {}

    GenerationJobType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
