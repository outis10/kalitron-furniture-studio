package com.kalitron.studio.service.validation;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import com.kalitron.studio.service.dto.croquis.CroquisCodeDTO;
import com.kalitron.studio.service.dto.croquis.RuleKind;
import com.kalitron.studio.service.dto.croquis.RuleParamDTO;
import com.kalitron.studio.service.dto.croquis.RulePrerequisite;
import com.kalitron.studio.service.dto.croquis.RuleScope;
import com.kalitron.studio.service.dto.croquis.RuleSet;
import com.kalitron.studio.service.dto.croquis.ValidationIssueDTO;
import com.kalitron.studio.service.dto.croquis.ValidationRuleDTO;
import com.kalitron.studio.service.dto.measurement.MeasuredValueDTO;
import com.kalitron.studio.service.dto.measurement.MeasurementCornerDTO;
import com.kalitron.studio.service.dto.measurement.MeasurementElementDTO;
import com.kalitron.studio.service.dto.measurement.MeasurementWallDTO;
import com.kalitron.studio.service.dto.measurement.SiteMeasurementPayloadDTO;
import com.kalitron.studio.service.dto.measurement.ValueSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Pure executor of the catalog's {@code MEASUREMENT} rules (E12 #113).
 *
 * <p>Semantics shared with the KFS-APP Dart engine and pinned by the
 * conformance vectors:
 * <ul>
 *   <li>every applicable rule is evaluated (no short-circuit);</li>
 *   <li>unmet prerequisites skip the rule for that target without an issue;</li>
 *   <li>issues are returned in a deterministic order (see {@link #ORDER}).</li>
 * </ul>
 */
public final class MeasurementRuleEngine {

    private static final List<String> WALL_VALUE_FIELDS = List.of(
        "lengthFloorMm",
        "length900Mm",
        "lengthCeilingMm",
        "outOfPlumbMm",
        "closingMm"
    );
    private static final List<String> ELEMENT_VALUE_FIELDS = List.of("xMm", "yMm", "widthMm", "heightMm", "depthMm");

    /**
     * Measurement-level issues first, then site issues, then walls in input
     * order; inside a wall, wall-level issues before element issues; elements
     * by X (missing X last) then uuid; then rule code and field.
     */
    private static final Comparator<Located> ORDER = Comparator.comparingInt(Located::scopeRank)
        .thenComparingInt(Located::wallIndex)
        .thenComparingInt(Located::elementRank)
        .thenComparing(Located::elementX, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(Located::elementUuid, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(located -> located.issue().code())
        .thenComparing(located -> located.issue().cornerCode(), Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(located -> located.issue().field(), Comparator.nullsLast(Comparator.naturalOrder()));

    private final CroquisCatalogDTO catalog;
    private final Map<String, BigDecimal> params;
    private final Map<String, CroquisCodeDTO> codes;

    /**
     * @param catalog         validated catalog
     * @param paramOverrides  effective parameter values (e.g. admin overrides,
     *                        #127); missing keys use catalog defaults
     */
    public MeasurementRuleEngine(CroquisCatalogDTO catalog, Map<String, BigDecimal> paramOverrides) {
        this.catalog = Objects.requireNonNull(catalog);
        Map<String, BigDecimal> effective = new HashMap<>();
        for (RuleParamDTO param : catalog.params()) {
            effective.put(param.key(), param.defaultValue());
        }
        effective.putAll(paramOverrides == null ? Map.of() : paramOverrides);
        this.params = Map.copyOf(effective);
        this.codes = catalog.entries().stream().collect(Collectors.toMap(CroquisCodeDTO::code, Function.identity()));
    }

    public MeasurementRuleEngine(CroquisCatalogDTO catalog) {
        this(catalog, Map.of());
    }

    public List<ValidationIssueDTO> validate(SiteMeasurementPayloadDTO measurement) {
        ProjectType projectType = Optional.ofNullable(measurement.projectType()).orElse(ProjectType.KITCHEN);
        List<MeasurementWallDTO> walls = nullSafe(measurement.walls());
        List<Located> found = new ArrayList<>();

        for (ValidationRuleDTO rule : catalog.validationRules()) {
            if (rule.ruleSet() != RuleSet.MEASUREMENT || !rule.projectTypes().contains(projectType)) {
                continue;
            }
            switch (rule.scope()) {
                case WALL -> {
                    for (int i = 0; i < walls.size(); i++) {
                        MeasurementWallDTO wall = walls.get(i);
                        if (wallPrerequisitesMet(rule, wall)) {
                            for (Issue issue : evaluateWall(rule, wall)) {
                                found.add(located(rule, issue, 2, i, wall, null));
                            }
                        }
                    }
                }
                case ELEMENT -> {
                    for (int i = 0; i < walls.size(); i++) {
                        MeasurementWallDTO wall = walls.get(i);
                        for (MeasurementElementDTO element : nullSafe(wall.elements())) {
                            if (elementPrerequisitesMet(rule, wall, element)) {
                                for (Issue issue : evaluateElement(rule, wall, element)) {
                                    found.add(located(rule, issue, 2, i, wall, element));
                                }
                            }
                        }
                    }
                }
                case SITE -> evaluateSite(rule, measurement).forEach(issue -> found.add(located(rule, issue, 1, -1, null, null)));
                case MEASUREMENT -> evaluateMeasurement(rule, measurement).forEach(issue ->
                    found.add(located(rule, issue, 0, -1, null, null))
                );
                default -> throw new IllegalStateException("Scope " + rule.scope() + " is not a MEASUREMENT scope");
            }
        }
        return found.stream().sorted(ORDER).map(Located::issue).toList();
    }

    // ---- prerequisites --------------------------------------------------

    private boolean wallPrerequisitesMet(ValidationRuleDTO rule, MeasurementWallDTO wall) {
        for (RulePrerequisite prerequisite : nullSafe(rule.prerequisites())) {
            boolean met = switch (prerequisite) {
                case WALL_COMPLETE -> wall.hasAllLengths();
                case ELEMENT_HAS_XY -> nullSafe(wall.elements()).stream().anyMatch(MeasurementRuleEngine::hasXy);
                case ELEMENT_HAS_WIDTH -> nullSafe(wall.elements()).stream().anyMatch(MeasurementRuleEngine::hasXAndWidth);
                case CODE_KNOWN, ROOM_CLOSED, MEASUREMENT_PRESENT -> true;
            };
            if (!met) {
                return false;
            }
        }
        return true;
    }

    private boolean elementPrerequisitesMet(ValidationRuleDTO rule, MeasurementWallDTO wall, MeasurementElementDTO element) {
        for (RulePrerequisite prerequisite : nullSafe(rule.prerequisites())) {
            boolean met = switch (prerequisite) {
                case WALL_COMPLETE -> wall.hasAllLengths();
                case ELEMENT_HAS_XY -> hasXy(element);
                case ELEMENT_HAS_WIDTH -> MeasuredValueDTO.present(element.widthMm());
                case CODE_KNOWN -> codes.containsKey(element.code());
                case ROOM_CLOSED, MEASUREMENT_PRESENT -> true;
            };
            if (!met) {
                return false;
            }
        }
        return true;
    }

    // ---- WALL scope -----------------------------------------------------

    private List<Issue> evaluateWall(ValidationRuleDTO rule, MeasurementWallDTO wall) {
        Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("wallCode", wall.wallCode());
        return switch (rule.kind()) {
            case REQUIRED_FIELDS -> {
                if (
                    "WALL_HAS_ELEMENTS_WITH_WIDTH".equals(rule.params().get("when")) &&
                    nullSafe(wall.elements())
                        .stream()
                        .noneMatch(e -> hasXAndWidth(e))
                ) {
                    yield List.of();
                }
                List<String> missing = stringList(rule, "fields")
                    .stream()
                    .filter(field -> !MeasuredValueDTO.present(wallValue(wall, field)))
                    .toList();
                if (missing.isEmpty()) {
                    yield List.of();
                }
                vars.put("missing", String.join(", ", missing));
                yield List.of(new Issue(String.join(",", missing), null, null, vars));
            }
            case SPREAD_WITHIN_TOLERANCE -> {
                List<Integer> values = stringList(rule, "fields")
                    .stream()
                    .map(field -> MeasuredValueDTO.valueOf(wallValue(wall, field)))
                    .toList();
                if (values.stream().anyMatch(Objects::isNull)) {
                    yield List.of();
                }
                int spread = values.stream().max(Integer::compare).orElseThrow() - values.stream().min(Integer::compare).orElseThrow();
                int tolerance = param(rule, "toleranceMm");
                if (spread <= tolerance) {
                    yield List.of();
                }
                vars.put("spread", spread);
                vars.put("toleranceMm", tolerance);
                yield List.of(new Issue(null, null, null, vars));
            }
            case SUM_WITHIN_TOLERANCE -> evaluateClosure(rule, wall, vars);
            case HAS_ATTACHMENT -> {
                int min = ((Number) rule.params().getOrDefault("min", 1)).intValue();
                yield nullSafe(wall.photoUuids()).size() >= min ? List.of() : List.of(new Issue(null, null, null, vars));
            }
            case SOURCE_IS -> evaluateSource(rule, wall, vars);
            default -> throw new IllegalStateException(rule.code() + ": kind " + rule.kind() + " not supported at WALL scope");
        };
    }

    /** X + A of the rightmost element + closing measurement ≈ design length. */
    private List<Issue> evaluateClosure(ValidationRuleDTO rule, MeasurementWallDTO wall, Map<String, Object> vars) {
        if (!MeasuredValueDTO.present(wall.closingMm())) {
            return List.of();
        }
        Optional<Integer> rightEdge = nullSafe(wall.elements())
            .stream()
            .filter(MeasurementRuleEngine::hasXAndWidth)
            .map(e -> e.xMm().value() + e.widthMm().value())
            .max(Integer::compare);
        if (rightEdge.isEmpty()) {
            return List.of();
        }
        int sum = rightEdge.get() + wall.closingMm().value();
        int length = wall.designLengthMm();
        int delta = Math.abs(sum - length);
        if (delta <= param(rule, "toleranceMm")) {
            return List.of();
        }
        vars.put("sum", sum);
        vars.put("length", length);
        vars.put("delta", delta);
        return List.of(new Issue("closingMm", null, null, vars));
    }

    private List<Issue> evaluateSource(ValidationRuleDTO rule, MeasurementWallDTO wall, Map<String, Object> wallVars) {
        ValueSource wanted = ValueSource.valueOf(String.valueOf(rule.params().getOrDefault("source", "MANUAL")));
        List<Issue> issues = new ArrayList<>();
        for (String field : WALL_VALUE_FIELDS) {
            MeasuredValueDTO value = wallValue(wall, field);
            if (MeasuredValueDTO.present(value) && value.source() == wanted) {
                Map<String, Object> vars = new LinkedHashMap<>(wallVars);
                vars.put("field", field);
                issues.add(new Issue(field, null, null, vars));
            }
        }
        if (Boolean.TRUE.equals(rule.params().get("includeElements"))) {
            for (MeasurementElementDTO element : nullSafe(wall.elements())) {
                for (String field : ELEMENT_VALUE_FIELDS) {
                    MeasuredValueDTO value = elementValue(element, field);
                    if (MeasuredValueDTO.present(value) && value.source() == wanted) {
                        Map<String, Object> vars = new LinkedHashMap<>(wallVars);
                        vars.put("field", element.code() + "." + field);
                        issues.add(new Issue(field, element, null, vars));
                    }
                }
            }
        }
        return issues;
    }

    // ---- ELEMENT scope --------------------------------------------------

    private List<Issue> evaluateElement(ValidationRuleDTO rule, MeasurementWallDTO wall, MeasurementElementDTO element) {
        Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("wallCode", wall.wallCode());
        vars.put("code", element.code());
        return switch (rule.kind()) {
            case CODE_IN_CATALOG -> codes.containsKey(element.code()) ? List.of() : List.of(new Issue("code", element, null, vars));
            case REQUIRED_FIELDS -> {
                List<String> groups = stringList(rule, "groups");
                CroquisCodeDTO entry = codes.get(element.code());
                if (!groups.isEmpty() && (entry == null || !groups.contains(entry.group().name()))) {
                    yield List.of();
                }
                List<String> missing = stringList(rule, "fields")
                    .stream()
                    .filter(f -> !MeasuredValueDTO.present(elementValue(element, f)))
                    .toList();
                if (missing.isEmpty()) {
                    yield List.of();
                }
                vars.put("missing", String.join(", ", missing));
                yield List.of(new Issue(String.join(",", missing), element, null, vars));
            }
            case WITHIN_BOUNDS -> {
                int length = wall.designLengthMm();
                int start = element.xMm().value();
                int end = start + Optional.ofNullable(MeasuredValueDTO.valueOf(element.widthMm())).orElse(0);
                if (start >= 0 && end <= length) {
                    yield List.of();
                }
                vars.put("end", end);
                vars.put("length", length);
                yield List.of(new Issue("xMm", element, null, vars));
            }
            case NO_OVERLAP -> evaluateOverlap(rule, wall, element, vars);
            default -> throw new IllegalStateException(rule.code() + ": kind " + rule.kind() + " not supported at ELEMENT scope");
        };
    }

    /**
     * Reports the overlap on the later element of each pair (by X, then uuid)
     * so each overlapping pair yields exactly one issue.
     */
    private List<Issue> evaluateOverlap(
        ValidationRuleDTO rule,
        MeasurementWallDTO wall,
        MeasurementElementDTO element,
        Map<String, Object> vars
    ) {
        List<String> excluded = stringList(rule, "excludeGroups");
        if (!isRectangle(element) || isExcluded(element, excluded)) {
            return List.of();
        }
        List<Issue> issues = new ArrayList<>();
        for (MeasurementElementDTO other : nullSafe(wall.elements())) {
            if (other == element || !isRectangle(other) || isExcluded(other, excluded) || compareElements(other, element) >= 0) {
                continue;
            }
            if (overlaps(element, other)) {
                Map<String, Object> pairVars = new LinkedHashMap<>(vars);
                pairVars.put("otherCode", other.code());
                issues.add(new Issue(null, element, null, pairVars));
            }
        }
        return issues;
    }

    private boolean isExcluded(MeasurementElementDTO element, List<String> excludedGroups) {
        CroquisCodeDTO entry = codes.get(element.code());
        return entry == null || excludedGroups.contains(entry.group().name());
    }

    private static boolean isRectangle(MeasurementElementDTO e) {
        return hasXy(e) && MeasuredValueDTO.present(e.widthMm()) && MeasuredValueDTO.present(e.heightMm());
    }

    /** Strict overlap: touching edges do not overlap. */
    private static boolean overlaps(MeasurementElementDTO a, MeasurementElementDTO b) {
        int ax1 = a.xMm().value();
        int ax2 = ax1 + a.widthMm().value();
        int ay1 = a.yMm().value();
        int ay2 = ay1 + a.heightMm().value();
        int bx1 = b.xMm().value();
        int bx2 = bx1 + b.widthMm().value();
        int by1 = b.yMm().value();
        int by2 = by1 + b.heightMm().value();
        return ax1 < bx2 && bx1 < ax2 && ay1 < by2 && by1 < ay2;
    }

    private static int compareElements(MeasurementElementDTO a, MeasurementElementDTO b) {
        return Comparator.comparing(
            (MeasurementElementDTO e) -> MeasuredValueDTO.valueOf(e.xMm()),
            Comparator.nullsLast(Comparator.naturalOrder())
        )
            .thenComparing(MeasurementElementDTO::elementUuid, Comparator.nullsLast(Comparator.naturalOrder()))
            .compare(a, b);
    }

    // ---- SITE / MEASUREMENT scope ---------------------------------------

    private List<Issue> evaluateSite(ValidationRuleDTO rule, SiteMeasurementPayloadDTO measurement) {
        if (rule.kind() != RuleKind.THRESHOLD_EXCEEDED) {
            throw new IllegalStateException(rule.code() + ": kind " + rule.kind() + " not supported at SITE scope");
        }
        String field = String.valueOf(rule.params().get("field"));
        return switch (field) {
            case "site.floorOutOfLevelMm" -> {
                Integer value = measurement.site() == null ? null : measurement.site().floorOutOfLevelMm();
                int threshold = param(rule, "thresholdMm");
                if (value == null || Math.abs(value) <= threshold) {
                    yield List.of();
                }
                Map<String, Object> vars = new LinkedHashMap<>();
                vars.put("value", value);
                vars.put("thresholdMm", threshold);
                yield List.of(new Issue("floorOutOfLevelMm", null, null, vars));
            }
            case "corner.angleDeg" -> {
                int expected = ((Number) rule.params().getOrDefault("expected", 90)).intValue();
                int threshold = ((Number) rule.params().getOrDefault("threshold", 0)).intValue();
                List<Issue> issues = new ArrayList<>();
                for (MeasurementCornerDTO corner : nullSafe(measurement.corners())) {
                    if (corner.angleDeg() != null && Math.abs(corner.angleDeg() - expected) > threshold) {
                        Map<String, Object> vars = new LinkedHashMap<>();
                        vars.put("cornerCode", corner.cornerCode());
                        vars.put("value", corner.angleDeg());
                        issues.add(new Issue("angleDeg", null, corner.cornerCode(), vars));
                    }
                }
                yield issues;
            }
            default -> throw new IllegalStateException(rule.code() + ": unknown SITE field " + field);
        };
    }

    private List<Issue> evaluateMeasurement(ValidationRuleDTO rule, SiteMeasurementPayloadDTO measurement) {
        // No MEASUREMENT-scope kinds yet; the catalog validator rejects such rules.
        return List.of();
    }

    // ---- helpers --------------------------------------------------------

    private int param(ValidationRuleDTO rule, String name) {
        String key = rule.paramRefs().get(name);
        BigDecimal value = key == null ? null : params.get(key);
        if (value == null) {
            throw new IllegalStateException(rule.code() + ": param " + name + " is not defined");
        }
        return value.intValueExact();
    }

    @SuppressWarnings("unchecked")
    private static List<String> stringList(ValidationRuleDTO rule, String name) {
        Object value = rule.params().get(name);
        if (value == null) {
            return List.of();
        }
        return ((Collection<Object>) value).stream().map(String::valueOf).toList();
    }

    private static MeasuredValueDTO wallValue(MeasurementWallDTO wall, String field) {
        return switch (field) {
            case "lengthFloorMm" -> wall.lengthFloorMm();
            case "length900Mm" -> wall.length900Mm();
            case "lengthCeilingMm" -> wall.lengthCeilingMm();
            case "outOfPlumbMm" -> wall.outOfPlumbMm();
            case "closingMm" -> wall.closingMm();
            default -> throw new IllegalStateException("Unknown wall field " + field);
        };
    }

    private static MeasuredValueDTO elementValue(MeasurementElementDTO element, String field) {
        return switch (field) {
            case "xMm" -> element.xMm();
            case "yMm" -> element.yMm();
            case "widthMm" -> element.widthMm();
            case "heightMm" -> element.heightMm();
            case "depthMm" -> element.depthMm();
            default -> throw new IllegalStateException("Unknown element field " + field);
        };
    }

    private static boolean hasXy(MeasurementElementDTO e) {
        return MeasuredValueDTO.present(e.xMm()) && MeasuredValueDTO.present(e.yMm());
    }

    private static boolean hasXAndWidth(MeasurementElementDTO e) {
        return MeasuredValueDTO.present(e.xMm()) && MeasuredValueDTO.present(e.widthMm());
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }

    private Located located(
        ValidationRuleDTO rule,
        Issue issue,
        int scopeRank,
        int wallIndex,
        MeasurementWallDTO wall,
        MeasurementElementDTO scopedElement
    ) {
        MeasurementElementDTO element = issue.element() != null ? issue.element() : scopedElement;
        String message = MessageTemplate.render(rule.messageEsMx(), issue.vars());
        ValidationIssueDTO dto = new ValidationIssueDTO(
            rule.ruleSet(),
            rule.code(),
            rule.severity(),
            rule.scope(),
            wall == null ? null : wall.wallCode(),
            null,
            issue.cornerCode(),
            element == null ? null : element.elementUuid(),
            null,
            issue.field(),
            message,
            rule.acknowledgeable(),
            false
        );
        return new Located(
            dto,
            scopeRank,
            wallIndex,
            element == null ? 0 : 1,
            element == null ? null : MeasuredValueDTO.valueOf(element.xMm()),
            element == null ? null : element.elementUuid()
        );
    }

    private record Issue(String field, MeasurementElementDTO element, String cornerCode, Map<String, Object> vars) {}

    private record Located(ValidationIssueDTO issue, int scopeRank, int wallIndex, int elementRank, Integer elementX, String elementUuid) {}

    /**
     * (scope, kind) combinations implemented for {@code MEASUREMENT} rules.
     * The catalog validator rejects any other combination at startup.
     */
    private static final Map<RuleScope, Set<RuleKind>> SUPPORTED = Map.of(
        RuleScope.WALL,
        EnumSet.of(
            RuleKind.REQUIRED_FIELDS,
            RuleKind.SPREAD_WITHIN_TOLERANCE,
            RuleKind.SUM_WITHIN_TOLERANCE,
            RuleKind.HAS_ATTACHMENT,
            RuleKind.SOURCE_IS
        ),
        RuleScope.ELEMENT,
        EnumSet.of(RuleKind.CODE_IN_CATALOG, RuleKind.REQUIRED_FIELDS, RuleKind.WITHIN_BOUNDS, RuleKind.NO_OVERLAP),
        RuleScope.SITE,
        EnumSet.of(RuleKind.THRESHOLD_EXCEEDED),
        RuleScope.MEASUREMENT,
        EnumSet.noneOf(RuleKind.class)
    );

    public static boolean supports(RuleScope scope, RuleKind kind) {
        return SUPPORTED.getOrDefault(scope, Set.of()).contains(kind);
    }
}
