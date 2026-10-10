package com.kalitron.studio.service.impl;

import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import com.kalitron.studio.service.dto.croquis.CroquisCodeDTO;
import com.kalitron.studio.service.dto.croquis.RuleKind;
import com.kalitron.studio.service.dto.croquis.RuleParamDTO;
import com.kalitron.studio.service.dto.croquis.ValidationRuleDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Structural checks of the croquis catalog. Collects every problem and throws a
 * single {@link IllegalStateException} so a broken catalog never reaches clients.
 * Enum values (groups, kinds, scopes, prerequisites, obstacle/appliance types)
 * are already enforced by strict deserialization.
 */
final class CroquisCatalogValidator {

    private static final Pattern CATALOG_VERSION = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\.\\d+$");
    private static final Pattern SEMVER = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");
    private static final Pattern CODE = Pattern.compile("^[A-Za-z]{1,4}$");
    private static final Pattern RULE_CODE = Pattern.compile("^[A-Z][A-Z0-9_]{2,63}$");

    private CroquisCatalogValidator() {}

    static void validate(CroquisCatalogDTO catalog) {
        List<String> errors = new ArrayList<>();

        if (catalog.catalogVersion() == null || !CATALOG_VERSION.matcher(catalog.catalogVersion()).matches()) {
            errors.add("catalogVersion must look like 2026-10-01.1");
        }
        if (catalog.minAppVersion() == null || !SEMVER.matcher(catalog.minAppVersion()).matches()) {
            errors.add("minAppVersion must be semver (x.y.z)");
        }
        if (catalog.rulesEngineVersion() < 1 || catalog.rulesEngineVersion() > RuleKind.CURRENT_ENGINE_VERSION) {
            errors.add(
                "rulesEngineVersion " + catalog.rulesEngineVersion() + " not supported (engine " + RuleKind.CURRENT_ENGINE_VERSION + ")"
            );
        }
        checkPattern("wallCodePattern", catalog.wallCodePattern(), errors);
        checkPattern("cornerCodePattern", catalog.cornerCodePattern(), errors);

        validateEntries(nullSafe(catalog.entries()), errors);
        Set<String> paramKeys = validateParams(nullSafe(catalog.params()), errors);
        validateRules(nullSafe(catalog.validationRules()), paramKeys, catalog.rulesEngineVersion(), errors);

        if (!errors.isEmpty()) {
            throw new IllegalStateException("Invalid croquis catalog:\n - " + String.join("\n - ", errors));
        }
    }

    private static void validateEntries(List<CroquisCodeDTO> entries, List<String> errors) {
        if (entries.isEmpty()) {
            errors.add("entries must not be empty");
        }
        Set<String> seen = new HashSet<>();
        for (CroquisCodeDTO entry : entries) {
            String code = entry.code();
            if (code == null || !CODE.matcher(code).matches()) {
                errors.add("entry code '" + code + "' is invalid");
                continue;
            }
            if (!seen.add(code)) {
                errors.add("duplicate entry code " + code);
            }
            if (entry.group() == null) {
                errors.add(code + ": group is required");
            }
            if (isBlank(entry.labelEsMx())) {
                errors.add(code + ": labelEsMx is required");
            }
            if (nullSafe(entry.requiredFields()).isEmpty()) {
                errors.add(code + ": requiredFields must not be empty");
            }
            if (nullSafe(entry.projectTypes()).isEmpty()) {
                errors.add(code + ": projectTypes must not be empty");
            }
        }
    }

    private static Set<String> validateParams(List<RuleParamDTO> params, List<String> errors) {
        Set<String> keys = new HashSet<>();
        for (RuleParamDTO param : params) {
            if (isBlank(param.key())) {
                errors.add("param without key");
                continue;
            }
            if (!keys.add(param.key())) {
                errors.add("duplicate param " + param.key());
            }
            if (param.defaultValue() == null || param.min() == null || param.max() == null) {
                errors.add(param.key() + ": defaultValue, min and max are required");
            } else if (param.min().compareTo(param.max()) > 0) {
                errors.add(param.key() + ": min > max");
            } else if (param.defaultValue().compareTo(param.min()) < 0 || param.defaultValue().compareTo(param.max()) > 0) {
                errors.add(param.key() + ": defaultValue outside [min, max]");
            }
        }
        return keys;
    }

    private static void validateRules(List<ValidationRuleDTO> rules, Set<String> paramKeys, int engineVersion, List<String> errors) {
        Set<String> seen = new HashSet<>();
        for (ValidationRuleDTO rule : rules) {
            String code = rule.code();
            if (code == null || !RULE_CODE.matcher(code).matches()) {
                errors.add("rule code '" + code + "' is invalid");
                continue;
            }
            if (!seen.add(code)) {
                errors.add("duplicate rule " + code);
            }
            if (rule.ruleSet() == null || rule.scope() == null || rule.kind() == null || rule.severity() == null) {
                errors.add(code + ": ruleSet, scope, kind and severity are required");
                continue;
            }
            if (!rule.ruleSet().allows(rule.scope())) {
                errors.add(code + ": scope " + rule.scope() + " not allowed for ruleSet " + rule.ruleSet());
            }
            if (rule.kind().getSinceEngineVersion() > engineVersion) {
                errors.add(code + ": kind " + rule.kind() + " needs engine version " + rule.kind().getSinceEngineVersion());
            }
            Map<String, String> refs = rule.paramRefs() == null ? Map.of() : rule.paramRefs();
            Set<String> missing = refs
                .values()
                .stream()
                .filter(ref -> !paramKeys.contains(ref))
                .collect(Collectors.toSet());
            if (!missing.isEmpty()) {
                errors.add(code + ": unknown paramRefs " + missing);
            }
            if (isBlank(rule.messageEsMx())) {
                errors.add(code + ": messageEsMx is required");
            }
            if (nullSafe(rule.projectTypes()).isEmpty()) {
                errors.add(code + ": projectTypes must not be empty");
            }
        }
    }

    private static void checkPattern(String name, String regex, List<String> errors) {
        if (isBlank(regex)) {
            errors.add(name + " is required");
            return;
        }
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            errors.add(name + " is not a valid regex");
        }
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
