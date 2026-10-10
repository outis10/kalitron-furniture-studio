package com.kalitron.studio.service.dto.croquis;

/**
 * Data conditions a rule needs; when unmet the rule is skipped for that
 * target (no issue) — the missing data is reported by its own rule (E12 #113).
 */
public enum RulePrerequisite {
    WALL_COMPLETE,
    ELEMENT_HAS_XY,
    ELEMENT_HAS_WIDTH,
    CODE_KNOWN,
    ROOM_CLOSED,
    MEASUREMENT_PRESENT,
}
