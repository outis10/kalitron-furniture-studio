package com.kalitron.studio.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import com.kalitron.studio.service.dto.croquis.CroquisCodeDTO;
import com.kalitron.studio.service.dto.croquis.RuleKind;
import com.kalitron.studio.service.dto.croquis.RuleSet;
import com.kalitron.studio.service.dto.croquis.ValidationRuleDTO;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class CroquisCatalogServiceImplTest {

    private static final Resource CATALOG = new ClassPathResource("croquis/catalog.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final CroquisCatalogServiceImpl service = new CroquisCatalogServiceImpl(CATALOG);

    @Test
    void loadsTheShippedCatalog() {
        CroquisCatalogDTO catalog = service.getCatalog();

        assertThat(catalog.catalogVersion()).matches("\\d{4}-\\d{2}-\\d{2}\\.\\d+");
        assertThat(catalog.rulesEngineVersion()).isEqualTo(RuleKind.CURRENT_ENGINE_VERSION);
        assertThat(catalog.entries())
            .extracting(CroquisCodeDTO::code)
            .containsExactlyInAnyOrder(
                "V",
                "P",
                "TA",
                "DR",
                "GS",
                "CT",
                "AP",
                "CE",
                "CL",
                "VG",
                "TB",
                "RG",
                "RF",
                "ES",
                "PA",
                "HO",
                "CA",
                "MW",
                "LV",
                "TJ",
                "dP",
                "dPl"
            );
        assertThat(catalog.validationRules())
            .extracting(ValidationRuleDTO::code)
            .containsExactlyInAnyOrder(
                "WALL_INCOMPLETE",
                "WALL_LENGTH_SPREAD",
                "WALL_CLOSURE_MISMATCH",
                "WALL_CLOSURE_MISSING",
                "WALL_WITHOUT_PHOTO",
                "MANUAL_VALUE",
                "UNKNOWN_CODE",
                "ELEMENT_MISSING_XY",
                "APPLIANCE_MISSING_DIMS",
                "ELEMENT_OUT_OF_WALL",
                "ELEMENTS_OVERLAP",
                "FLOOR_OUT_OF_LEVEL",
                "CORNER_NOT_SQUARE"
            );
        assertThat(catalog.validationRules()).allMatch(rule -> rule.ruleSet() == RuleSet.MEASUREMENT);
    }

    @Test
    void labelsMatchTheSpanishValueOfTheMappedEnum() {
        for (CroquisCodeDTO entry : service.getCatalog().entries()) {
            if (entry.applianceType() != null) {
                assertThat(entry.labelEsMx()).as(entry.code()).isEqualTo(entry.applianceType().getValue());
            } else if (entry.obstacleType() != null) {
                assertThat(entry.labelEsMx()).as(entry.code()).isEqualTo(entry.obstacleType().getValue());
            }
        }
    }

    @Test
    void everyNonSiteCodeMapsToAnObstacleType() {
        assertThat(service.getCatalog().entries())
            .filteredOn(entry -> entry.group() != com.kalitron.studio.service.dto.croquis.CroquisGroup.SITE)
            .allSatisfy(entry -> assertThat(entry.obstacleType()).as(entry.code()).isNotNull());
    }

    @Test
    void everyRuleKindIsSupportedByTheEngineVersion() {
        CroquisCatalogDTO catalog = service.getCatalog();
        assertThat(catalog.validationRules()).allSatisfy(rule ->
            assertThat(rule.kind().getSinceEngineVersion()).as(rule.code()).isLessThanOrEqualTo(catalog.rulesEngineVersion())
        );
    }

    @Test
    void findByCodeIsCaseSensitive() {
        assertThat(service.findByCode("dP")).isPresent();
        assertThat(service.findByCode("DP")).isEmpty();
        assertThat(service.findByCode(null)).isEmpty();
    }

    @Test
    void rejectsUnknownRuleKind() {
        assertInvalid(root -> firstRule(root).put("kind", "TELEPATHY"), "TELEPATHY");
    }

    @Test
    void rejectsUnknownObstacleType() {
        assertInvalid(root -> firstEntry(root).put("obstacleType", "SPACESHIP"), "SPACESHIP");
    }

    @Test
    void rejectsUnknownProperty() {
        assertInvalid(root -> root.put("surprise", true), "surprise");
    }

    @Test
    void rejectsDuplicateCodes() {
        assertInvalid(root -> ((ArrayNode) root.get("entries")).add(firstEntry(root).deepCopy()), "duplicate entry code V");
    }

    @Test
    void rejectsScopeNotAllowedForRuleSet() {
        assertInvalid(root -> firstRule(root).put("scope", "ITEM"), "scope ITEM not allowed for ruleSet MEASUREMENT");
    }

    @Test
    void rejectsKindNotSupportedAtScope() {
        assertInvalid(root -> rule(root, "FLOOR_OUT_OF_LEVEL").put("kind", "NO_OVERLAP"), "kind NO_OVERLAP not supported at scope SITE");
    }

    @Test
    void rejectsUnknownParamRef() {
        assertInvalid(
            root -> ((ObjectNode) rule(root, "WALL_LENGTH_SPREAD").get("paramRefs")).put("toleranceMm", "measure.nope"),
            "unknown paramRefs [measure.nope]"
        );
    }

    @Test
    void rejectsDefaultOutsideBounds() {
        assertInvalid(root -> ((ObjectNode) root.get("params").get(0)).put("defaultValue", 500), "defaultValue outside [min, max]");
    }

    @Test
    void rejectsUnsupportedEngineVersion() {
        assertInvalid(root -> root.put("rulesEngineVersion", 99), "rulesEngineVersion 99 not supported");
    }

    @Test
    void rejectsBadCatalogVersion() {
        assertInvalid(root -> root.put("catalogVersion", "v1"), "catalogVersion must look like");
    }

    private static void assertInvalid(Consumer<ObjectNode> mutation, String expectedMessage) {
        ObjectNode root = readShippedCatalog();
        mutation.accept(root);
        Resource resource = new ByteArrayResource(root.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));

        assertThatThrownBy(() -> new CroquisCatalogServiceImpl(resource))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(expectedMessage);
    }

    private static ObjectNode readShippedCatalog() {
        try (InputStream in = CATALOG.getInputStream()) {
            return (ObjectNode) MAPPER.readTree(in);
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ObjectNode firstEntry(ObjectNode root) {
        return (ObjectNode) root.get("entries").get(0);
    }

    private static ObjectNode firstRule(ObjectNode root) {
        return (ObjectNode) root.get("validationRules").get(0);
    }

    private static ObjectNode rule(ObjectNode root, String code) {
        for (JsonNode rule : (List<JsonNode>) toList(root.get("validationRules"))) {
            if (code.equals(rule.get("code").asText())) {
                return (ObjectNode) rule;
            }
        }
        throw new IllegalArgumentException(code);
    }

    private static List<JsonNode> toList(JsonNode array) {
        List<JsonNode> nodes = new java.util.ArrayList<>();
        array.forEach(nodes::add);
        return nodes;
    }
}
