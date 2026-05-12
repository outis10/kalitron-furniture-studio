package com.kalitron.studio.web.rest.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.ChatMessage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.MessageRole;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.ChatMessageRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import java.time.Instant;
import java.util.Map;
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
class ChatResourceIT {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @AfterEach
    void cleanup() {
        chatMessageRepository.deleteAll();
        designSessionRepository.deleteAll();
    }

    @Test
    void chatEndpointsRequireAuthentication() throws Exception {
        mockMvc
            .perform(post("/api/chat/sessions").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void startSessionCreatesSessionAndGreeting() throws Exception {
        mockMvc
            .perform(
                post("/api/chat/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("clientName", "Ana Lopez", "clientEmail", "ana@example.com")))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.sessionId").isNumber())
            .andExpect(jsonPath("$.sessionCode").value(startsWith("KD-")))
            .andExpect(jsonPath("$.clientName").value("Ana Lopez"))
            .andExpect(jsonPath("$.clientEmail").value("ana@example.com"))
            .andExpect(jsonPath("$.status").value("CHATTING"))
            .andExpect(jsonPath("$.messages", hasSize(1)))
            .andExpect(jsonPath("$.messages[0].role").value("ASSISTANT"));

        assertThat(designSessionRepository.findAll()).hasSize(1);
        assertThat(chatMessageRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    @WithMockUser
    void resumeSessionReturnsStoredMessages() throws Exception {
        DesignSession session = saveSession("KD-2026-777");
        chatMessageRepository.save(new ChatMessage().session(session).role(MessageRole.ASSISTANT).content("Hola").createdAt(Instant.now()));

        mockMvc
            .perform(get("/api/chat/sessions/{sessionCode}", "KD-2026-777"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionCode").value("KD-2026-777"))
            .andExpect(jsonPath("$.messages", hasSize(1)))
            .andExpect(jsonPath("$.messages[0].content").value("Hola"));
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessagePersistsUserAndAssistantMessages() throws Exception {
        DesignSession session = saveSession("KD-2026-778");

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "message", "Mi proyecto es cocina moderna")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.sessionCode").value("KD-2026-778"))
            .andExpect(jsonPath("$.reply").isString())
            .andExpect(jsonPath("$.specsReady").value(false));

        assertThat(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId()))
            .extracting(ChatMessage::getRole)
            .containsExactly(MessageRole.USER, MessageRole.ASSISTANT);
    }

    private DesignSession saveSession(String sessionCode) {
        Instant now = Instant.now();
        return designSessionRepository.save(
            new DesignSession()
                .sessionCode(sessionCode)
                .projectType(ProjectType.KITCHEN)
                .status(SessionStatus.CHATTING)
                .clientName("Ana Lopez")
                .clientEmail("ana@example.com")
                .createdAt(now)
                .updatedAt(now)
        );
    }
}
