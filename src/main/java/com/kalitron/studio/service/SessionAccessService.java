package com.kalitron.studio.service;

import com.kalitron.studio.domain.DesignSession;

/**
 * Session-level authorization for on-site measurement (E12 #116): admins, or the
 * measurer assigned to the session. Used by the mobile session endpoints, the sync
 * API (#112) and the backup sheet (#106).
 */
public interface SessionAccessService {
    /**
     * Loads the session if the current user may measure it.
     *
     * @throws org.springframework.security.access.AccessDeniedException if not admin and not the assigned measurer.
     * @throws java.util.NoSuchElementException if the session does not exist.
     */
    DesignSession requireMeasurementAccess(Long sessionId);

    boolean canMeasure(DesignSession session);
}
