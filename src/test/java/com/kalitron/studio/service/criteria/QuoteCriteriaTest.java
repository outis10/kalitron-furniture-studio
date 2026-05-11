package com.kalitron.studio.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class QuoteCriteriaTest {

    @Test
    void newQuoteCriteriaHasAllFiltersNullTest() {
        var quoteCriteria = new QuoteCriteria();
        assertThat(quoteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void quoteCriteriaFluentMethodsCreatesFiltersTest() {
        var quoteCriteria = new QuoteCriteria();

        setAllFilters(quoteCriteria);

        assertThat(quoteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void quoteCriteriaCopyCreatesNullFilterTest() {
        var quoteCriteria = new QuoteCriteria();
        var copy = quoteCriteria.copy();

        assertThat(quoteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(quoteCriteria)
        );
    }

    @Test
    void quoteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var quoteCriteria = new QuoteCriteria();
        setAllFilters(quoteCriteria);

        var copy = quoteCriteria.copy();

        assertThat(quoteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(quoteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var quoteCriteria = new QuoteCriteria();

        assertThat(quoteCriteria).hasToString("QuoteCriteria{}");
    }

    private static void setAllFilters(QuoteCriteria quoteCriteria) {
        quoteCriteria.id();
        quoteCriteria.quoteNumber();
        quoteCriteria.status();
        quoteCriteria.subtotalMxn();
        quoteCriteria.taxMxn();
        quoteCriteria.totalMxn();
        quoteCriteria.laborMxn();
        quoteCriteria.validUntil();
        quoteCriteria.publicToken();
        quoteCriteria.notes();
        quoteCriteria.createdAt();
        quoteCriteria.sentAt();
        quoteCriteria.itemsId();
        quoteCriteria.renderImageId();
        quoteCriteria.pdfArtifactId();
        quoteCriteria.sessionId();
        quoteCriteria.distinct();
    }

    private static Condition<QuoteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuoteNumber()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getSubtotalMxn()) &&
                condition.apply(criteria.getTaxMxn()) &&
                condition.apply(criteria.getTotalMxn()) &&
                condition.apply(criteria.getLaborMxn()) &&
                condition.apply(criteria.getValidUntil()) &&
                condition.apply(criteria.getPublicToken()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getSentAt()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getRenderImageId()) &&
                condition.apply(criteria.getPdfArtifactId()) &&
                condition.apply(criteria.getSessionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<QuoteCriteria> copyFiltersAre(QuoteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuoteNumber(), copy.getQuoteNumber()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getSubtotalMxn(), copy.getSubtotalMxn()) &&
                condition.apply(criteria.getTaxMxn(), copy.getTaxMxn()) &&
                condition.apply(criteria.getTotalMxn(), copy.getTotalMxn()) &&
                condition.apply(criteria.getLaborMxn(), copy.getLaborMxn()) &&
                condition.apply(criteria.getValidUntil(), copy.getValidUntil()) &&
                condition.apply(criteria.getPublicToken(), copy.getPublicToken()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getSentAt(), copy.getSentAt()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getRenderImageId(), copy.getRenderImageId()) &&
                condition.apply(criteria.getPdfArtifactId(), copy.getPdfArtifactId()) &&
                condition.apply(criteria.getSessionId(), copy.getSessionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
