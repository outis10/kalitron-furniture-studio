package com.kalitron.studio.service.dto.measurement;

import java.io.Serializable;

/** Element on a wall (window, outlet, fridge…) identified by a croquis code. */
public record MeasurementElementDTO(
    String elementUuid,
    String code,
    MeasuredValueDTO xMm,
    MeasuredValueDTO yMm,
    MeasuredValueDTO widthMm,
    MeasuredValueDTO heightMm,
    MeasuredValueDTO depthMm,
    String swing,
    String notes
) implements Serializable {}
