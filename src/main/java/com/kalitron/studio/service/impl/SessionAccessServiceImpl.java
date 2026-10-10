package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.User;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.security.AuthoritiesConstants;
import com.kalitron.studio.security.SecurityUtils;
import com.kalitron.studio.service.SessionAccessService;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SessionAccessServiceImpl implements SessionAccessService {

    private final DesignSessionRepository designSessionRepository;

    public SessionAccessServiceImpl(DesignSessionRepository designSessionRepository) {
        this.designSessionRepository = designSessionRepository;
    }

    @Override
    public DesignSession requireMeasurementAccess(Long sessionId) {
        DesignSession session = designSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId));
        if (!canMeasure(session)) {
            throw new AccessDeniedException("Session " + sessionId + " is not assigned to the current user");
        }
        return session;
    }

    @Override
    public boolean canMeasure(DesignSession session) {
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return true;
        }
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.MEASURER)) {
            return false;
        }
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        User assigned = session.getAssignedMeasurer();
        return login.isPresent() && assigned != null && login.get().equals(assigned.getLogin());
    }
}
