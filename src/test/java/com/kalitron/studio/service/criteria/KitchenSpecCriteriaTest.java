package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class KitchenSpecCriteriaTest {

    @Test
    void newKitchenSpecCriteriaHasAllFiltersNullTest() {
        var kitchenSpecCriteria = new KitchenSpecCriteria();
        assertThat(kitchenSpecCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void kitchenSpecCriteriaFluentMethodsCreatesFiltersTest() {
        var kitchenSpecCriteria = new KitchenSpecCriteria();

        setAllFilters(kitchenSpecCriteria);

        assertThat(kitchenSpecCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void kitchenSpecCriteriaCopyCreatesNullFilterTest() {
        var kitchenSpecCriteria = new KitchenSpecCriteria();
        var copy = kitchenSpecCriteria.copy();

        assertThat(kitchenSpecCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(kitchenSpecCriteria)
        );
    }

    @Test
    void kitchenSpecCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var kitchenSpecCriteria = new KitchenSpecCriteria();
        setAllFilters(kitchenSpecCriteria);

        var copy = kitchenSpecCriteria.copy();

        assertThat(kitchenSpecCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(kitchenSpecCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var kitchenSpecCriteria = new KitchenSpecCriteria();

        assertThat(kitchenSpecCriteria).hasToString("KitchenSpecCriteria{}");
    }

    private static void setAllFilters(KitchenSpecCriteria kitchenSpecCriteria) {
        kitchenSpecCriteria.id();
        kitchenSpecCriteria.layout();
        kitchenSpecCriteria.totalWidthMm();
        kitchenSpecCriteria.totalHeightMm();
        kitchenSpecCriteria.totalDepthMm();
        kitchenSpecCriteria.style();
        kitchenSpecCriteria.primaryFinish();
        kitchenSpecCriteria.handleType();
        kitchenSpecCriteria.countertopMaterial();
        kitchenSpecCriteria.sinkPosition();
        kitchenSpecCriteria.confirmedByClient();
        kitchenSpecCriteria.confirmedAt();
        kitchenSpecCriteria.cabinetsId();
        kitchenSpecCriteria.primaryMaterialId();
        kitchenSpecCriteria.sessionId();
        kitchenSpecCriteria.distinct();
    }

    private static Condition<KitchenSpecCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLayout()) &&
                condition.apply(criteria.getTotalWidthMm()) &&
                condition.apply(criteria.getTotalHeightMm()) &&
                condition.apply(criteria.getTotalDepthMm()) &&
                condition.apply(criteria.getStyle()) &&
                condition.apply(criteria.getPrimaryFinish()) &&
                condition.apply(criteria.getHandleType()) &&
                condition.apply(criteria.getCountertopMaterial()) &&
                condition.apply(criteria.getSinkPosition()) &&
                condition.apply(criteria.getConfirmedByClient()) &&
                condition.apply(criteria.getConfirmedAt()) &&
                condition.apply(criteria.getCabinetsId()) &&
                condition.apply(criteria.getPrimaryMaterialId()) &&
                condition.apply(criteria.getSessionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<KitchenSpecCriteria> copyFiltersAre(KitchenSpecCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLayout(), copy.getLayout()) &&
                condition.apply(criteria.getTotalWidthMm(), copy.getTotalWidthMm()) &&
                condition.apply(criteria.getTotalHeightMm(), copy.getTotalHeightMm()) &&
                condition.apply(criteria.getTotalDepthMm(), copy.getTotalDepthMm()) &&
                condition.apply(criteria.getStyle(), copy.getStyle()) &&
                condition.apply(criteria.getPrimaryFinish(), copy.getPrimaryFinish()) &&
                condition.apply(criteria.getHandleType(), copy.getHandleType()) &&
                condition.apply(criteria.getCountertopMaterial(), copy.getCountertopMaterial()) &&
                condition.apply(criteria.getSinkPosition(), copy.getSinkPosition()) &&
                condition.apply(criteria.getConfirmedByClient(), copy.getConfirmedByClient()) &&
                condition.apply(criteria.getConfirmedAt(), copy.getConfirmedAt()) &&
                condition.apply(criteria.getCabinetsId(), copy.getCabinetsId()) &&
                condition.apply(criteria.getPrimaryMaterialId(), copy.getPrimaryMaterialId()) &&
                condition.apply(criteria.getSessionId(), copy.getSessionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
