package com.kalitron.studio.domain.enumeration;

/**
 * The MaterialKind enumeration.
 */
public enum MaterialKind {
    MDF,
    MELAMINE("Melamina"),
    PLYWOOD("Triplay"),
    SOLID_WOOD("Madera sólida"),
    LAMINATE("Laminado"),
    COUNTERTOP("Cubierta"),
    OTHER("Otro");

    private String value;

    MaterialKind() {}

    MaterialKind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
