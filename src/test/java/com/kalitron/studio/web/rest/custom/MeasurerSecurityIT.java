package com.kalitron.studio.web.rest.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * URL rules from E12 #116: an account with only ROLE_MEASURER reaches the mobile and
 * catalog endpoints but none of the generated CRUD endpoints.
 */
@IntegrationTest
@AutoConfigureMockMvc
class MeasurerSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(
        strings = {
            "/api/design-sessions",
            "/api/chat-messages",
            "/api/quotes",
            "/api/quote-items",
            "/api/design-images",
            "/api/design-artifacts",
            "/api/kitchen-specs",
            "/api/cabinets",
        }
    )
    @WithMockUser(username = "solo-medidor-it", authorities = AuthoritiesConstants.MEASURER)
    void measurerOnlyAccountCannotReachGeneratedCrud(String url) throws Exception {
        mockMvc.perform(get(url)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "solo-medidor-it", authorities = AuthoritiesConstants.MEASURER)
    void measurerOnlyAccountReachesMobileAndCatalog() throws Exception {
        mockMvc.perform(get("/api/mobile/sessions")).andExpect(status().isOk());
        mockMvc.perform(get("/api/croquis/catalog")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void regularUserKeepsGeneratedCrudAccess() throws Exception {
        mockMvc.perform(get("/api/design-sessions")).andExpect(status().isOk());
        mockMvc.perform(get("/api/croquis/catalog")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = { AuthoritiesConstants.USER, AuthoritiesConstants.MEASURER })
    void combinedRolesKeepBothAccesses() throws Exception {
        mockMvc.perform(get("/api/design-sessions")).andExpect(status().isOk());
        mockMvc.perform(get("/api/mobile/sessions")).andExpect(status().isOk());
    }
}
