package com.kalitron.studio.web.rest.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.ChatMessage;
import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.ImageType;
import com.kalitron.studio.domain.enumeration.MessageRole;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.ChatMessageRepository;
import com.kalitron.studio.repository.DesignImageRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
class ProposalResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private DesignImageRepository designImageRepository;

    @AfterEach
    void cleanup() {
        designImageRepository.deleteAll();
        chatMessageRepository.deleteAll();
        designSessionRepository.deleteAll();
    }

    @Test
    @Transactional
    void getProposalIsPublicAndReturnsProposalSafeData() throws Exception {
        DesignSession session = designSessionRepository.save(
            new DesignSession()
                .sessionCode("KD-2026-901")
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.VISUAL_GENERATED)
                .clientName("Ana Lopez")
                .clientEmail("ana@example.com")
                .selectedStyle("Minimalista Negro")
                .createdAt(Instant.parse("2026-05-20T10:00:00Z"))
                .updatedAt(Instant.parse("2026-05-20T10:10:00Z"))
        );
        chatMessageRepository.save(
            new ChatMessage()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content("Resumen de diseño:\n• Tipo de proyecto: Cocina\n• Estilo: Minimalista")
                .createdAt(Instant.parse("2026-05-20T10:05:00Z"))
        );
        designImageRepository.save(
            new DesignImage()
                .session(session)
                .imageType(ImageType.AI_RENDER)
                .fileName("concept.jpg")
                .filePath("http://localhost:8000/outputs/concept.jpg")
                .mimeType("image/jpeg")
                .isActive(true)
                .uploadedAt(Instant.parse("2026-05-20T10:06:00Z"))
                .description("Based on your photo")
        );

        mockMvc
            .perform(get("/api/public/proposals/{sessionCode}", "KD-2026-901"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionCode").value("KD-2026-901"))
            .andExpect(jsonPath("$.clientName").value("Ana Lopez"))
            .andExpect(jsonPath("$.projectType").value("KITCHEN"))
            .andExpect(jsonPath("$.selectedStyle").value("Minimalista Negro"))
            .andExpect(jsonPath("$.renderImageUrl").value("http://localhost:8000/outputs/concept.jpg"))
            .andExpect(jsonPath("$.renderBadge").value("Based on your photo"))
            .andExpect(jsonPath("$.specsSummary").value("Resumen de diseño:\n• Tipo de proyecto: Cocina\n• Estilo: Minimalista"))
            .andExpect(jsonPath("$.cabinetCount").value(0))
            .andExpect(jsonPath("$.clientEmail").doesNotExist());
    }

    @Test
    void getProposalReturnsNotFoundForUnknownSession() throws Exception {
        mockMvc.perform(get("/api/public/proposals/{sessionCode}", "KD-2026-404")).andExpect(status().isNotFound());
    }
}
