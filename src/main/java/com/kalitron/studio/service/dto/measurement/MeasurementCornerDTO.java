package com.kalitron.studio.service.dto.measurement;

import java.io.Serializable;

/**
 * Corner between two walls, e.g. {@code E-AB}. {@code angleDeg} is computed by
 * the app from the diagonal method ({@link SquareCheckDTO}); Studio recomputes
 * it on sync (#112).
 */
public record MeasurementCornerDTO(String cornerCode, Integer angleDeg, SquareCheckDTO squareCheck) implements Serializable {}
