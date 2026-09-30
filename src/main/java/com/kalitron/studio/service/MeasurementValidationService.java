package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.croquis.ValidationIssueDTO;
import com.kalitron.studio.service.dto.measurement.SiteMeasurementPayloadDTO;
import java.util.List;

/**
 * Authoritative validation of a site measurement with the current catalog
 * (E12 #113). Used by the sync API (#112) on upsert and confirm.
 */
public interface MeasurementValidationService {
    List<ValidationIssueDTO> validate(SiteMeasurementPayloadDTO measurement);
}
