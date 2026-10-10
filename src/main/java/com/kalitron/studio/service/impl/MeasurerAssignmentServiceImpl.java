package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.Authority;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.User;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.UserRepository;
import com.kalitron.studio.security.AuthoritiesConstants;
import com.kalitron.studio.security.SecurityUtils;
import com.kalitron.studio.service.MeasurerAssignmentException;
import com.kalitron.studio.service.MeasurerAssignmentException.Reason;
import com.kalitron.studio.service.MeasurerAssignmentService;
import com.kalitron.studio.service.dto.measurer.MeasurerAssignmentDTO;
import com.kalitron.studio.service.dto.measurer.MeasurerSummaryDTO;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeasurerAssignmentServiceImpl implements MeasurerAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(MeasurerAssignmentServiceImpl.class);

    private final DesignSessionRepository designSessionRepository;
    private final UserRepository userRepository;

    public MeasurerAssignmentServiceImpl(DesignSessionRepository designSessionRepository, UserRepository userRepository) {
        this.designSessionRepository = designSessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MeasurerAssignmentDTO assign(Long sessionId, String userLogin) {
        DesignSession session = designSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new MeasurerAssignmentException(Reason.SESSION_NOT_FOUND, "Session not found: " + sessionId));
        String previous = session.getAssignedMeasurer() != null ? session.getAssignedMeasurer().getLogin() : null;

        if (userLogin == null || userLogin.isBlank()) {
            session.setAssignedMeasurer(null);
            session.setMeasurerAssignedAt(null);
            logChange(session, previous, null);
            return new MeasurerAssignmentDTO(session.getId(), null);
        }

        User user = userRepository
            .findOneWithAuthoritiesByLogin(userLogin.toLowerCase())
            .orElseThrow(() -> new MeasurerAssignmentException(Reason.USER_NOT_FOUND, "User not found: " + userLogin));
        if (!user.isActivated()) {
            throw new MeasurerAssignmentException(Reason.USER_NOT_ACTIVATED, "User is not activated: " + userLogin);
        }
        if (user.getAuthorities().stream().map(Authority::getName).noneMatch(AuthoritiesConstants.MEASURER::equals)) {
            throw new MeasurerAssignmentException(Reason.USER_NOT_MEASURER, "User does not have " + AuthoritiesConstants.MEASURER);
        }

        if (!Objects.equals(previous, user.getLogin())) {
            // the login lookup may come from the cache; assign a managed reference
            session.setAssignedMeasurer(userRepository.getReferenceById(user.getId()));
            session.setMeasurerAssignedAt(Instant.now());
            logChange(session, previous, user.getLogin());
        }
        return new MeasurerAssignmentDTO(session.getId(), toSummary(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeasurerSummaryDTO> findAssignableMeasurers() {
        return userRepository.findAllActivatedWithAuthority(AuthoritiesConstants.MEASURER).stream().map(this::toSummary).toList();
    }

    private MeasurerSummaryDTO toSummary(User user) {
        return new MeasurerSummaryDTO(user.getLogin(), user.getFirstName(), user.getLastName());
    }

    private void logChange(DesignSession session, String previous, String next) {
        LOG.info(
            "Measurer assignment: session={} from={} to={} by={}",
            session.getSessionCode(),
            previous,
            next,
            SecurityUtils.getCurrentUserLogin().orElse("unknown")
        );
    }
}
