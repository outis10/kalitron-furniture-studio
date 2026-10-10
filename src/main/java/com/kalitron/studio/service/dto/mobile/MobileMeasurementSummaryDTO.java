package com.kalitron.studio.service.dto.mobile;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Latest site measurement of a session, as listed in the mobile app (E12 #116).
 * Filled once {@code SiteMeasurement} exists (#107).
 */
public record MobileMeasurementSummaryDTO(
    UUID measurementUuid,
    Integer revision,
    String status,
    Instant capturedAt
) implements Serializable {}
