package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MaterialCriteriaTest {

    @Test
    void newMaterialCriteriaHasAllFiltersNullTest() {
        var materialCriteria = new MaterialCriteria();
        assertThat(materialCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void materialCriteriaFluentMethodsCreatesFiltersTest() {
        var materialCriteria = new MaterialCriteria();

        setAllFilters(materialCriteria);

        assertThat(materialCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void materialCriteriaCopyCreatesNullFilterTest() {
        var materialCriteria = new MaterialCriteria();
        var copy = materialCriteria.copy();

        assertThat(materialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(materialCriteria)
        );
    }

    @Test
    void materialCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var materialCriteria = new MaterialCriteria();
        setAllFilters(materialCriteria);

        var copy = materialCriteria.copy();

        assertThat(materialCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(materialCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var materialCriteria = new MaterialCriteria();

        assertThat(materialCriteria).hasToString("MaterialCriteria{}");
    }

    private static void setAllFilters(MaterialCriteria materialCriteria) {
        materialCriteria.id();
        materialCriteria.code();
        materialCriteria.name();
        materialCriteria.materialKind();
        materialCriteria.thicknessMm();
        materialCriteria.sheetWidthMm();
        materialCriteria.sheetHeightMm();
        materialCriteria.costPerSheetMxn();
        materialCriteria.costPerSquareMeterMxn();
        materialCriteria.supplierName();
        materialCriteria.isActive();
        materialCriteria.notes();
        materialCriteria.distinct();
    }

    private static Condition<MaterialCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getMaterialKind()) &&
                condition.apply(criteria.getThicknessMm()) &&
                condition.apply(criteria.getSheetWidthMm()) &&
                condition.apply(criteria.getSheetHeightMm()) &&
                condition.apply(criteria.getCostPerSheetMxn()) &&
                condition.apply(criteria.getCostPerSquareMeterMxn()) &&
                condition.apply(criteria.getSupplierName()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MaterialCriteria> copyFiltersAre(MaterialCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getMaterialKind(), copy.getMaterialKind()) &&
                condition.apply(criteria.getThicknessMm(), copy.getThicknessMm()) &&
                condition.apply(criteria.getSheetWidthMm(), copy.getSheetWidthMm()) &&
                condition.apply(criteria.getSheetHeightMm(), copy.getSheetHeightMm()) &&
                condition.apply(criteria.getCostPerSheetMxn(), copy.getCostPerSheetMxn()) &&
                condition.apply(criteria.getCostPerSquareMeterMxn(), copy.getCostPerSquareMeterMxn()) &&
                condition.apply(criteria.getSupplierName(), copy.getSupplierName()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
