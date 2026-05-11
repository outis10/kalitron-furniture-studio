package com.kalitron.studio.domain.enumeration;

/**
 * The MessageRole enumeration.
 */
public enum MessageRole {
    USER("Usuario"),
    ASSISTANT("Asistente"),
    SYSTEM("Sistema");

    private final String value;

    MessageRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
