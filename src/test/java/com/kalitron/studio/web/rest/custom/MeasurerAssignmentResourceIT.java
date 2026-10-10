package com.kalitron.studio.web.rest.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.User;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.AuthorityRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.UserRepository;
import com.kalitron.studio.security.AuthoritiesConstants;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class MeasurerAssignmentResourceIT {

    private static final String ADMIN = AuthoritiesConstants.ADMIN;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    @Autowired
    private CacheManager cacheManager;

    private User measurer;
    private DesignSession session;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).clear();
        measurer = MeasurerTestData.user(userRepository, authorityRepository, "ana-it", true, AuthoritiesConstants.MEASURER);
        session = MeasurerTestData.session(designSessionRepository, SessionStatus.SPECS_READY, null);
    }

    private String url(Long id) {
        return "/api/design-sessions/" + id + "/assigned-measurer";
    }

    private static String body(String login) {
        return login == null ? "{\"userLogin\":null}" : "{\"userLogin\":\"" + login + "\"}";
    }

    @Test
    @WithMockUser(authorities = ADMIN)
    void adminAssignsMeasurer() throws Exception {
        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("ana-it")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.assignedMeasurer.login").value("ana-it"))
            .andExpect(jsonPath("$.assignedMeasurer.email").doesNotExist());

        DesignSession saved = designSessionRepository.findById(session.getId()).orElseThrow();
        assertThat(saved.getAssignedMeasurer().getLogin()).isEqualTo("ana-it");
        assertThat(saved.getMeasurerAssignedAt()).isNotNull();
    }

    @Test
    @WithMockUser(authorities = ADMIN)
    void assigningSameMeasurerTwiceKeepsAssignmentTime() throws Exception {
        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("ana-it")))
            .andExpect(status().isOk());
        var first = designSessionRepository.findById(session.getId()).orElseThrow().getMeasurerAssignedAt();

        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("ana-it")))
            .andExpect(status().isOk());

        assertThat(designSessionRepository.findById(session.getId()).orElseThrow().getMeasurerAssignedAt()).isEqualTo(first);
    }

    @Test
    @WithMockUser(authorities = ADMIN)
    void adminUnassignsMeasurer() throws Exception {
        DesignSession assigned = MeasurerTestData.session(designSessionRepository, SessionStatus.SPECS_READY, measurer);

        mockMvc
            .perform(put(url(assigned.getId())).contentType(MediaType.APPLICATION_JSON).content(body(null)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.assignedMeasurer").isEmpty());

        DesignSession saved = designSessionRepository.findById(assigned.getId()).orElseThrow();
        assertThat(saved.getAssignedMeasurer()).isNull();
        assertThat(saved.getMeasurerAssignedAt()).isNull();
    }

    @Test
    @WithMockUser(authorities = ADMIN)
    void rejectsUserWithoutMeasurerRole() throws Exception {
        MeasurerTestData.user(userRepository, authorityRepository, "luis-it", true, AuthoritiesConstants.USER);

        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("luis-it")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.USER_NOT_MEASURER"));
    }

    @Test
    @WithMockUser(authorities = ADMIN)
    void rejectsInactiveMeasurer() throws Exception {
        MeasurerTestData.user(userRepository, authorityRepository, "inactiva-it", false, AuthoritiesConstants.MEASURER);

        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("inactiva-it")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.USER_NOT_ACTIVATED"));
    }

    @Test
    @WithMockUser(authorities = ADMIN)
    void returnsNotFoundForUnknownSessionOrUser() throws Exception {
        mockMvc
            .perform(put(url(Long.MAX_VALUE)).contentType(MediaType.APPLICATION_JSON).content(body("ana-it")))
            .andExpect(status().isNotFound());
        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("nadie-it")))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void regularUserCannotAssign() throws Exception {
        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("ana-it")))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/measurers")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ana-it", authorities = AuthoritiesConstants.MEASURER)
    void measurerCannotAssign() throws Exception {
        mockMvc
            .perform(put(url(session.getId())).contentType(MediaType.APPLICATION_JSON).content(body("ana-it")))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = ADMIN)
    void listsOnlyActivatedMeasurersWithoutContactData() throws Exception {
        MeasurerTestData.user(userRepository, authorityRepository, "inactiva2-it", false, AuthoritiesConstants.MEASURER);
        MeasurerTestData.user(userRepository, authorityRepository, "luis2-it", true, AuthoritiesConstants.USER);

        mockMvc
            .perform(get("/api/measurers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.login == 'ana-it')]").exists())
            .andExpect(jsonPath("$[?(@.login == 'inactiva2-it')]").doesNotExist())
            .andExpect(jsonPath("$[?(@.login == 'luis2-it')]").doesNotExist())
            .andExpect(jsonPath("$[0].email").doesNotExist());
    }
}
