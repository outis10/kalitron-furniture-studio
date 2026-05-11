package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DesignSessionCriteriaTest {

    @Test
    void newDesignSessionCriteriaHasAllFiltersNullTest() {
        var designSessionCriteria = new DesignSessionCriteria();
        assertThat(designSessionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void designSessionCriteriaFluentMethodsCreatesFiltersTest() {
        var designSessionCriteria = new DesignSessionCriteria();

        setAllFilters(designSessionCriteria);

        assertThat(designSessionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void designSessionCriteriaCopyCreatesNullFilterTest() {
        var designSessionCriteria = new DesignSessionCriteria();
        var copy = designSessionCriteria.copy();

        assertThat(designSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(designSessionCriteria)
        );
    }

    @Test
    void designSessionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var designSessionCriteria = new DesignSessionCriteria();
        setAllFilters(designSessionCriteria);

        var copy = designSessionCriteria.copy();

        assertThat(designSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(designSessionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var designSessionCriteria = new DesignSessionCriteria();

        assertThat(designSessionCriteria).hasToString("DesignSessionCriteria{}");
    }

    private static void setAllFilters(DesignSessionCriteria designSessionCriteria) {
        designSessionCriteria.id();
        designSessionCriteria.sessionCode();
        designSessionCriteria.projectType();
        designSessionCriteria.status();
        designSessionCriteria.clientName();
        designSessionCriteria.clientEmail();
        designSessionCriteria.clientPhone();
        designSessionCriteria.selectedStyle();
        designSessionCriteria.notes();
        designSessionCriteria.createdAt();
        designSessionCriteria.updatedAt();
        designSessionCriteria.specId();
        designSessionCriteria.messagesId();
        designSessionCriteria.imagesId();
        designSessionCriteria.artifactsId();
        designSessionCriteria.jobsId();
        designSessionCriteria.quotesId();
        designSessionCriteria.wallsId();
        designSessionCriteria.obstaclesId();
        designSessionCriteria.catalogStyleId();
        designSessionCriteria.distinct();
    }

    private static Condition<DesignSessionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSessionCode()) &&
                condition.apply(criteria.getProjectType()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getClientName()) &&
                condition.apply(criteria.getClientEmail()) &&
                condition.apply(criteria.getClientPhone()) &&
                condition.apply(criteria.getSelectedStyle()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getSpecId()) &&
                condition.apply(criteria.getMessagesId()) &&
                condition.apply(criteria.getImagesId()) &&
                condition.apply(criteria.getArtifactsId()) &&
                condition.apply(criteria.getJobsId()) &&
                condition.apply(criteria.getQuotesId()) &&
                condition.apply(criteria.getWallsId()) &&
                condition.apply(criteria.getObstaclesId()) &&
                condition.apply(criteria.getCatalogStyleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DesignSessionCriteria> copyFiltersAre(
        DesignSessionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSessionCode(), copy.getSessionCode()) &&
                condition.apply(criteria.getProjectType(), copy.getProjectType()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getClientName(), copy.getClientName()) &&
                condition.apply(criteria.getClientEmail(), copy.getClientEmail()) &&
                condition.apply(criteria.getClientPhone(), copy.getClientPhone()) &&
                condition.apply(criteria.getSelectedStyle(), copy.getSelectedStyle()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getSpecId(), copy.getSpecId()) &&
                condition.apply(criteria.getMessagesId(), copy.getMessagesId()) &&
                condition.apply(criteria.getImagesId(), copy.getImagesId()) &&
                condition.apply(criteria.getArtifactsId(), copy.getArtifactsId()) &&
                condition.apply(criteria.getJobsId(), copy.getJobsId()) &&
                condition.apply(criteria.getQuotesId(), copy.getQuotesId()) &&
                condition.apply(criteria.getWallsId(), copy.getWallsId()) &&
                condition.apply(criteria.getObstaclesId(), copy.getObstaclesId()) &&
                condition.apply(criteria.getCatalogStyleId(), copy.getCatalogStyleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
