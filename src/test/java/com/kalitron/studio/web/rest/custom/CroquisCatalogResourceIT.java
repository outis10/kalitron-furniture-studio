package com.kalitron.studio.web.rest.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.service.CroquisCatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc
class CroquisCatalogResourceIT {

    private static final String URL = "/api/croquis/catalog";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CroquisCatalogService croquisCatalogService;

    @Test
    @WithMockUser
    void returnsCatalogWithEtag() throws Exception {
        String version = croquisCatalogService.getCatalogVersion();

        mockMvc
            .perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ETAG, "\"" + version + "\""))
            .andExpect(jsonPath("$.catalogVersion").value(version))
            .andExpect(jsonPath("$.entries[?(@.code == 'V')].labelEsMx").value("Ventana"))
            .andExpect(jsonPath("$.entries[?(@.code == 'CA')].obstacleType").value("RANGE_HOOD"))
            .andExpect(jsonPath("$.entries[?(@.code == 'RF')].applianceType").value("FRIDGE"))
            .andExpect(jsonPath("$.validationRules[?(@.code == 'WALL_CLOSURE_MISMATCH')].ruleSet").value("MEASUREMENT"));
    }

    @Test
    @WithMockUser
    void returnsNotModifiedWhenEtagMatches() throws Exception {
        String etag = "\"" + croquisCatalogService.getCatalogVersion() + "\"";

        mockMvc
            .perform(get(URL).header(HttpHeaders.IF_NONE_MATCH, etag))
            .andExpect(status().isNotModified())
            .andExpect(header().string(HttpHeaders.ETAG, etag));
    }

    @Test
    @WithMockUser
    void returnsCatalogWhenEtagIsStale() throws Exception {
        mockMvc.perform(get(URL).header(HttpHeaders.IF_NONE_MATCH, "\"2000-01-01.1\"")).andExpect(status().isOk());
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }
}
