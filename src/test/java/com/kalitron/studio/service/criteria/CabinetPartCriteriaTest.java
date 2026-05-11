package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CabinetPartCriteriaTest {

    @Test
    void newCabinetPartCriteriaHasAllFiltersNullTest() {
        var cabinetPartCriteria = new CabinetPartCriteria();
        assertThat(cabinetPartCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cabinetPartCriteriaFluentMethodsCreatesFiltersTest() {
        var cabinetPartCriteria = new CabinetPartCriteria();

        setAllFilters(cabinetPartCriteria);

        assertThat(cabinetPartCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cabinetPartCriteriaCopyCreatesNullFilterTest() {
        var cabinetPartCriteria = new CabinetPartCriteria();
        var copy = cabinetPartCriteria.copy();

        assertThat(cabinetPartCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cabinetPartCriteria)
        );
    }

    @Test
    void cabinetPartCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cabinetPartCriteria = new CabinetPartCriteria();
        setAllFilters(cabinetPartCriteria);

        var copy = cabinetPartCriteria.copy();

        assertThat(cabinetPartCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cabinetPartCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cabinetPartCriteria = new CabinetPartCriteria();

        assertThat(cabinetPartCriteria).hasToString("CabinetPartCriteria{}");
    }

    private static void setAllFilters(CabinetPartCriteria cabinetPartCriteria) {
        cabinetPartCriteria.id();
        cabinetPartCriteria.partCode();
        cabinetPartCriteria.name();
        cabinetPartCriteria.widthMm();
        cabinetPartCriteria.heightMm();
        cabinetPartCriteria.thicknessMm();
        cabinetPartCriteria.quantity();
        cabinetPartCriteria.edgeBanding();
        cabinetPartCriteria.grainDirection();
        cabinetPartCriteria.cncOperation();
        cabinetPartCriteria.notes();
        cabinetPartCriteria.materialId();
        cabinetPartCriteria.cabinetId();
        cabinetPartCriteria.distinct();
    }

    private static Condition<CabinetPartCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPartCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getWidthMm()) &&
                condition.apply(criteria.getHeightMm()) &&
                condition.apply(criteria.getThicknessMm()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getEdgeBanding()) &&
                condition.apply(criteria.getGrainDirection()) &&
                condition.apply(criteria.getCncOperation()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getMaterialId()) &&
                condition.apply(criteria.getCabinetId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CabinetPartCriteria> copyFiltersAre(CabinetPartCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPartCode(), copy.getPartCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getWidthMm(), copy.getWidthMm()) &&
                condition.apply(criteria.getHeightMm(), copy.getHeightMm()) &&
                condition.apply(criteria.getThicknessMm(), copy.getThicknessMm()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getEdgeBanding(), copy.getEdgeBanding()) &&
                condition.apply(criteria.getGrainDirection(), copy.getGrainDirection()) &&
                condition.apply(criteria.getCncOperation(), copy.getCncOperation()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getMaterialId(), copy.getMaterialId()) &&
                condition.apply(criteria.getCabinetId(), copy.getCabinetId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
