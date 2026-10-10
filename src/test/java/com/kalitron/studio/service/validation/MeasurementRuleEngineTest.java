package com.kalitron.studio.service.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import com.kalitron.studio.service.dto.croquis.RuleKind;
import com.kalitron.studio.service.dto.croquis.RuleScope;
import com.kalitron.studio.service.dto.croquis.ValidationIssueDTO;
import com.kalitron.studio.service.dto.measurement.MeasuredValueDTO;
import com.kalitron.studio.service.dto.measurement.MeasurementWallDTO;
import com.kalitron.studio.service.dto.measurement.SiteMeasurementPayloadDTO;
import com.kalitron.studio.service.dto.measurement.ValueSource;
import com.kalitron.studio.service.impl.CroquisCatalogServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class MeasurementRuleEngineTest {

    private static final CroquisCatalogDTO CATALOG = new CroquisCatalogServiceImpl(
        new ClassPathResource("croquis/catalog.json")
    ).getCatalog();

    @Test
    void paramOverridesChangeResultsWithoutCodeChanges() {
        SiteMeasurementPayloadDTO measurement = measurementWithSpread(8);

        assertThat(codes(new MeasurementRuleEngine(CATALOG).validate(measurement))).containsExactly("WALL_LENGTH_SPREAD");
        assertThat(new MeasurementRuleEngine(CATALOG, Map.of("measure.lengthSpreadMm", BigDecimal.TEN)).validate(measurement)).isEmpty();
    }

    @Test
    void rendersMessagesWithValues() {
        ValidationIssueDTO issue = new MeasurementRuleEngine(CATALOG).validate(measurementWithSpread(8)).getFirst();

        assertThat(issue.message()).isEqualTo("Muro A: las tres longitudes difieren 8 mm (máximo 5 mm).");
    }

    @Test
    void emptyMeasurementProducesNoIssues() {
        assertThat(
            new MeasurementRuleEngine(CATALOG).validate(new SiteMeasurementPayloadDTO(1, ProjectType.KITCHEN, null, null, null, null))
        ).isEmpty();
    }

    @Test
    void supportedCombinationsCoverEveryShippedRule() {
        assertThat(CATALOG.validationRules()).allSatisfy(rule ->
            assertThat(MeasurementRuleEngine.supports(rule.scope(), rule.kind())).as(rule.code()).isTrue()
        );
        assertThat(MeasurementRuleEngine.supports(RuleScope.SITE, RuleKind.NO_OVERLAP)).isFalse();
    }

    private static SiteMeasurementPayloadDTO measurementWithSpread(int spread) {
        MeasurementWallDTO wall = new MeasurementWallDTO(
            "A",
            laser(3000),
            laser(3000 + spread),
            laser(3000),
            null,
            null,
            laser(2440),
            laser(2440),
            List.of(),
            List.of("photo-1")
        );
        return new SiteMeasurementPayloadDTO(1, ProjectType.KITCHEN, "2026-09-30.1", List.of(), List.of(wall), null);
    }

    private static MeasuredValueDTO laser(int value) {
        return new MeasuredValueDTO(value, ValueSource.LASER);
    }

    private static List<String> codes(List<ValidationIssueDTO> issues) {
        return issues.stream().map(ValidationIssueDTO::code).toList();
    }
}
