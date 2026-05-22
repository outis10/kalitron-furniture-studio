package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.service.MeasuredLayoutService;
import com.kalitron.studio.service.dto.MeasuredLayoutRequestDTO;
import com.kalitron.studio.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/design-sessions")
public class MeasuredLayoutResource {

    private static final String ENTITY_NAME = "measuredLayout";

    private final MeasuredLayoutService measuredLayoutService;

    public MeasuredLayoutResource(MeasuredLayoutService measuredLayoutService) {
        this.measuredLayoutService = measuredLayoutService;
    }

    @PutMapping("/{sessionId}/measured-layout")
    public ResponseEntity<MeasuredLayoutRequestDTO> saveMeasuredLayout(
        @PathVariable Long sessionId,
        @Valid @RequestBody MeasuredLayoutRequestDTO request
    ) {
        try {
            return ResponseEntity.ok(measuredLayoutService.saveMeasuredLayout(sessionId, request));
        } catch (IllegalArgumentException e) {
            if ("Design session not found".equals(e.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidmeasuredlayout");
        }
    }

    @GetMapping("/{sessionId}/measured-layout")
    public ResponseEntity<MeasuredLayoutRequestDTO> getMeasuredLayout(@PathVariable Long sessionId) {
        return measuredLayoutService.findMeasuredLayout(sessionId).map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }
}
