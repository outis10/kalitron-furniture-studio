package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.ChatRequestDTO;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import com.kalitron.studio.service.dto.ChatSessionDTO;
import com.kalitron.studio.service.dto.ChatSessionStartRequestDTO;
import com.kalitron.studio.service.dto.ChatSessionSummaryDTO;
import com.kalitron.studio.service.dto.DesignProposalDTO;
import com.kalitron.studio.service.dto.SketchAnalysisRequestDTO;
import com.kalitron.studio.service.dto.SketchExtractionResponseDTO;
import com.kalitron.studio.service.dto.VisualConceptRequestDTO;
import com.kalitron.studio.service.dto.VisualConceptResponseDTO;
import java.util.List;
import java.util.Optional;

public interface DesignChatService {
    ChatSessionDTO startSession(ChatSessionStartRequestDTO request);

    Optional<ChatSessionDTO> resumeSession(String sessionCode);

    List<ChatSessionSummaryDTO> listSessions();

    Optional<DesignProposalDTO> findProposal(String sessionCode);

    ChatResponseDTO sendMessage(ChatRequestDTO request);

    VisualConceptResponseDTO generateVisualConcept(VisualConceptRequestDTO request);

    SketchExtractionResponseDTO analyzeSketch(SketchAnalysisRequestDTO request);
}
