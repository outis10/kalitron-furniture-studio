package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class HardwareCriteriaTest {

    @Test
    void newHardwareCriteriaHasAllFiltersNullTest() {
        var hardwareCriteria = new HardwareCriteria();
        assertThat(hardwareCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void hardwareCriteriaFluentMethodsCreatesFiltersTest() {
        var hardwareCriteria = new HardwareCriteria();

        setAllFilters(hardwareCriteria);

        assertThat(hardwareCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void hardwareCriteriaCopyCreatesNullFilterTest() {
        var hardwareCriteria = new HardwareCriteria();
        var copy = hardwareCriteria.copy();

        assertThat(hardwareCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(hardwareCriteria)
        );
    }

    @Test
    void hardwareCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var hardwareCriteria = new HardwareCriteria();
        setAllFilters(hardwareCriteria);

        var copy = hardwareCriteria.copy();

        assertThat(hardwareCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(hardwareCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var hardwareCriteria = new HardwareCriteria();

        assertThat(hardwareCriteria).hasToString("HardwareCriteria{}");
    }

    private static void setAllFilters(HardwareCriteria hardwareCriteria) {
        hardwareCriteria.id();
        hardwareCriteria.code();
        hardwareCriteria.name();
        hardwareCriteria.hardwareType();
        hardwareCriteria.unitCostMxn();
        hardwareCriteria.supplierName();
        hardwareCriteria.isActive();
        hardwareCriteria.notes();
        hardwareCriteria.distinct();
    }

    private static Condition<HardwareCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getHardwareType()) &&
                condition.apply(criteria.getUnitCostMxn()) &&
                condition.apply(criteria.getSupplierName()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<HardwareCriteria> copyFiltersAre(HardwareCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getHardwareType(), copy.getHardwareType()) &&
                condition.apply(criteria.getUnitCostMxn(), copy.getUnitCostMxn()) &&
                condition.apply(criteria.getSupplierName(), copy.getSupplierName()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
