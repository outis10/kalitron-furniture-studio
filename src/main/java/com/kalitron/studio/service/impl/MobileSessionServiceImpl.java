package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.security.SecurityUtils;
import com.kalitron.studio.service.MobileSessionService;
import com.kalitron.studio.service.SessionAccessService;
import com.kalitron.studio.service.dto.mobile.MobileSessionDTO;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MobileSessionServiceImpl implements MobileSessionService {

    /** Closed sessions are not offered for measurement. */
    static final Set<SessionStatus> CLOSED_STATUSES = EnumSet.of(SessionStatus.COMPLETED, SessionStatus.ARCHIVED);

    private final DesignSessionRepository designSessionRepository;
    private final SessionAccessService sessionAccessService;

    public MobileSessionServiceImpl(DesignSessionRepository designSessionRepository, SessionAccessService sessionAccessService) {
        this.designSessionRepository = designSessionRepository;
        this.sessionAccessService = sessionAccessService;
    }

    @Override
    public List<MobileSessionDTO> findAssignedToCurrentUser() {
        return SecurityUtils.getCurrentUserLogin()
            .map(login ->
                designSessionRepository
                    .findByAssignedMeasurerLoginAndStatusNotInOrderByMeasurerAssignedAtDesc(login, CLOSED_STATUSES)
                    .stream()
                    .map(MobileSessionServiceImpl::toDto)
                    .toList()
            )
            .orElse(List.of());
    }

    @Override
    public Page<MobileSessionDTO> findAllOpen(String measurerLogin, Pageable pageable) {
        Page<DesignSession> page = (measurerLogin == null || measurerLogin.isBlank())
            ? designSessionRepository.findByStatusNotIn(CLOSED_STATUSES, pageable)
            : designSessionRepository.findByAssignedMeasurerLoginAndStatusNotIn(measurerLogin.toLowerCase(), CLOSED_STATUSES, pageable);
        return page.map(MobileSessionServiceImpl::toDto);
    }

    @Override
    public MobileSessionDTO findOne(Long sessionId) {
        return toDto(sessionAccessService.requireMeasurementAccess(sessionId));
    }

    static MobileSessionDTO toDto(DesignSession session) {
        // latestMeasurement stays null until SiteMeasurement exists (#107)
        return new MobileSessionDTO(
            session.getId(),
            session.getSessionCode(),
            session.getProjectType(),
            session.getStatus(),
            session.getClientName(),
            session.getMeasurerAssignedAt(),
            null
        );
    }
}
