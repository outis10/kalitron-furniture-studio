package com.kalitron.studio.service.dto.croquis;

import java.io.Serializable;
import java.util.List;

/** Versioned nomenclature catalog exported at {@code GET /api/croquis/catalog}. */
public record CroquisCatalogDTO(
    String catalogVersion,
    int rulesEngineVersion,
    String minAppVersion,
    String wallCodePattern,
    String cornerCodePattern,
    List<CroquisCodeDTO> entries,
    List<RuleParamDTO> params,
    List<ValidationRuleDTO> validationRules
) implements Serializable {}
