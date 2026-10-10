package com.kalitron.studio.service.dto.measurer;

import java.io.Serializable;

/**
 * Result of assigning or unassigning the measurer of a session (E12 #116).
 * {@code assignedMeasurer} is null when the session is unassigned.
 */
public record MeasurerAssignmentDTO(Long sessionId, MeasurerSummaryDTO assignedMeasurer) implements Serializable {}
