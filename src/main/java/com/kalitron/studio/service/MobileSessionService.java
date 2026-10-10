package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.mobile.MobileSessionDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Sessions as seen by the mobile app (E12 #116).
 */
public interface MobileSessionService {
    /** Non-closed sessions assigned to the current user. */
    List<MobileSessionDTO> findAssignedToCurrentUser();

    /** Admin view: all non-closed sessions, optionally only those assigned to {@code measurerLogin}. */
    Page<MobileSessionDTO> findAllOpen(String measurerLogin, Pageable pageable);

    /**
     * @throws org.springframework.security.access.AccessDeniedException if the current user may not measure it.
     * @throws java.util.NoSuchElementException if the session does not exist.
     */
    MobileSessionDTO findOne(Long sessionId);
}
