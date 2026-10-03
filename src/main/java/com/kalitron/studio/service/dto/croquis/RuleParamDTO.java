package com.kalitron.studio.service.dto.croquis;

import com.kalitron.studio.domain.enumeration.ProjectType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/** Tunable numeric parameter referenced by rules through {@code paramRefs}. */
public record RuleParamDTO(
    String key,
    BigDecimal defaultValue,
    String unit,
    BigDecimal min,
    BigDecimal max,
    String labelEsMx,
    List<ProjectType> projectTypes
) implements Serializable {}
