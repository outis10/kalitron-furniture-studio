package com.kalitron.studio.service.dto.croquis;

import com.kalitron.studio.domain.enumeration.ProjectType;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Declarative validation rule executed by the Studio and app engines.
 * {@code params} holds fixed rule configuration; {@code paramRefs} maps rule
 * inputs to tunable {@link RuleParamDTO} keys.
 */
public record ValidationRuleDTO(
    String code,
    RuleSet ruleSet,
    RuleScope scope,
    RuleKind kind,
    ValidationSeverity severity,
    Map<String, Object> params,
    Map<String, String> paramRefs,
    List<RulePrerequisite> prerequisites,
    boolean acknowledgeable,
    List<ProjectType> projectTypes,
    String messageEsMx
) implements Serializable {}
