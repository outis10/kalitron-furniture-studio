package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.ChatMessage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.MessageRole;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.ChatMessageRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.service.DesignChatService;
import com.kalitron.studio.service.dto.ChatMessageViewDTO;
import com.kalitron.studio.service.dto.ChatRequestDTO;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import com.kalitron.studio.service.dto.ChatSessionDTO;
import com.kalitron.studio.service.dto.ChatSessionStartRequestDTO;
import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DesignChatServiceImpl implements DesignChatService {

    private final DesignSessionRepository designSessionRepository;

    private final ChatMessageRepository chatMessageRepository;

    public DesignChatServiceImpl(DesignSessionRepository designSessionRepository, ChatMessageRepository chatMessageRepository) {
        this.designSessionRepository = designSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
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

        String message = request.getMessage().trim();
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        saveMessage(session, MessageRole.USER, message);
        updateProjectTypeFromMessage(session, message);
        if (normalizeSelectedStyle(request.getSelectedStyle()) != null) {
            session.setSelectedStyle(normalizeSelectedStyle(request.getSelectedStyle()));
        }
        session.setStatus(SessionStatus.CHATTING);
        session.setUpdatedAt(Instant.now());
        DesignSession savedSession = designSessionRepository.save(session);

        String reply = buildTemporaryAssistantReply(message);
        saveMessage(savedSession, MessageRole.ASSISTANT, reply);

        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(savedSession.getId());
        response.setSessionCode(savedSession.getSessionCode());
        response.setReply(reply);
        response.setSpecsReady(false);
        response.setSpecsSummary(null);
        return response;
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
        } else if (normalized.contains("closet") || normalized.contains("clóset")) {
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

    private String buildTemporaryAssistantReply(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("cocina") || normalized.contains("closet") || normalized.contains("ambos") || normalized.contains("both")) {
            return "Perfecto. ¿Qué forma tiene tu espacio: lineal, en L o en U? ¿Tienes medidas aproximadas?";
        }
        if (
            normalized.contains("moderno") ||
            normalized.contains("minimalista") ||
            normalized.contains("rustico") ||
            normalized.contains("rústico")
        ) {
            return "Entendido. ¿Prefieres acabado claro, madera natural o tonos oscuros? ¿Buscas algo económico, medio o premium?";
        }
        if (normalized.matches(".*\\d+.*")) {
            return "Gracias, esas medidas ayudan. ¿Dónde están tarja, estufa y refrigerador?";
        }
        return "Gracias. ¿Qué estilo visual prefieres? ¿Tienes medidas aproximadas del espacio?";
    }
}
