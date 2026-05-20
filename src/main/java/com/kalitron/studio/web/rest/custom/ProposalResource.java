package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.service.DesignChatService;
import com.kalitron.studio.service.dto.DesignProposalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/public/proposals")
public class ProposalResource {

    private final DesignChatService designChatService;

    public ProposalResource(DesignChatService designChatService) {
        this.designChatService = designChatService;
    }

    @GetMapping("/{sessionCode}")
    public ResponseEntity<DesignProposalDTO> getProposal(@PathVariable String sessionCode) {
        return designChatService
            .findProposal(sessionCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Design proposal not found"));
    }
}
