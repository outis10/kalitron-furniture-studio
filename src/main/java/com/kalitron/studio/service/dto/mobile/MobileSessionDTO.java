package com.kalitron.studio.service.dto.mobile;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import java.io.Serializable;
import java.time.Instant;

/**
 * Minimal session view for the mobile app (E12 #116). Must never carry client
 * contact data, notes, chat, images, quotes or artifacts.
 */
public record MobileSessionDTO(
    Long id,
    String sessionCode,
    ProjectType projectType,
    SessionStatus status,
    String clientName,
    Instant assignedAt,
    MobileMeasurementSummaryDTO latestMeasurement
) implements Serializable {}
