package com.kalitron.studio.domain.enumeration;

/**
 * The GenerationJobStatus enumeration.
 */
public enum GenerationJobStatus {
    PENDING("Pendiente"),
    RUNNING("En ejecución"),
    DONE("Terminado"),
    FAILED("Fallido"),
    CANCELLED("Cancelado");

    private final String value;

    GenerationJobStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
