package com.kalitron.studio.service.dto.croquis;

/**
 * Closed set of rule kinds implemented by the Java and Dart engines (E12 #113).
 * A new kind increments the engine version and requires a catalog
 * {@code minAppVersion} bump.
 */
public enum RuleKind {
    SUM_WITHIN_TOLERANCE(1),
    SPREAD_WITHIN_TOLERANCE(1),
    THRESHOLD_EXCEEDED(1),
    REQUIRED_FIELDS(1),
    WITHIN_BOUNDS(1),
    CODE_IN_CATALOG(1),
    SOURCE_IS(1),
    HAS_ATTACHMENT(1),
    NO_OVERLAP(1);

    /** Engine version currently implemented by Studio. */
    public static final int CURRENT_ENGINE_VERSION = 1;

    private final int sinceEngineVersion;

    RuleKind(int sinceEngineVersion) {
        this.sinceEngineVersion = sinceEngineVersion;
    }

    public int getSinceEngineVersion() {
        return sinceEngineVersion;
    }
}
