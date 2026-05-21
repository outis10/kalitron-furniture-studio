package com.kalitron.studio.web.rest.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.kalitron.studio.service.FastApiGateway;
import com.kalitron.studio.service.FastApiGateway.GatewayChatRequest;
import com.kalitron.studio.service.FastApiGateway.GatewayGenerateRequest;
import com.kalitron.studio.service.FastApiGateway.GatewaySketchAnalysisRequest;
import com.kalitron.studio.service.FastApiGatewayException;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import com.kalitron.studio.service.dto.SketchExtractionResponseDTO;
import com.kalitron.studio.service.dto.SketchFieldDTO;
import com.kalitron.studio.service.dto.VisualConceptResponseDTO;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @Autowired
    private DesignImageRepository designImageRepository;

    @MockitoBean
    private FastApiGateway fastApiGateway;

    @AfterEach
    void cleanup() {
        designImageRepository.deleteAll();
        chatMessageRepository.deleteAll();
        designSessionRepository.deleteAll();
    }

    @Test
    void chatEndpointsRequireAuthentication() throws Exception {
        mockMvc
            .perform(post("/api/chat/sessions").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());

        mockMvc
            .perform(post("/api/chat/message").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());

        mockMvc
            .perform(post("/api/chat/sketch-analysis").contentType(MediaType.APPLICATION_JSON).content("{}"))
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
                    .content(
                        om.writeValueAsBytes(
                            Map.of("clientName", "Ana Lopez", "clientEmail", "ana@example.com", "selectedStyle", "Moderno Blanco")
                        )
                    )
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.sessionId").isNumber())
            .andExpect(jsonPath("$.sessionCode").value(startsWith("KD-")))
            .andExpect(jsonPath("$.clientName").value("Ana Lopez"))
            .andExpect(jsonPath("$.clientEmail").value("ana@example.com"))
            .andExpect(jsonPath("$.selectedStyle").value("Moderno Blanco"))
            .andExpect(jsonPath("$.status").value("CHATTING"))
            .andExpect(jsonPath("$.messages", hasSize(1)))
            .andExpect(jsonPath("$.messages[0].role").value("ASSISTANT"));

        assertThat(designSessionRepository.findAll()).hasSize(1);
        assertThat(designSessionRepository.findAll().getFirst().getSelectedStyle()).isEqualTo("Moderno Blanco");
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
    void listSessionsReturnsRecentSessionsWithConceptCounts() throws Exception {
        DesignSession olderSession = saveSession("KD-2026-760", SessionStatus.CHATTING);
        olderSession.setUpdatedAt(Instant.parse("2026-05-01T10:00:00Z"));
        designSessionRepository.save(olderSession);

        DesignSession recentSession = saveSession("KD-2026-761", SessionStatus.VISUAL_GENERATED);
        recentSession.setUpdatedAt(Instant.parse("2026-05-02T10:00:00Z"));
        designSessionRepository.save(recentSession);
        designImageRepository.save(
            new DesignImage()
                .session(recentSession)
                .imageType(ImageType.AI_RENDER)
                .fileName("concept.jpg")
                .filePath("http://localhost:8000/outputs/concept.jpg")
                .mimeType("image/jpeg")
                .isActive(true)
                .uploadedAt(Instant.parse("2026-05-02T10:01:00Z"))
                .description("Based on your photo")
        );

        mockMvc
            .perform(get("/api/chat/sessions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].sessionCode").value("KD-2026-761"))
            .andExpect(jsonPath("$[0].status").value("VISUAL_GENERATED"))
            .andExpect(jsonPath("$[0].generatedConceptCount").value(1))
            .andExpect(jsonPath("$[1].sessionCode").value("KD-2026-760"))
            .andExpect(jsonPath("$[1].generatedConceptCount").value(0));
    }

    @Test
    @Transactional
    @WithMockUser
    void resumeSessionIncludesGeneratedConceptsInHistory() throws Exception {
        DesignSession session = saveSession("KD-2026-762", SessionStatus.VISUAL_GENERATED);
        chatMessageRepository.save(
            new ChatMessage()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content("Resumen listo")
                .createdAt(Instant.parse("2026-05-02T10:00:00Z"))
        );
        designImageRepository.save(
            new DesignImage()
                .session(session)
                .imageType(ImageType.AI_RENDER)
                .fileName("concept.jpg")
                .filePath("http://localhost:8000/outputs/concept.jpg")
                .mimeType("image/jpeg")
                .isActive(true)
                .uploadedAt(Instant.parse("2026-05-02T10:01:00Z"))
                .description("Based on your photo")
        );

        mockMvc
            .perform(get("/api/chat/sessions/{sessionCode}", "KD-2026-762"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.messages", hasSize(2)))
            .andExpect(jsonPath("$.messages[0].content").value("Resumen listo"))
            .andExpect(jsonPath("$.messages[1].content").value("Concepto visual generado."))
            .andExpect(jsonPath("$.messages[1].imagePreviewUrl").value("http://localhost:8000/outputs/concept.jpg"))
            .andExpect(jsonPath("$.messages[1].imageFileName").value("concept.jpg"))
            .andExpect(jsonPath("$.messages[1].imageBadge").value("Based on your photo"));
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessagePersistsUserAndAssistantMessages() throws Exception {
        DesignSession session = saveSession("KD-2026-778");
        when(fastApiGateway.sendMessage(any())).thenReturn(gatewayResponse(session, "Respuesta desde gateway", false));

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of(
                                "sessionId",
                                session.getId(),
                                "message",
                                "Mi proyecto es cocina moderna",
                                "selectedStyle",
                                "Moderno Gris"
                            )
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.sessionCode").value("KD-2026-778"))
            .andExpect(jsonPath("$.reply").value("Respuesta desde gateway"))
            .andExpect(jsonPath("$.specsReady").value(false));

        assertThat(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId()))
            .extracting(ChatMessage::getRole)
            .containsExactly(MessageRole.USER, MessageRole.ASSISTANT);
        assertThat(designSessionRepository.findById(session.getId()).orElseThrow().getSelectedStyle()).isEqualTo("Moderno Gris");
        ArgumentCaptor<GatewayChatRequest> gatewayRequest = ArgumentCaptor.forClass(GatewayChatRequest.class);
        verify(fastApiGateway).sendMessage(gatewayRequest.capture());
        assertThat(gatewayRequest.getValue().sessionId()).isEqualTo("KD-2026-778");
        assertThat(gatewayRequest.getValue().message()).contains("Estilo visual seleccionado: Moderno Gris");
        assertThat(gatewayRequest.getValue().imageBase64()).isNull();
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessageWithReferenceImagePersistsDesignImage() throws Exception {
        DesignSession session = saveSession("KD-2026-779");
        when(fastApiGateway.sendMessage(any())).thenReturn(gatewayResponse(session, "Veo la foto de referencia.", false));

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of(
                                "sessionId",
                                session.getId(),
                                "message",
                                "Esta es mi cocina actual",
                                "imageBase64",
                                "ZmFrZS1pbWFnZQ==",
                                "imageFileName",
                                "cocina.webp",
                                "imageMimeType",
                                "image/webp",
                                "imageSizeBytes",
                                10
                            )
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reply").value("Veo la foto de referencia."));

        assertThat(designImageRepository.findAll()).extracting(DesignImage::getImageType).containsExactly(ImageType.REFERENCE);
        DesignImage image = designImageRepository.findAll().getFirst();
        assertThat(image.getSession().getId()).isEqualTo(session.getId());
        assertThat(image.getFileName()).isEqualTo("cocina.webp");
        assertThat(image.getMimeType()).isEqualTo("image/webp");
        assertThat(image.getFilePath()).contains("KD-2026-779");
        assertThat(image.getImageDataBase64()).isEqualTo("ZmFrZS1pbWFnZQ==");

        ArgumentCaptor<GatewayChatRequest> gatewayRequest = ArgumentCaptor.forClass(GatewayChatRequest.class);
        verify(fastApiGateway).sendMessage(gatewayRequest.capture());
        assertThat(gatewayRequest.getValue().imageBase64()).isEqualTo("ZmFrZS1pbWFnZQ==");
        assertThat(gatewayRequest.getValue().imageMimeType()).isEqualTo("image/webp");
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessageUpdatesSessionStatusWhenSpecsReady() throws Exception {
        DesignSession session = saveSession("KD-2026-781");
        when(fastApiGateway.sendMessage(any())).thenReturn(gatewayResponse(session, "Especificaciones listas.", true));

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "message", "Confirmo especificaciones")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specsReady").value(true));

        assertThat(designSessionRepository.findById(session.getId()).orElseThrow().getStatus()).isEqualTo(SessionStatus.SPECS_READY);
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessageDoesNotRegressSpecsReadyStatus() throws Exception {
        DesignSession session = saveSession("KD-2026-788", SessionStatus.SPECS_READY);
        when(fastApiGateway.sendMessage(any())).thenReturn(gatewayResponse(session, "Ajusto el concepto con esa nota.", false));

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "message", "Agrega madera natural")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specsReady").value(false));

        assertThat(designSessionRepository.findById(session.getId()).orElseThrow().getStatus()).isEqualTo(SessionStatus.SPECS_READY);
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessageAcceptsSpecsReadyCompletionReply() throws Exception {
        DesignSession session = saveSession("KD-2026-783");
        when(fastApiGateway.sendMessage(any())).thenReturn(
            gatewayResponse(session, "Perfecto, ya tengo la información base para preparar las especificaciones de diseño.", true)
        );

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "message", "SI")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reply").value(startsWith("Perfecto")))
            .andExpect(jsonPath("$.specsReady").value(true));
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessageHandlesGatewayUnavailableWithoutLosingUserMessage() throws Exception {
        DesignSession session = saveSession("KD-2026-782");
        when(fastApiGateway.sendMessage(any())).thenThrow(new FastApiGatewayException("down"));

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "message", "Necesito una cocina en L")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reply").value(startsWith("El diseñador IA no está disponible")))
            .andExpect(jsonPath("$.specsReady").value(false));

        assertThat(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId()))
            .extracting(ChatMessage::getRole)
            .containsExactly(MessageRole.USER, MessageRole.ASSISTANT);
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessageRejectsInvalidReferenceImageType() throws Exception {
        DesignSession session = saveSession("KD-2026-780");

        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of(
                                "sessionId",
                                session.getId(),
                                "message",
                                "Foto",
                                "imageBase64",
                                "ZmFrZS1pbWFnZQ==",
                                "imageFileName",
                                "cocina.gif",
                                "imageMimeType",
                                "image/gif",
                                "imageSizeBytes",
                                10
                            )
                        )
                    )
            )
            .andExpect(status().isBadRequest());

        assertThat(designImageRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @WithMockUser
    void sendMessageReturnsNotFoundForMissingSession() throws Exception {
        mockMvc
            .perform(
                post("/api/chat/message")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", 999999L, "message", "Hola")))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser
    void generateVisualConceptPersistsAiRenderImage() throws Exception {
        DesignSession session = saveSession("KD-2026-784", SessionStatus.SPECS_READY);
        chatMessageRepository.save(
            new ChatMessage()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content("Perfecto, aqui tienes el resumen de tu proyecto de cocina.")
                .createdAt(Instant.now())
        );
        when(fastApiGateway.generateVisualConcept(any())).thenReturn(
            visualConceptResponse(session, "http://localhost:8000/outputs/concepts/KD-2026-784_txt2img.jpg", "txt2img")
        );

        mockMvc
            .perform(
                post("/api/chat/visual-concept")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of("sessionId", session.getId(), "style", "minimalista", "layout", "lineal", "finish", "negro opaco")
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.imageUrl").value("http://localhost:8000/outputs/concepts/KD-2026-784_txt2img.jpg"))
            .andExpect(jsonPath("$.pipeline").value("txt2img"))
            .andExpect(jsonPath("$.badge").value("Generated from description"));

        assertThat(designSessionRepository.findById(session.getId()).orElseThrow().getStatus()).isEqualTo(SessionStatus.VISUAL_GENERATED);
        assertThat(designImageRepository.findAll()).extracting(DesignImage::getImageType).containsExactly(ImageType.AI_RENDER);
        assertThat(designImageRepository.findAll().getFirst().getFilePath()).contains("KD-2026-784_txt2img.jpg");

        ArgumentCaptor<GatewayGenerateRequest> gatewayRequest = ArgumentCaptor.forClass(GatewayGenerateRequest.class);
        verify(fastApiGateway).generateVisualConcept(gatewayRequest.capture());
        assertThat(gatewayRequest.getValue().sessionId()).isEqualTo("KD-2026-784");
        assertThat(gatewayRequest.getValue().style()).isEqualTo("minimalista");
        assertThat(gatewayRequest.getValue().layout()).isEqualTo("lineal");
        assertThat(gatewayRequest.getValue().finish()).isEqualTo("negro opaco");
        assertThat(gatewayRequest.getValue().projectType()).isEqualTo("KITCHEN");
        assertThat(gatewayRequest.getValue().designBrief()).contains("resumen de tu proyecto de cocina");
        assertThat(gatewayRequest.getValue().clientImageBase64()).isNull();
    }

    @Test
    @Transactional
    @WithMockUser
    void generateVisualConceptUsesLatestReferenceImageForImg2Img() throws Exception {
        DesignSession session = saveSession("KD-2026-790", SessionStatus.SPECS_READY);
        designImageRepository.save(
            new DesignImage()
                .session(session)
                .imageType(ImageType.REFERENCE)
                .fileName("cocina.webp")
                .filePath("reference-images/KD-2026-790/cocina.webp")
                .imageDataBase64("cmVmZXJlbmNlLWltYWdl")
                .mimeType("image/webp")
                .fileSizeKb(1L)
                .isActive(true)
                .uploadedAt(Instant.now())
                .description("Reference photo uploaded from design chat")
        );
        when(fastApiGateway.generateVisualConcept(any())).thenReturn(
            visualConceptResponse(session, "http://localhost:8000/outputs/concepts/KD-2026-790_img2img.jpg", "img2img")
        );

        mockMvc
            .perform(
                post("/api/chat/visual-concept")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId())))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pipeline").value("img2img"))
            .andExpect(jsonPath("$.badge").value("Based on your photo"));

        ArgumentCaptor<GatewayGenerateRequest> gatewayRequest = ArgumentCaptor.forClass(GatewayGenerateRequest.class);
        verify(fastApiGateway).generateVisualConcept(gatewayRequest.capture());
        assertThat(gatewayRequest.getValue().clientImageBase64()).isEqualTo("cmVmZXJlbmNlLWltYWdl");
        assertThat(designImageRepository.findAll())
            .extracting(DesignImage::getImageType)
            .contains(ImageType.REFERENCE, ImageType.AI_RENDER);
    }

    @Test
    @Transactional
    @WithMockUser
    void generateVisualConceptAppendsVisualInstructionsToDesignBrief() throws Exception {
        DesignSession session = saveSession("KD-2026-791", SessionStatus.VISUAL_GENERATED);
        chatMessageRepository.save(
            new ChatMessage()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content("Perfecto, aqui tienes el resumen de tu proyecto de cocina: muebles de madera.")
                .createdAt(Instant.now())
        );
        when(fastApiGateway.generateVisualConcept(any())).thenReturn(
            visualConceptResponse(session, "http://localhost:8000/outputs/concepts/KD-2026-791_img2img.jpg", "img2img")
        );

        mockMvc
            .perform(
                post("/api/chat/visual-concept")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of(
                                "sessionId",
                                session.getId(),
                                "finish",
                                "negro opaco",
                                "visualInstructions",
                                "La encimera color blanco y los muebles color negro opaco"
                            )
                        )
                    )
            )
            .andExpect(status().isOk());

        ArgumentCaptor<GatewayGenerateRequest> gatewayRequest = ArgumentCaptor.forClass(GatewayGenerateRequest.class);
        verify(fastApiGateway).generateVisualConcept(gatewayRequest.capture());
        assertThat(gatewayRequest.getValue().finish()).isEqualTo("negro opaco");
        assertThat(gatewayRequest.getValue().designBrief()).contains("resumen de tu proyecto de cocina");
        assertThat(gatewayRequest.getValue().designBrief()).contains("Instrucciones visuales para esta regeneracion");
        assertThat(gatewayRequest.getValue().designBrief()).contains("encimera color blanco");
        assertThat(gatewayRequest.getValue().designBrief()).contains("muebles color negro opaco");
    }

    @Test
    @Transactional
    @WithMockUser
    void generateVisualConceptPassesClosetProjectAndSummaryToGateway() throws Exception {
        DesignSession session = saveSession("KD-2026-789", SessionStatus.SPECS_READY);
        session.setProjectType(ProjectType.CLOSET);
        designSessionRepository.save(session);
        chatMessageRepository.save(
            new ChatMessage()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content("Perfecto, aqui tienes el resumen de tu proyecto de armario: alto brillo blanco.")
                .createdAt(Instant.now())
        );
        when(fastApiGateway.generateVisualConcept(any())).thenReturn(
            visualConceptResponse(session, "http://localhost:8000/outputs/concepts/KD-2026-789_txt2img.jpg", "txt2img")
        );

        mockMvc
            .perform(
                post("/api/chat/visual-concept")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId())))
            )
            .andExpect(status().isOk());

        ArgumentCaptor<GatewayGenerateRequest> gatewayRequest = ArgumentCaptor.forClass(GatewayGenerateRequest.class);
        verify(fastApiGateway).generateVisualConcept(gatewayRequest.capture());
        assertThat(gatewayRequest.getValue().projectType()).isEqualTo("CLOSET");
        assertThat(gatewayRequest.getValue().designBrief()).contains("proyecto de armario");
        assertThat(gatewayRequest.getValue().finish()).isNull();
    }

    @Test
    @Transactional
    @WithMockUser
    void generateVisualConceptKeepsLayoutConfirmedStatus() throws Exception {
        DesignSession session = saveSession("KD-2026-787", SessionStatus.LAYOUT_CONFIRMED);
        when(fastApiGateway.generateVisualConcept(any())).thenReturn(
            visualConceptResponse(session, "http://localhost:8000/outputs/concepts/KD-2026-787_txt2img.jpg", "txt2img")
        );

        mockMvc
            .perform(
                post("/api/chat/visual-concept")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "style", "minimalista")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.imageUrl").value("http://localhost:8000/outputs/concepts/KD-2026-787_txt2img.jpg"));

        assertThat(designSessionRepository.findById(session.getId()).orElseThrow().getStatus()).isEqualTo(SessionStatus.LAYOUT_CONFIRMED);
        assertThat(designImageRepository.findAll()).extracting(DesignImage::getImageType).containsExactly(ImageType.AI_RENDER);
    }

    @Test
    @Transactional
    @WithMockUser
    void generateVisualConceptRejectsSessionBeforeSpecsReady() throws Exception {
        DesignSession session = saveSession("KD-2026-785", SessionStatus.CHATTING);

        mockMvc
            .perform(
                post("/api/chat/visual-concept")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "style", "moderno")))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser
    void generateVisualConceptHandlesGatewayUnavailable() throws Exception {
        DesignSession session = saveSession("KD-2026-786", SessionStatus.SPECS_READY);
        when(fastApiGateway.generateVisualConcept(any())).thenThrow(new FastApiGatewayException("down"));

        mockMvc
            .perform(
                post("/api/chat/visual-concept")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("sessionId", session.getId(), "style", "moderno")))
            )
            .andExpect(status().isServiceUnavailable());
    }

    @Test
    @Transactional
    @WithMockUser
    void analyzeSketchPersistsSketchAndReturnsExtraction() throws Exception {
        DesignSession session = saveSession("KD-2026-792", SessionStatus.CHATTING);
        when(fastApiGateway.analyzeSketch(any())).thenReturn(sketchExtractionResponse(session));

        mockMvc
            .perform(
                post("/api/chat/sketch-analysis")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of(
                                "sessionId",
                                session.getId(),
                                "imageBase64",
                                "Ym9jZXRv",
                                "imageFileName",
                                "boceto.png",
                                "imageMimeType",
                                "image/png",
                                "imageSizeBytes",
                                6,
                                "projectTypeHint",
                                "KITCHEN",
                                "unitHint",
                                "MM",
                                "userPrompt",
                                "Cocina lineal con tarja al centro"
                            )
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schemaVersion").value("1.0"))
            .andExpect(jsonPath("$.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.sessionCode").value("KD-2026-792"))
            .andExpect(jsonPath("$.projectType.value").value("KITCHEN"))
            .andExpect(jsonPath("$.layout.value").value("LINEAR"))
            .andExpect(jsonPath("$.warnings[0]").value("Confirma medidas antes de guardar."));

        assertThat(designImageRepository.findAll()).extracting(DesignImage::getImageType).containsExactly(ImageType.SKETCH);
        DesignImage image = designImageRepository.findAll().getFirst();
        assertThat(image.getFileName()).isEqualTo("boceto.png");
        assertThat(image.getMimeType()).isEqualTo("image/png");
        assertThat(image.getFilePath()).contains("sketch-images/KD-2026-792");
        assertThat(image.getImageDataBase64()).isEqualTo("Ym9jZXRv");

        ArgumentCaptor<GatewaySketchAnalysisRequest> gatewayRequest = ArgumentCaptor.forClass(GatewaySketchAnalysisRequest.class);
        verify(fastApiGateway).analyzeSketch(gatewayRequest.capture());
        assertThat(gatewayRequest.getValue().sessionId()).isEqualTo("KD-2026-792");
        assertThat(gatewayRequest.getValue().imageBase64()).isEqualTo("Ym9jZXRv");
        assertThat(gatewayRequest.getValue().imageMimeType()).isEqualTo("image/png");
        assertThat(gatewayRequest.getValue().imageFileName()).isEqualTo("boceto.png");
        assertThat(gatewayRequest.getValue().projectTypeHint()).isEqualTo("KITCHEN");
        assertThat(gatewayRequest.getValue().unitHint()).isEqualTo("MM");
        assertThat(gatewayRequest.getValue().userPrompt()).isEqualTo("Cocina lineal con tarja al centro");
    }

    @Test
    @Transactional
    @WithMockUser
    void analyzeSketchRejectsInvalidImageType() throws Exception {
        DesignSession session = saveSession("KD-2026-793", SessionStatus.CHATTING);

        mockMvc
            .perform(
                post("/api/chat/sketch-analysis")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of(
                                "sessionId",
                                session.getId(),
                                "imageBase64",
                                "Ym9jZXRv",
                                "imageFileName",
                                "boceto.gif",
                                "imageMimeType",
                                "image/gif",
                                "imageSizeBytes",
                                6
                            )
                        )
                    )
            )
            .andExpect(status().isBadRequest());

        assertThat(designImageRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @WithMockUser
    void analyzeSketchHandlesGatewayUnavailable() throws Exception {
        DesignSession session = saveSession("KD-2026-794", SessionStatus.CHATTING);
        when(fastApiGateway.analyzeSketch(any())).thenThrow(new FastApiGatewayException("down"));

        mockMvc
            .perform(
                post("/api/chat/sketch-analysis")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        om.writeValueAsBytes(
                            Map.of(
                                "sessionId",
                                session.getId(),
                                "imageBase64",
                                "Ym9jZXRv",
                                "imageFileName",
                                "boceto.jpg",
                                "imageMimeType",
                                "image/jpeg",
                                "imageSizeBytes",
                                6
                            )
                        )
                    )
            )
            .andExpect(status().isServiceUnavailable());
    }

    private DesignSession saveSession(String sessionCode) {
        return saveSession(sessionCode, SessionStatus.CHATTING);
    }

    private DesignSession saveSession(String sessionCode, SessionStatus status) {
        Instant now = Instant.now();
        return designSessionRepository.save(
            new DesignSession()
                .sessionCode(sessionCode)
                .projectType(ProjectType.KITCHEN)
                .status(status)
                .clientName("Ana Lopez")
                .clientEmail("ana@example.com")
                .createdAt(now)
                .updatedAt(now)
        );
    }

    private ChatResponseDTO gatewayResponse(DesignSession session, String reply, boolean specsReady) {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(session.getId());
        response.setSessionCode(session.getSessionCode());
        response.setReply(reply);
        response.setSpecsReady(specsReady);
        response.setSpecsSummary(null);
        return response;
    }

    private VisualConceptResponseDTO visualConceptResponse(DesignSession session, String imageUrl, String pipeline) {
        VisualConceptResponseDTO response = new VisualConceptResponseDTO();
        response.setSessionId(session.getId());
        response.setSessionCode(session.getSessionCode());
        response.setImageUrl(imageUrl);
        response.setPromptUsed("kitchen interior design");
        response.setPipeline(pipeline);
        response.setBadge("img2img".equals(pipeline) ? "Based on your photo" : "Generated from description");
        return response;
    }

    private SketchExtractionResponseDTO sketchExtractionResponse(DesignSession session) {
        SketchExtractionResponseDTO response = new SketchExtractionResponseDTO();
        response.setSchemaVersion("1.0");
        response.setRequestId("sketch-test-001");
        response.setSessionId(session.getId());
        response.setSessionCode(session.getSessionCode());
        response.setProjectType(new SketchFieldDTO<>("KITCHEN", "HIGH", "cocina"));
        response.setLayout(new SketchFieldDTO<>("LINEAR", "MEDIUM", "vista frontal"));
        response.setUnit(new SketchFieldDTO<>("MM", "MEDIUM", "600"));
        response.setWarnings(List.of("Confirma medidas antes de guardar."));
        return response;
    }
}
