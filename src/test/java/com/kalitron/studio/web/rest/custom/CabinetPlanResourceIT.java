package com.kalitron.studio.web.rest.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.CabinetRepository;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.KitchenSpecRepository;
import com.kalitron.studio.repository.MaterialRepository;
import com.kalitron.studio.repository.RoomObstacleRepository;
import com.kalitron.studio.repository.RoomWallRepository;
import com.kalitron.studio.service.dto.CabinetPlanResponseDTO;
import com.kalitron.studio.service.dto.LayoutObstacleDTO;
import com.kalitron.studio.service.dto.LayoutZoneDTO;
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
class CabinetPlanResourceIT {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    @Autowired
    private KitchenSpecRepository kitchenSpecRepository;

    @Autowired
    private CabinetRepository cabinetRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private RoomWallRepository roomWallRepository;

    @Autowired
    private RoomObstacleRepository roomObstacleRepository;

    @Autowired
    private DesignArtifactRepository designArtifactRepository;

    @AfterEach
    void cleanup() {
        cabinetRepository.deleteAll();
        roomObstacleRepository.deleteAll();
        roomWallRepository.deleteAll();
        designArtifactRepository.deleteAll();
        designSessionRepository.deleteAll();
        kitchenSpecRepository.deleteAll();
        materialRepository.deleteAll();
    }

    @Test
    void cabinetPlanEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/design-sessions/1/cabinet-plan")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/design-sessions/1/cabinet-plan")).andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/design-sessions/1/cabinet-plan")).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void generateCabinetPlanFromMeasuredLayoutAndPersistIt() throws Exception {
        DesignSession session = designSessionRepository.save(
            new DesignSession()
                .sessionCode("KD-2026-044")
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.LAYOUT_CONFIRMED)
                .clientName("Cabinet Client")
                .clientEmail("cabinet@example.com")
                .selectedStyle("Blanco brillante")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
        );
        saveMeasuredLayout(session.getId());

        mockMvc
            .perform(post("/api/design-sessions/{sessionId}/cabinet-plan", session.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionCode").value("KD-2026-044"))
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.cabinetCount").value(5))
            .andExpect(jsonPath("$.cabinets[0].wallCode").value("A"))
            .andExpect(jsonPath("$.cabinets[?(@.category == 'SINK')]").isArray());

        assertThat(kitchenSpecRepository.findBySessionId(session.getId())).isPresent();
        assertThat(cabinetRepository.findAll()).hasSize(5);
        assertThat(
            designArtifactRepository.findFirstBySessionIdAndFileNameOrderByCreatedAtDesc(session.getId(), "cabinet-plan.json")
        ).isPresent();

        mockMvc
            .perform(get("/api/design-sessions/{sessionId}/cabinet-plan", session.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cabinetCount").value(5));
    }

    @Test
    @Transactional
    @WithMockUser
    void saveEditedCabinetPlanValidatesAndPersistsIt() throws Exception {
        DesignSession session = designSessionRepository.save(
            new DesignSession()
                .sessionCode("KD-2026-042")
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.LAYOUT_CONFIRMED)
                .clientName("Cabinet Editor")
                .clientEmail("editor@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
        );
        saveMeasuredLayout(session.getId());
        CabinetPlanResponseDTO generatedPlan = generateCabinetPlan(session.getId());
        generatedPlan.getCabinets().getFirst().setLabel("Base ajustada");
        generatedPlan.getCabinets().getFirst().setWidthMm(500);
        generatedPlan.getCabinets().getFirst().setNotes("Ajuste manual aprobado.");

        mockMvc
            .perform(
                put("/api/design-sessions/{sessionId}/cabinet-plan", session.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(generatedPlan))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.cabinetCount").value(5))
            .andExpect(jsonPath("$.totalOccupiedLengthMm").value(3500))
            .andExpect(jsonPath("$.cabinets[0].label").value("Base ajustada"));

        assertThat(
            cabinetRepository.findBySpecIdOrderByPositionSeqAsc(
                kitchenSpecRepository.findBySessionId(session.getId()).orElseThrow().getId()
            )
        ).anySatisfy(cabinet -> {
            assertThat(cabinet.getLabel()).isEqualTo("Base ajustada");
            assertThat(cabinet.getWidthMm()).isEqualTo(500);
            assertThat(cabinet.getNotes()).isEqualTo("Ajuste manual aprobado.");
        });
    }

    @Test
    @Transactional
    @WithMockUser
    void saveEditedCabinetPlanReturnsValidationErrorsWithoutPersistingInvalidPlan() throws Exception {
        DesignSession session = designSessionRepository.save(
            new DesignSession()
                .sessionCode("KD-2026-042-BAD")
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.LAYOUT_CONFIRMED)
                .clientName("Cabinet Editor")
                .clientEmail("editor@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
        );
        saveMeasuredLayout(session.getId());
        CabinetPlanResponseDTO generatedPlan = generateCabinetPlan(session.getId());
        generatedPlan.getCabinets().getFirst().setWidthMm(50);

        mockMvc
            .perform(
                put("/api/design-sessions/{sessionId}/cabinet-plan", session.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(generatedPlan))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.validationMessages[?(@.code == 'CABINET_WIDTH_OUT_OF_RANGE')]").isArray());

        assertThat(
            cabinetRepository.findBySpecIdOrderByPositionSeqAsc(
                kitchenSpecRepository.findBySessionId(session.getId()).orElseThrow().getId()
            )
        )
            .hasSize(5)
            .noneSatisfy(cabinet -> assertThat(cabinet.getWidthMm()).isEqualTo(50));
    }

    @Test
    @Transactional
    @WithMockUser
    void generateCabinetPlanReturnsNotFoundWithoutMeasuredLayout() throws Exception {
        DesignSession session = designSessionRepository.save(
            new DesignSession()
                .sessionCode("KD-2026-044-NO-LYT")
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.LAYOUT_CONFIRMED)
                .clientName("Cabinet Client")
                .clientEmail("cabinet@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
        );

        mockMvc.perform(post("/api/design-sessions/{sessionId}/cabinet-plan", session.getId())).andExpect(status().isNotFound());
    }

    private CabinetPlanResponseDTO generateCabinetPlan(Long sessionId) throws Exception {
        return om.readValue(
            mockMvc
                .perform(post("/api/design-sessions/{sessionId}/cabinet-plan", sessionId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray(),
            CabinetPlanResponseDTO.class
        );
    }

    private void saveMeasuredLayout(Long sessionId) throws Exception {
        MeasuredLayoutRequestDTO request = new MeasuredLayoutRequestDTO();
        request.setSessionId(sessionId);
        request.setLayout(KitchenLayout.LINEAR);
        request.setRoomHeightMm(2400);
        request.setDefaultBaseDepthMm(600);
        request.setDefaultUpperDepthMm(350);
        request.setWalls(List.of(measuredWall("A", 3600)));
        request.setZones(List.of(layoutZone("SINK-1", "SINK", "A", 600, 800)));
        request.setObstacles(List.of(layoutObstacle(RoomObstacleType.WINDOW, "Ventana", "A", 500, 1100, 900, 700)));

        mockMvc
            .perform(
                put("/api/design-sessions/{sessionId}/measured-layout", sessionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isOk());
    }

    private MeasuredWallSegmentDTO measuredWall(String wallCode, int lengthMm) {
        MeasuredWallSegmentDTO wall = new MeasuredWallSegmentDTO();
        wall.setWallCode(wallCode);
        wall.setLengthMm(lengthMm);
        wall.setHeightMm(2400);
        wall.setAngleDeg(0);
        wall.setSortOrder(1);
        return wall;
    }

    private LayoutZoneDTO layoutZone(String zoneCode, String zoneType, String wallCode, int xMm, int widthMm) {
        LayoutZoneDTO zone = new LayoutZoneDTO();
        zone.setZoneCode(zoneCode);
        zone.setZoneType(zoneType);
        zone.setWallCode(wallCode);
        zone.setxMm(xMm);
        zone.setWidthMm(widthMm);
        return zone;
    }

    private LayoutObstacleDTO layoutObstacle(
        RoomObstacleType obstacleType,
        String label,
        String wallCode,
        int xMm,
        int zMm,
        int widthMm,
        int heightMm
    ) {
        LayoutObstacleDTO obstacle = new LayoutObstacleDTO();
        obstacle.setObstacleType(obstacleType);
        obstacle.setLabel(label);
        obstacle.setWallCode(wallCode);
        obstacle.setxMm(xMm);
        obstacle.setzMm(zMm);
        obstacle.setWidthMm(widthMm);
        obstacle.setHeightMm(heightMm);
        return obstacle;
    }
}
