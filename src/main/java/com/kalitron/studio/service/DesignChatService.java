package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.ChatRequestDTO;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import com.kalitron.studio.service.dto.ChatSessionDTO;
import com.kalitron.studio.service.dto.ChatSessionStartRequestDTO;
import java.util.Optional;

public interface DesignChatService {
    ChatSessionDTO startSession(ChatSessionStartRequestDTO request);

    Optional<ChatSessionDTO> resumeSession(String sessionCode);

    ChatResponseDTO sendMessage(ChatRequestDTO request);
}
