package com.kalitron.studio.service.dto.measurement;

import java.io.Serializable;

/** Corner between two walls, e.g. {@code E-AB}; angle recorded when ≠ 90°. */
public record MeasurementCornerDTO(String cornerCode, Integer angleDeg) implements Serializable {}
