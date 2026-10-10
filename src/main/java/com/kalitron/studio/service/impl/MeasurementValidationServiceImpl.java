package com.kalitron.studio.service.impl;

import com.kalitron.studio.service.CroquisCatalogService;
import com.kalitron.studio.service.MeasurementValidationService;
import com.kalitron.studio.service.dto.croquis.ValidationIssueDTO;
import com.kalitron.studio.service.dto.measurement.SiteMeasurementPayloadDTO;
import com.kalitron.studio.service.validation.MeasurementRuleEngine;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MeasurementValidationServiceImpl implements MeasurementValidationService {

    private final MeasurementRuleEngine engine;

    public MeasurementValidationServiceImpl(CroquisCatalogService croquisCatalogService) {
        // Catalog defaults for now; admin overrides arrive with E13 #127.
        this.engine = new MeasurementRuleEngine(croquisCatalogService.getCatalog());
    }

    @Override
    public List<ValidationIssueDTO> validate(SiteMeasurementPayloadDTO measurement) {
        return engine.validate(measurement);
    }
}
