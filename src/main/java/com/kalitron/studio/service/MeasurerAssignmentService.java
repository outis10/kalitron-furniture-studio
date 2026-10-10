package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.measurer.MeasurerAssignmentDTO;
import com.kalitron.studio.service.dto.measurer.MeasurerSummaryDTO;
import java.util.List;

/**
 * Assigns the site measurer of a design session (E12 #116).
 */
public interface MeasurerAssignmentService {
    /**
     * Assigns {@code userLogin} as the session's measurer, or unassigns when it is null.
     *
     * @throws MeasurerAssignmentException when the session or user does not exist, or the user
     *     is not an activated measurer.
     */
    MeasurerAssignmentDTO assign(Long sessionId, String userLogin);

    /** Activated users with {@code ROLE_MEASURER}, sorted by name. */
    List<MeasurerSummaryDTO> findAssignableMeasurers();
}
