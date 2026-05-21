package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.service.CabinetPlanService;
import com.kalitron.studio.service.dto.CabinetPlanResponseDTO;
import com.kalitron.studio.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/design-sessions")
public class CabinetPlanResource {

    private static final String ENTITY_NAME = "cabinetPlan";

    private final CabinetPlanService cabinetPlanService;

    public CabinetPlanResource(CabinetPlanService cabinetPlanService) {
        this.cabinetPlanService = cabinetPlanService;
    }

    @PostMapping("/{sessionId}/cabinet-plan")
    public ResponseEntity<CabinetPlanResponseDTO> generateCabinetPlan(@PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(cabinetPlanService.generateCabinetPlan(sessionId));
        } catch (IllegalArgumentException e) {
            if ("Design session not found".equals(e.getMessage()) || "Measured layout not found".equals(e.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidcabinetplan");
        }
    }

    @PutMapping("/{sessionId}/cabinet-plan")
    public ResponseEntity<CabinetPlanResponseDTO> saveCabinetPlan(
        @PathVariable Long sessionId,
        @Valid @RequestBody CabinetPlanResponseDTO cabinetPlan
    ) {
        try {
            return ResponseEntity.ok(cabinetPlanService.saveCabinetPlan(sessionId, cabinetPlan));
        } catch (IllegalArgumentException e) {
            if ("Design session not found".equals(e.getMessage()) || "Measured layout not found".equals(e.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidcabinetplan");
        }
    }

    @GetMapping("/{sessionId}/cabinet-plan")
    public ResponseEntity<CabinetPlanResponseDTO> getCabinetPlan(@PathVariable Long sessionId) {
        return cabinetPlanService
            .findCabinetPlan(sessionId)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cabinet plan not found"));
    }
}
