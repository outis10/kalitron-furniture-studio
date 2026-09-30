package com.kalitron.studio.service.dto.croquis;

import java.io.Serializable;

/**
 * Issue produced by a rule engine (E12 #113). Unused location fields are null.
 * Identical shape in Studio and in the KFS-APP Dart engine.
 */
public record ValidationIssueDTO(
    RuleSet ruleSet,
    String code,
    ValidationSeverity severity,
    RuleScope scope,
    String wallCode,
    String runCode,
    String cornerCode,
    String elementUuid,
    String itemUuid,
    String field,
    String message,
    boolean acknowledgeable,
    boolean acknowledged
) implements Serializable {}
