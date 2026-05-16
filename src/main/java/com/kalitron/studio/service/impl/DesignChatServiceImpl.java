package com.kalitron.studio.service.impl;

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
import com.kalitron.studio.service.DesignChatService;
import com.kalitron.studio.service.FastApiGateway;
import com.kalitron.studio.service.FastApiGateway.GatewayChatRequest;
import com.kalitron.studio.service.FastApiGateway.GatewayGenerateRequest;
import com.kalitron.studio.service.FastApiGatewayException;
import com.kalitron.studio.service.dto.ChatMessageViewDTO;
import com.kalitron.studio.service.dto.ChatRequestDTO;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import com.kalitron.studio.service.dto.ChatSessionDTO;
import com.kalitron.studio.service.dto.ChatSessionStartRequestDTO;
import com.kalitron.studio.service.dto.VisualConceptRequestDTO;
import com.kalitron.studio.service.dto.VisualConceptResponseDTO;
import java.time.Instant;
import java.time.Year;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class DesignChatServiceImpl implements DesignChatService {

    private static final Set<String> ALLOWED_REFERENCE_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private static final long MAX_REFERENCE_IMAGE_BYTES = 5L * 1024L * 1024L;

    private final DesignSessionRepository designSessionRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final DesignImageRepository designImageRepository;

    private final FastApiGateway fastApiGateway;

    public DesignChatServiceImpl(
        DesignSessionRepository designSessionRepository,
        ChatMessageRepository chatMessageRepository,
        DesignImageRepository designImageRepository,
        FastApiGateway fastApiGateway
    ) {
        this.designSessionRepository = designSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.designImageRepository = designImageRepository;
        this.fastApiGateway = fastApiGateway;
    }

    @Override
    public ChatSessionDTO startSession(ChatSessionStartRequestDTO request) {
        Instant now = Instant.now();
        DesignSession session = new DesignSession()
            .sessionCode(nextSessionCode())
            .projectType(ProjectType.KITCHEN)
            .status(SessionStatus.CHATTING)
            .clientName(request.getClientName().trim())
            .clientEmail(request.getClientEmail().trim())
            .selectedStyle(normalizeSelectedStyle(request.getSelectedStyle()))
            .createdAt(now)
            .updatedAt(now);

        DesignSession savedSession = designSessionRepository.save(session);
        saveMessage(
            savedSession,
            MessageRole.ASSISTANT,
            "Hola " + savedSession.getClientName() + ", soy tu diseñador IA de Kalitron. ¿Tu proyecto es cocina, closet o ambos?"
        );

        return toSessionDTO(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatSessionDTO> resumeSession(String sessionCode) {
        return designSessionRepository.findBySessionCode(sessionCode).map(this::toSessionDTO);
    }

    @Override
    public ChatResponseDTO sendMessage(ChatRequestDTO request) {
        DesignSession session = designSessionRepository
            .findById(request.getSessionId())
            .orElseThrow(() -> new IllegalArgumentException("Design session not found"));

        String message = normalizeMessage(request.getMessage());
        boolean hasReferenceImage = hasReferenceImage(request);
        if (message.isEmpty() && !hasReferenceImage) {
            throw new IllegalArgumentException("Message or reference image is required");
        }

        if (!message.isEmpty()) {
            saveMessage(session, MessageRole.USER, message);
            updateProjectTypeFromMessage(session, message);
        } else {
            saveMessage(session, MessageRole.USER, "Imagen de referencia adjunta.");
        }
        if (hasReferenceImage) {
            saveReferenceImage(session, request);
        }
        if (normalizeSelectedStyle(request.getSelectedStyle()) != null) {
            session.setSelectedStyle(normalizeSelectedStyle(request.getSelectedStyle()));
        }
        if (session.getStatus() == SessionStatus.DRAFT) {
            session.setStatus(SessionStatus.CHATTING);
        }
        session.setUpdatedAt(Instant.now());
        DesignSession savedSession = designSessionRepository.save(session);

        ChatResponseDTO response = sendToGateway(savedSession, message, request);
        saveMessage(savedSession, MessageRole.ASSISTANT, response.getReply());
        ProjectType previousProjectType = savedSession.getProjectType();
        updateProjectTypeFromMessage(savedSession, response.getReply());
        if (response.isSpecsReady()) {
            savedSession.setStatus(SessionStatus.SPECS_READY);
            savedSession.setUpdatedAt(Instant.now());
            designSessionRepository.save(savedSession);
        } else if (savedSession.getProjectType() != previousProjectType) {
            savedSession.setUpdatedAt(Instant.now());
            designSessionRepository.save(savedSession);
        }

        return response;
    }

    @Override
    public VisualConceptResponseDTO generateVisualConcept(VisualConceptRequestDTO request) {
        DesignSession session = designSessionRepository
            .findById(request.getSessionId())
            .orElseThrow(() -> new IllegalArgumentException("Design session not found"));

        if (!canGenerateVisualConcept(session.getStatus())) {
            throw new IllegalArgumentException(
                "Design session must be specs ready before generating a visual concept. Current status: " + session.getStatus()
            );
        }

        try {
            VisualConceptResponseDTO response = fastApiGateway.generateVisualConcept(
                new GatewayGenerateRequest(
                    session.getSessionCode(),
                    normalizeImageBase64(request.getClientImageBase64()),
                    normalizeVisualOption(request.getStyle(), session.getSelectedStyle(), "moderno"),
                    normalizeVisualOption(request.getLayout(), null, null),
                    normalizeVisualOption(request.getFinish(), null, null),
                    session.getProjectType().name(),
                    buildVisualDesignBrief(session),
                    session.getId(),
                    session.getSessionCode()
                )
            );

            saveGeneratedImage(session, response);
            if (session.getStatus() == SessionStatus.SPECS_READY) {
                session.setStatus(SessionStatus.VISUAL_GENERATED);
            }
            session.setUpdatedAt(Instant.now());
            designSessionRepository.save(session);
            return response;
        } catch (FastApiGatewayException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI visual concept generation is unavailable", e);
        }
    }

    private String nextSessionCode() {
        String prefix = "KD-" + Year.now().getValue() + "-";
        long sequence = designSessionRepository.countBySessionCodeStartingWith(prefix) + 1;
        String candidate = prefix + String.format("%03d", sequence);

        while (designSessionRepository.existsBySessionCode(candidate)) {
            sequence++;
            candidate = prefix + String.format("%03d", sequence);
        }

        return candidate;
    }

    private ChatMessage saveMessage(DesignSession session, MessageRole role, String content) {
        ChatMessage message = new ChatMessage().session(session).role(role).content(content).createdAt(Instant.now());
        return chatMessageRepository.save(message);
    }

    private boolean canGenerateVisualConcept(SessionStatus status) {
        return status == SessionStatus.SPECS_READY || status == SessionStatus.VISUAL_GENERATED || status == SessionStatus.LAYOUT_CONFIRMED;
    }

    private ChatSessionDTO toSessionDTO(DesignSession session) {
        List<ChatMessageViewDTO> messages = chatMessageRepository
            .findBySessionIdOrderByCreatedAtAsc(session.getId())
            .stream()
            .map(message -> new ChatMessageViewDTO(message.getRole(), message.getContent(), message.getCreatedAt()))
            .toList();

        ChatSessionDTO dto = new ChatSessionDTO();
        dto.setSessionId(session.getId());
        dto.setSessionCode(session.getSessionCode());
        dto.setClientName(session.getClientName());
        dto.setClientEmail(session.getClientEmail());
        dto.setSelectedStyle(session.getSelectedStyle());
        dto.setProjectType(session.getProjectType());
        dto.setStatus(session.getStatus());
        dto.setMessages(messages);
        return dto;
    }

    private void updateProjectTypeFromMessage(DesignSession session, String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("ambos") || normalized.contains("both")) {
            session.setProjectType(ProjectType.BOTH);
        } else if (
            normalized.contains("closet") ||
            normalized.contains("clóset") ||
            normalized.contains("armario") ||
            normalized.contains("ropero") ||
            normalized.contains("guardarropa") ||
            normalized.contains("wardrobe")
        ) {
            session.setProjectType(ProjectType.CLOSET);
        } else if (normalized.contains("cocina") || normalized.contains("kitchen")) {
            session.setProjectType(ProjectType.KITCHEN);
        }
    }

    private String normalizeSelectedStyle(String selectedStyle) {
        if (selectedStyle == null || selectedStyle.isBlank()) {
            return null;
        }
        return selectedStyle.trim();
    }

    private String normalizeMessage(String message) {
        if (message == null) {
            return "";
        }
        return message.trim();
    }

    private boolean hasReferenceImage(ChatRequestDTO request) {
        return request.getImageBase64() != null && !request.getImageBase64().isBlank();
    }

    private void saveReferenceImage(DesignSession session, ChatRequestDTO request) {
        String mimeType = normalizeImageMimeType(request.getImageMimeType());
        if (!ALLOWED_REFERENCE_IMAGE_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("Reference image must be JPG, PNG, or WebP");
        }

        long imageSizeBytes = resolveImageSizeBytes(request);
        if (imageSizeBytes > MAX_REFERENCE_IMAGE_BYTES) {
            throw new IllegalArgumentException("Reference image must be 5MB or smaller");
        }

        String fileName = sanitizeImageFileName(request.getImageFileName());
        DesignImage image = new DesignImage()
            .session(session)
            .imageType(ImageType.REFERENCE)
            .fileName(fileName)
            .filePath("reference-images/" + session.getSessionCode() + "/" + Instant.now().toEpochMilli() + "-" + fileName)
            .mimeType(mimeType)
            .fileSizeKb(Math.max(1L, (long) Math.ceil(imageSizeBytes / 1024.0)))
            .isActive(true)
            .uploadedAt(Instant.now())
            .description("Reference photo uploaded from design chat");

        designImageRepository.save(image);
    }

    private String normalizeImageMimeType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return "";
        }
        return mimeType.trim().toLowerCase(Locale.ROOT);
    }

    private long resolveImageSizeBytes(ChatRequestDTO request) {
        if (request.getImageSizeBytes() != null && request.getImageSizeBytes() > 0) {
            return request.getImageSizeBytes();
        }

        String base64 = request.getImageBase64().trim();
        int commaIndex = base64.indexOf(',');
        if (commaIndex >= 0) {
            base64 = base64.substring(commaIndex + 1);
        }
        return Base64.getDecoder().decode(base64).length;
    }

    private String sanitizeImageFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "reference-photo";
        }
        return fileName.trim().replaceAll("[^A-Za-z0-9._-]", "-");
    }

    private ChatResponseDTO sendToGateway(DesignSession session, String message, ChatRequestDTO request) {
        try {
            return fastApiGateway.sendMessage(
                new GatewayChatRequest(
                    session.getSessionCode(),
                    buildGatewayMessage(session, message),
                    normalizeImageBase64(request.getImageBase64()),
                    session.getId(),
                    session.getSessionCode()
                )
            );
        } catch (FastApiGatewayException e) {
            ChatResponseDTO response = new ChatResponseDTO();
            response.setSessionId(session.getId());
            response.setSessionCode(session.getSessionCode());
            response.setReply(
                "El diseñador IA no está disponible por el momento. Guardé tu mensaje y puedes intentar de nuevo en unos minutos."
            );
            response.setSpecsReady(false);
            response.setSpecsSummary(null);
            return response;
        }
    }

    private String buildGatewayMessage(DesignSession session, String message) {
        String userMessage = message.isBlank() ? "Imagen de referencia adjunta." : message;
        if (session.getSelectedStyle() == null || session.getSelectedStyle().isBlank()) {
            return userMessage;
        }

        return "Estilo visual seleccionado: " + session.getSelectedStyle() + ".\n\nMensaje del cliente: " + userMessage;
    }

    private String buildVisualDesignBrief(DesignSession session) {
        List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
        String latestAssistantSummary = messages
            .stream()
            .filter(message -> message.getRole() == MessageRole.ASSISTANT)
            .map(ChatMessage::getContent)
            .filter(content -> content != null && content.toLowerCase(Locale.ROOT).contains("resumen"))
            .reduce((previous, current) -> current)
            .orElse(null);

        if (latestAssistantSummary != null && !latestAssistantSummary.isBlank()) {
            return latestAssistantSummary.trim();
        }

        return messages
            .stream()
            .filter(message -> message.getContent() != null && !message.getContent().isBlank())
            .map(message -> message.getRole() + ": " + message.getContent().trim())
            .reduce((previous, current) -> previous + "\n" + current)
            .orElse("");
    }

    private String normalizeImageBase64(String imageBase64) {
        if (imageBase64 == null || imageBase64.isBlank()) {
            return null;
        }
        return imageBase64.trim();
    }

    private String normalizeVisualOption(String option, String fallback, String defaultValue) {
        if (option != null && !option.isBlank()) {
            return option.trim();
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback.trim();
        }
        return defaultValue;
    }

    private void saveGeneratedImage(DesignSession session, VisualConceptResponseDTO response) {
        Instant now = Instant.now();
        String fileName = imageFileNameFromUrl(response.getImageUrl());
        DesignImage image = new DesignImage()
            .session(session)
            .imageType(ImageType.AI_RENDER)
            .fileName(fileName)
            .filePath(response.getImageUrl())
            .mimeType("image/jpeg")
            .isActive(true)
            .uploadedAt(now)
            .description(response.getBadge());

        designImageRepository.save(image);
    }

    private String imageFileNameFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return "visual-concept.jpg";
        }

        int slashIndex = imageUrl.lastIndexOf('/');
        String fileName = slashIndex >= 0 ? imageUrl.substring(slashIndex + 1) : imageUrl;
        if (fileName.isBlank()) {
            return "visual-concept.jpg";
        }
        return sanitizeImageFileName(fileName);
    }
}
