package com.kalitron.studio.service.dto.measurement;

import java.io.Serializable;

/** A value in millimeters (or degrees) with its capture source. */
public record MeasuredValueDTO(Integer value, ValueSource source) implements Serializable {
    public static boolean present(MeasuredValueDTO measured) {
        return measured != null && measured.value() != null;
    }

    public static Integer valueOf(MeasuredValueDTO measured) {
        return measured == null ? null : measured.value();
    }
}
