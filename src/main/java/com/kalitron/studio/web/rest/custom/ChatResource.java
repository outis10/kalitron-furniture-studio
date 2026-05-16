package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.service.DesignChatService;
import com.kalitron.studio.service.dto.ChatRequestDTO;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import com.kalitron.studio.service.dto.ChatSessionDTO;
import com.kalitron.studio.service.dto.ChatSessionStartRequestDTO;
import com.kalitron.studio.service.dto.VisualConceptRequestDTO;
import com.kalitron.studio.service.dto.VisualConceptResponseDTO;
import com.kalitron.studio.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chat")
public class ChatResource {

    private static final String ENTITY_NAME = "designChat";

    private final DesignChatService designChatService;

    public ChatResource(DesignChatService designChatService) {
        this.designChatService = designChatService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionDTO> startSession(@Valid @RequestBody ChatSessionStartRequestDTO request) {
        ChatSessionDTO session = designChatService.startSession(request);
        return ResponseEntity.created(URI.create("/api/chat/sessions/" + session.getSessionCode())).body(session);
    }

    @GetMapping("/sessions/{sessionCode}")
    public ResponseEntity<ChatSessionDTO> resumeSession(@PathVariable String sessionCode) {
        return designChatService
            .resumeSession(sessionCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Design session not found"));
    }

    @PostMapping("/message")
    public ResponseEntity<ChatResponseDTO> sendMessage(@Valid @RequestBody ChatRequestDTO request) {
        try {
            return ResponseEntity.ok(designChatService.sendMessage(request));
        } catch (IllegalArgumentException e) {
            if ("Design session not found".equals(e.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidchatmessage");
        }
    }

    @PostMapping("/visual-concept")
    public ResponseEntity<VisualConceptResponseDTO> generateVisualConcept(@Valid @RequestBody VisualConceptRequestDTO request) {
        try {
            return ResponseEntity.ok(designChatService.generateVisualConcept(request));
        } catch (IllegalArgumentException e) {
            if ("Design session not found".equals(e.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidvisualconcept");
        }
    }
}
