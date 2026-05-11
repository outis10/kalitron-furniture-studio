package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.DesignArtifactTestSamples.*;
import static com.kalitron.studio.domain.DesignImageTestSamples.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static com.kalitron.studio.domain.QuoteItemTestSamples.*;
import static com.kalitron.studio.domain.QuoteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuoteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quote.class);
        Quote quote1 = getQuoteSample1();
        Quote quote2 = new Quote();
        assertThat(quote1).isNotEqualTo(quote2);

        quote2.setId(quote1.getId());
        assertThat(quote1).isEqualTo(quote2);

        quote2 = getQuoteSample2();
        assertThat(quote1).isNotEqualTo(quote2);
    }

    @Test
    void itemsTest() {
        Quote quote = getQuoteRandomSampleGenerator();
        QuoteItem quoteItemBack = getQuoteItemRandomSampleGenerator();

        quote.addItems(quoteItemBack);
        assertThat(quote.getItemses()).containsOnly(quoteItemBack);
        assertThat(quoteItemBack.getQuote()).isEqualTo(quote);

        quote.removeItems(quoteItemBack);
        assertThat(quote.getItemses()).doesNotContain(quoteItemBack);
        assertThat(quoteItemBack.getQuote()).isNull();

        quote.itemses(new HashSet<>(Set.of(quoteItemBack)));
        assertThat(quote.getItemses()).containsOnly(quoteItemBack);
        assertThat(quoteItemBack.getQuote()).isEqualTo(quote);

        quote.setItemses(new HashSet<>());
        assertThat(quote.getItemses()).doesNotContain(quoteItemBack);
        assertThat(quoteItemBack.getQuote()).isNull();
    }

    @Test
    void renderImageTest() {
        Quote quote = getQuoteRandomSampleGenerator();
        DesignImage designImageBack = getDesignImageRandomSampleGenerator();

        quote.setRenderImage(designImageBack);
        assertThat(quote.getRenderImage()).isEqualTo(designImageBack);

        quote.renderImage(null);
        assertThat(quote.getRenderImage()).isNull();
    }

    @Test
    void pdfArtifactTest() {
        Quote quote = getQuoteRandomSampleGenerator();
        DesignArtifact designArtifactBack = getDesignArtifactRandomSampleGenerator();

        quote.setPdfArtifact(designArtifactBack);
        assertThat(quote.getPdfArtifact()).isEqualTo(designArtifactBack);

        quote.pdfArtifact(null);
        assertThat(quote.getPdfArtifact()).isNull();
    }

    @Test
    void sessionTest() {
        Quote quote = getQuoteRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        quote.setSession(designSessionBack);
        assertThat(quote.getSession()).isEqualTo(designSessionBack);

        quote.session(null);
        assertThat(quote.getSession()).isNull();
    }
}
