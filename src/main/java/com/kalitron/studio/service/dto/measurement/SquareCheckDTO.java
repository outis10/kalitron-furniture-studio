package com.kalitron.studio.service.dto.measurement;

import java.io.Serializable;

/**
 * Diagonal method: points at {@code legAMm} / {@code legBMm} from the corner on
 * each wall and the measured diagonal between them (KFS-APP#27).
 */
public record SquareCheckDTO(
    SquareCheckStatus status,
    Integer legAMm,
    Integer legBMm,
    MeasuredValueDTO diagonalMm
) implements Serializable {
    /** Angle in whole degrees by the law of cosines; null when not computable. */
    public Integer computedAngleDeg() {
        if (legAMm == null || legBMm == null || !MeasuredValueDTO.present(diagonalMm) || legAMm <= 0 || legBMm <= 0) {
            return null;
        }
        double a = legAMm;
        double b = legBMm;
        double d = diagonalMm.value();
        double cos = (a * a + b * b - d * d) / (2 * a * b);
        if (cos < -1 || cos > 1) {
            return null;
        }
        return (int) Math.round(Math.toDegrees(Math.acos(cos)));
    }
}
