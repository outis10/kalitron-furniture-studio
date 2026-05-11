package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CabinetTemplateCriteriaTest {

    @Test
    void newCabinetTemplateCriteriaHasAllFiltersNullTest() {
        var cabinetTemplateCriteria = new CabinetTemplateCriteria();
        assertThat(cabinetTemplateCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cabinetTemplateCriteriaFluentMethodsCreatesFiltersTest() {
        var cabinetTemplateCriteria = new CabinetTemplateCriteria();

        setAllFilters(cabinetTemplateCriteria);

        assertThat(cabinetTemplateCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cabinetTemplateCriteriaCopyCreatesNullFilterTest() {
        var cabinetTemplateCriteria = new CabinetTemplateCriteria();
        var copy = cabinetTemplateCriteria.copy();

        assertThat(cabinetTemplateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cabinetTemplateCriteria)
        );
    }

    @Test
    void cabinetTemplateCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cabinetTemplateCriteria = new CabinetTemplateCriteria();
        setAllFilters(cabinetTemplateCriteria);

        var copy = cabinetTemplateCriteria.copy();

        assertThat(cabinetTemplateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cabinetTemplateCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cabinetTemplateCriteria = new CabinetTemplateCriteria();

        assertThat(cabinetTemplateCriteria).hasToString("CabinetTemplateCriteria{}");
    }

    private static void setAllFilters(CabinetTemplateCriteria cabinetTemplateCriteria) {
        cabinetTemplateCriteria.id();
        cabinetTemplateCriteria.code();
        cabinetTemplateCriteria.name();
        cabinetTemplateCriteria.category();
        cabinetTemplateCriteria.defaultWidthMm();
        cabinetTemplateCriteria.defaultHeightMm();
        cabinetTemplateCriteria.defaultDepthMm();
        cabinetTemplateCriteria.minWidthMm();
        cabinetTemplateCriteria.maxWidthMm();
        cabinetTemplateCriteria.supportsDoors();
        cabinetTemplateCriteria.supportsDrawers();
        cabinetTemplateCriteria.supportsShelves();
        cabinetTemplateCriteria.fusionTemplateName();
        cabinetTemplateCriteria.isActive();
        cabinetTemplateCriteria.sortOrder();
        cabinetTemplateCriteria.distinct();
    }

    private static Condition<CabinetTemplateCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCategory()) &&
                condition.apply(criteria.getDefaultWidthMm()) &&
                condition.apply(criteria.getDefaultHeightMm()) &&
                condition.apply(criteria.getDefaultDepthMm()) &&
                condition.apply(criteria.getMinWidthMm()) &&
                condition.apply(criteria.getMaxWidthMm()) &&
                condition.apply(criteria.getSupportsDoors()) &&
                condition.apply(criteria.getSupportsDrawers()) &&
                condition.apply(criteria.getSupportsShelves()) &&
                condition.apply(criteria.getFusionTemplateName()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getSortOrder()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CabinetTemplateCriteria> copyFiltersAre(
        CabinetTemplateCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCategory(), copy.getCategory()) &&
                condition.apply(criteria.getDefaultWidthMm(), copy.getDefaultWidthMm()) &&
                condition.apply(criteria.getDefaultHeightMm(), copy.getDefaultHeightMm()) &&
                condition.apply(criteria.getDefaultDepthMm(), copy.getDefaultDepthMm()) &&
                condition.apply(criteria.getMinWidthMm(), copy.getMinWidthMm()) &&
                condition.apply(criteria.getMaxWidthMm(), copy.getMaxWidthMm()) &&
                condition.apply(criteria.getSupportsDoors(), copy.getSupportsDoors()) &&
                condition.apply(criteria.getSupportsDrawers(), copy.getSupportsDrawers()) &&
                condition.apply(criteria.getSupportsShelves(), copy.getSupportsShelves()) &&
                condition.apply(criteria.getFusionTemplateName(), copy.getFusionTemplateName()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getSortOrder(), copy.getSortOrder()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
