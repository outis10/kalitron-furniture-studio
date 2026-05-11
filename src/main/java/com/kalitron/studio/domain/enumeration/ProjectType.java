package com.kalitron.studio.domain.enumeration;

/**
 * The ProjectType enumeration.
 */
public enum ProjectType {
    KITCHEN("Cocina"),
    CLOSET("Closet"),
    BOTH("Cocina y closet");

    private final String value;

    ProjectType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
