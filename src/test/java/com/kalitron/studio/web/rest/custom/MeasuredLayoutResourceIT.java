package com.kalitron.studio.web.rest.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.RoomWallRepository;
import com.kalitron.studio.service.dto.MeasuredLayoutRequestDTO;
import com.kalitron.studio.service.dto.MeasuredWallSegmentDTO;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
class MeasuredLayoutResourceIT {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    @Autowired
    private RoomWallRepository roomWallRepository;

    @Autowired
    private DesignArtifactRepository designArtifactRepository;

    @AfterEach
    void cleanup() {
        roomWallRepository.deleteAll();
        designArtifactRepository.deleteAll();
        designSessionRepository.deleteAll();
    }

    @Test
    void measuredLayoutEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/design-sessions/1/measured-layout")).andExpect(status().isUnauthorized());

        mockMvc
            .perform(put("/api/design-sessions/1/measured-layout").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void saveMeasuredLayoutStoresNormalizedWallsAndCanBeReopened() throws Exception {
        DesignSession session = designSessionRepository.save(
            new DesignSession()
                .sessionCode("KD-2026-039")
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.VISUAL_GENERATED)
                .clientName("Layout Client")
                .clientEmail("layout@example.com")
                .createdAt(Instant.parse("2026-05-20T10:00:00Z"))
                .updatedAt(Instant.parse("2026-05-20T10:00:00Z"))
        );

        MeasuredLayoutRequestDTO request = new MeasuredLayoutRequestDTO();
        request.setSessionId(session.getId());
        request.setLayout(KitchenLayout.L_SHAPE);
        request.setRoomHeightMm(2400);
        request.setDefaultBaseDepthMm(600);
        request.setDefaultUpperDepthMm(350);
        request.setNotes("Measured in site visit");
        request.setWalls(List.of(measuredWall("A", 3000, 0, 1), measuredWall("B", 2500, 90, 2)));

        mockMvc
            .perform(
                put("/api/design-sessions/{sessionId}/measured-layout", session.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.layout").value("L_SHAPE"))
            .andExpect(jsonPath("$.roomHeightMm").value(2400))
            .andExpect(jsonPath("$.walls[0].wallCode").value("A"))
            .andExpect(jsonPath("$.walls[0].lengthMm").value(3000));

        assertThat(roomWallRepository.findBySessionIdOrderBySortOrderAsc(session.getId())).hasSize(2);
        assertThat(
            designArtifactRepository.findFirstBySessionIdAndFileNameOrderByCreatedAtDesc(session.getId(), "measured-layout.json")
        ).isPresent();

        mockMvc
            .perform(get("/api/design-sessions/{sessionId}/measured-layout", session.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.layout").value("L_SHAPE"))
            .andExpect(jsonPath("$.walls").isArray())
            .andExpect(jsonPath("$.walls[1].wallCode").value("B"))
            .andExpect(jsonPath("$.walls[1].lengthMm").value(2500));
    }

    @Test
    @Transactional
    @WithMockUser
    void saveMeasuredLayoutRejectsLShapeWithOneWall() throws Exception {
        DesignSession session = designSessionRepository.save(
            new DesignSession()
                .sessionCode("KD-2026-040")
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.VISUAL_GENERATED)
                .clientName("Layout Client")
                .clientEmail("layout@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
        );

        MeasuredLayoutRequestDTO request = new MeasuredLayoutRequestDTO();
        request.setSessionId(session.getId());
        request.setLayout(KitchenLayout.L_SHAPE);
        request.setRoomHeightMm(2400);
        request.setWalls(List.of(measuredWall("A", 3000, 0, 1)));

        mockMvc
            .perform(
                put("/api/design-sessions/{sessionId}/measured-layout", session.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isBadRequest());
    }

    private MeasuredWallSegmentDTO measuredWall(String wallCode, int lengthMm, int angleDeg, int sortOrder) {
        MeasuredWallSegmentDTO wall = new MeasuredWallSegmentDTO();
        wall.setWallCode(wallCode);
        wall.setLengthMm(lengthMm);
        wall.setHeightMm(2400);
        wall.setAngleDeg(angleDeg);
        wall.setSortOrder(sortOrder);
        return wall;
    }
}
