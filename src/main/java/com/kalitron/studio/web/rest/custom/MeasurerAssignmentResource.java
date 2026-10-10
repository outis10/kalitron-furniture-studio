package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.security.AuthoritiesConstants;
import com.kalitron.studio.service.MeasurerAssignmentException;
import com.kalitron.studio.service.MeasurerAssignmentService;
import com.kalitron.studio.service.dto.measurer.MeasurerAssignmentDTO;
import com.kalitron.studio.service.dto.measurer.MeasurerSummaryDTO;
import com.kalitron.studio.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Assigns the site measurer of a design session (E12 #116). ROLE_DESIGNER joins
 * ROLE_ADMIN here once E13 #126 exists.
 */
@RestController
@RequestMapping("/api")
public class MeasurerAssignmentResource {

    private static final String ENTITY_NAME = "measurerAssignment";

    private final MeasurerAssignmentService measurerAssignmentService;

    public MeasurerAssignmentResource(MeasurerAssignmentService measurerAssignmentService) {
        this.measurerAssignmentService = measurerAssignmentService;
    }

    public record AssignMeasurerRequest(String userLogin) {}

    @PutMapping("/design-sessions/{id}/assigned-measurer")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<MeasurerAssignmentDTO> assign(@PathVariable("id") Long id, @RequestBody AssignMeasurerRequest request) {
        try {
            return ResponseEntity.ok(measurerAssignmentService.assign(id, request.userLogin()));
        } catch (MeasurerAssignmentException e) {
            switch (e.getReason()) {
                case SESSION_NOT_FOUND, USER_NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                default -> throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, e.getReason().name());
            }
        }
    }

    @GetMapping("/measurers")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<MeasurerSummaryDTO> getAssignableMeasurers() {
        return measurerAssignmentService.findAssignableMeasurers();
    }
}
