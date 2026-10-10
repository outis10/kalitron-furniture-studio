package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.domain.Authority;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.User;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.AuthorityRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.UserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;

/** Users and sessions for the E12 #116 measurer ITs. */
final class MeasurerTestData {

    private MeasurerTestData() {}

    static User user(UserRepository users, AuthorityRepository authorities, String login, boolean activated, String... roles) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        user.setActivated(activated);
        user.setEmail(login + "@kalitron.test");
        user.setFirstName("Nombre " + login);
        user.setLastName("Apellido");
        user.setLangKey("es");
        Set<Authority> granted = Arrays.stream(roles)
            .map(r -> authorities.findById(r).orElseThrow())
            .collect(Collectors.toCollection(HashSet::new));
        user.setAuthorities(granted);
        return users.saveAndFlush(user);
    }

    static DesignSession session(DesignSessionRepository sessions, SessionStatus status, User measurer) {
        Instant now = Instant.now();
        DesignSession session = new DesignSession()
            .sessionCode("KD-IT-" + RandomStringUtils.insecure().nextAlphanumeric(8).toUpperCase())
            .projectType(ProjectType.KITCHEN)
            .status(status)
            .clientName("Cliente IT")
            .clientEmail("cliente@kalitron.test")
            .clientPhone("5550000000")
            .notes("notas privadas")
            .createdAt(now)
            .updatedAt(now)
            .assignedMeasurer(measurer)
            .measurerAssignedAt(measurer != null ? now : null);
        return sessions.saveAndFlush(session);
    }
}
