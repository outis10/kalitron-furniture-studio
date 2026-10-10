package com.kalitron.studio.web.rest.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.User;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.AuthorityRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.UserRepository;
import com.kalitron.studio.security.AuthoritiesConstants;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class MobileSessionResourceIT {

    private static final String URL = "/api/mobile/sessions";

    /** The only fields the mobile session DTO may expose (E12 #116). */
    private static final Set<String> ALLOWED_FIELDS = Set.of(
        "id",
        "sessionCode",
        "projectType",
        "status",
        "clientName",
        "assignedAt",
        "latestMeasurement"
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    private DesignSession mineOpen;
    private DesignSession mineClosed;
    private DesignSession others;

    @BeforeEach
    void setUp() {
        User ana = MeasurerTestData.user(userRepository, authorityRepository, "ana-it", true, AuthoritiesConstants.MEASURER);
        User beto = MeasurerTestData.user(userRepository, authorityRepository, "beto-it", true, AuthoritiesConstants.MEASURER);
        mineOpen = MeasurerTestData.session(designSessionRepository, SessionStatus.SPECS_READY, ana);
        mineClosed = MeasurerTestData.session(designSessionRepository, SessionStatus.COMPLETED, ana);
        others = MeasurerTestData.session(designSessionRepository, SessionStatus.CHATTING, beto);
    }

    @Test
    @WithMockUser(username = "ana-it", authorities = AuthoritiesConstants.MEASURER)
    void measurerSeesOnlyAssignedOpenSessions() throws Exception {
        mockMvc
            .perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(mineOpen.getId()))
            .andExpect(jsonPath("$[0].sessionCode").value(mineOpen.getSessionCode()))
            .andExpect(jsonPath("$[0].assignedAt").isNotEmpty())
            .andExpect(jsonPath("$[0].latestMeasurement").isEmpty());
    }

    @Test
    @WithMockUser(username = "ana-it", authorities = AuthoritiesConstants.MEASURER)
    void mobileDtoExposesOnlyWhitelistedFields() throws Exception {
        String json = mockMvc
            .perform(get(URL + "/" + mineOpen.getId()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        Set<String> fields = new HashSet<>();
        node.fieldNames().forEachRemaining(fields::add);
        assertThat(fields).isEqualTo(ALLOWED_FIELDS);
        assertThat(json).doesNotContain("cliente@kalitron.test", "5550000000", "notas privadas");
    }

    @Test
    @WithMockUser(username = "ana-it", authorities = AuthoritiesConstants.MEASURER)
    void measurerCannotOpenOthersSession() throws Exception {
        mockMvc.perform(get(URL + "/" + others.getId())).andExpect(status().isForbidden());
        mockMvc.perform(get(URL + "/" + Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ana-it", authorities = AuthoritiesConstants.MEASURER)
    void measurerCanOpenOwnClosedSessionDetail() throws Exception {
        mockMvc.perform(get(URL + "/" + mineClosed.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void adminSeesAllOpenSessionsAndCanFilterByMeasurer() throws Exception {
        mockMvc
            .perform(get(URL).param("size", "500"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + mineOpen.getId() + ")]").exists())
            .andExpect(jsonPath("$[?(@.id == " + others.getId() + ")]").exists())
            .andExpect(jsonPath("$[?(@.id == " + mineClosed.getId() + ")]").doesNotExist());

        mockMvc
            .perform(get(URL).param("measurer", "beto-it"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(others.getId()));

        mockMvc.perform(get(URL + "/" + others.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void regularUserCannotUseMobileEndpoints() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isForbidden());
        mockMvc.perform(get(URL + "/" + mineOpen.getId())).andExpect(status().isForbidden());
    }

    @Test
    void anonymousIsUnauthorized() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }
}
