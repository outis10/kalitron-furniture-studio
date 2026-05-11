package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CabinetCriteriaTest {

    @Test
    void newCabinetCriteriaHasAllFiltersNullTest() {
        var cabinetCriteria = new CabinetCriteria();
        assertThat(cabinetCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cabinetCriteriaFluentMethodsCreatesFiltersTest() {
        var cabinetCriteria = new CabinetCriteria();

        setAllFilters(cabinetCriteria);

        assertThat(cabinetCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cabinetCriteriaCopyCreatesNullFilterTest() {
        var cabinetCriteria = new CabinetCriteria();
        var copy = cabinetCriteria.copy();

        assertThat(cabinetCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cabinetCriteria)
        );
    }

    @Test
    void cabinetCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cabinetCriteria = new CabinetCriteria();
        setAllFilters(cabinetCriteria);

        var copy = cabinetCriteria.copy();

        assertThat(cabinetCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cabinetCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cabinetCriteria = new CabinetCriteria();

        assertThat(cabinetCriteria).hasToString("CabinetCriteria{}");
    }

    private static void setAllFilters(CabinetCriteria cabinetCriteria) {
        cabinetCriteria.id();
        cabinetCriteria.cabinetCode();
        cabinetCriteria.category();
        cabinetCriteria.label();
        cabinetCriteria.widthMm();
        cabinetCriteria.heightMm();
        cabinetCriteria.depthMm();
        cabinetCriteria.doors();
        cabinetCriteria.drawers();
        cabinetCriteria.shelves();
        cabinetCriteria.finish();
        cabinetCriteria.positionX();
        cabinetCriteria.positionY();
        cabinetCriteria.positionZ();
        cabinetCriteria.rotationDeg();
        cabinetCriteria.positionSeq();
        cabinetCriteria.notes();
        cabinetCriteria.partsId();
        cabinetCriteria.templateId();
        cabinetCriteria.materialId();
        cabinetCriteria.specId();
        cabinetCriteria.distinct();
    }

    private static Condition<CabinetCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCabinetCode()) &&
                condition.apply(criteria.getCategory()) &&
                condition.apply(criteria.getLabel()) &&
                condition.apply(criteria.getWidthMm()) &&
                condition.apply(criteria.getHeightMm()) &&
                condition.apply(criteria.getDepthMm()) &&
                condition.apply(criteria.getDoors()) &&
                condition.apply(criteria.getDrawers()) &&
                condition.apply(criteria.getShelves()) &&
                condition.apply(criteria.getFinish()) &&
                condition.apply(criteria.getPositionX()) &&
                condition.apply(criteria.getPositionY()) &&
                condition.apply(criteria.getPositionZ()) &&
                condition.apply(criteria.getRotationDeg()) &&
                condition.apply(criteria.getPositionSeq()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getPartsId()) &&
                condition.apply(criteria.getTemplateId()) &&
                condition.apply(criteria.getMaterialId()) &&
                condition.apply(criteria.getSpecId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CabinetCriteria> copyFiltersAre(CabinetCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCabinetCode(), copy.getCabinetCode()) &&
                condition.apply(criteria.getCategory(), copy.getCategory()) &&
                condition.apply(criteria.getLabel(), copy.getLabel()) &&
                condition.apply(criteria.getWidthMm(), copy.getWidthMm()) &&
                condition.apply(criteria.getHeightMm(), copy.getHeightMm()) &&
                condition.apply(criteria.getDepthMm(), copy.getDepthMm()) &&
                condition.apply(criteria.getDoors(), copy.getDoors()) &&
                condition.apply(criteria.getDrawers(), copy.getDrawers()) &&
                condition.apply(criteria.getShelves(), copy.getShelves()) &&
                condition.apply(criteria.getFinish(), copy.getFinish()) &&
                condition.apply(criteria.getPositionX(), copy.getPositionX()) &&
                condition.apply(criteria.getPositionY(), copy.getPositionY()) &&
                condition.apply(criteria.getPositionZ(), copy.getPositionZ()) &&
                condition.apply(criteria.getRotationDeg(), copy.getRotationDeg()) &&
                condition.apply(criteria.getPositionSeq(), copy.getPositionSeq()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getPartsId(), copy.getPartsId()) &&
                condition.apply(criteria.getTemplateId(), copy.getTemplateId()) &&
                condition.apply(criteria.getMaterialId(), copy.getMaterialId()) &&
                condition.apply(criteria.getSpecId(), copy.getSpecId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
