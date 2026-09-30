package com.kalitron.studio.service.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import com.kalitron.studio.service.dto.croquis.ValidationIssueDTO;
import com.kalitron.studio.service.dto.measurement.SiteMeasurementPayloadDTO;
import com.kalitron.studio.service.impl.CroquisCatalogServiceImpl;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Runs every conformance vector (E12 #113) against the Java engine. The same
 * files are published for the KFS-APP Dart engine; both must produce the same
 * issues in the same order.
 */
class MeasurementRuleEngineVectorsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static final CroquisCatalogDTO CATALOG = new CroquisCatalogServiceImpl(
        new ClassPathResource("croquis/catalog.json")
    ).getCatalog();

    static Stream<Resource> vectors() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(
            "classpath:site-measurement/validation-vectors/*.json"
        );
        assertThat(resources).isNotEmpty();
        return Arrays.stream(resources).sorted(Comparator.comparing(Resource::getFilename));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("vectors")
    void vector(Resource vectorFile) throws IOException {
        JsonNode vector;
        try (InputStream in = vectorFile.getInputStream()) {
            vector = MAPPER.readTree(in);
        }
        Map<String, BigDecimal> params = new LinkedHashMap<>();
        vector
            .get("params")
            .properties()
            .forEach(entry -> params.put(entry.getKey(), entry.getValue().decimalValue()));
        SiteMeasurementPayloadDTO input = MAPPER.treeToValue(vector.get("input"), SiteMeasurementPayloadDTO.class);

        List<ValidationIssueDTO> actual = new MeasurementRuleEngine(CATALOG, params).validate(input);

        List<Map<String, Object>> expected = new ArrayList<>();
        vector.get("expectedIssues").forEach(node -> expected.add(project(MAPPER.convertValue(node, Map.class))));
        List<Map<String, Object>> projected = actual
            .stream()
            .map(issue -> project(MAPPER.convertValue(issue, Map.class)))
            .toList();

        assertThat(projected).as(vector.get("description").asText()).containsExactlyElementsOf(expected);
        assertThat(actual).allSatisfy(issue -> assertThat(issue.message()).doesNotContain("{"));
    }

    /** Fields compared by the vectors (messages are language-specific). */
    private static Map<String, Object> project(Map<?, ?> issue) {
        Map<String, Object> projection = new LinkedHashMap<>();
        for (String key : List.of(
            "ruleSet",
            "code",
            "severity",
            "scope",
            "wallCode",
            "runCode",
            "cornerCode",
            "elementUuid",
            "itemUuid",
            "field"
        )) {
            projection.put(key, issue.get(key));
        }
        return projection;
    }
}
