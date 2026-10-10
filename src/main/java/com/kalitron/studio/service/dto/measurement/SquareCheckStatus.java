package com.kalitron.studio.service.dto.measurement;

/** Outcome of the square check of a corner (KFS-APP#27). */
public enum SquareCheckStatus {
    /** Diagonal measured; angle computed. */
    VERIFIED,
    /** Measurer judged the corner square without measuring. */
    ASSUMED_SQUARE,
    /** Legs shorter than 300 mm: check not reliable, 90° assumed. */
    NOT_VERIFIABLE,
}
