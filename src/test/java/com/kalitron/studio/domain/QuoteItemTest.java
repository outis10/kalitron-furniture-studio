package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.CabinetTestSamples.*;
import static com.kalitron.studio.domain.HardwareTestSamples.*;
import static com.kalitron.studio.domain.QuoteItemTestSamples.*;
import static com.kalitron.studio.domain.QuoteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuoteItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuoteItem.class);
        QuoteItem quoteItem1 = getQuoteItemSample1();
        QuoteItem quoteItem2 = new QuoteItem();
        assertThat(quoteItem1).isNotEqualTo(quoteItem2);

        quoteItem2.setId(quoteItem1.getId());
        assertThat(quoteItem1).isEqualTo(quoteItem2);

        quoteItem2 = getQuoteItemSample2();
        assertThat(quoteItem1).isNotEqualTo(quoteItem2);
    }

    @Test
    void cabinetTest() {
        QuoteItem quoteItem = getQuoteItemRandomSampleGenerator();
        Cabinet cabinetBack = getCabinetRandomSampleGenerator();

        quoteItem.setCabinet(cabinetBack);
        assertThat(quoteItem.getCabinet()).isEqualTo(cabinetBack);

        quoteItem.cabinet(null);
        assertThat(quoteItem.getCabinet()).isNull();
    }

    @Test
    void hardwareTest() {
        QuoteItem quoteItem = getQuoteItemRandomSampleGenerator();
        Hardware hardwareBack = getHardwareRandomSampleGenerator();

        quoteItem.setHardware(hardwareBack);
        assertThat(quoteItem.getHardware()).isEqualTo(hardwareBack);

        quoteItem.hardware(null);
        assertThat(quoteItem.getHardware()).isNull();
    }

    @Test
    void quoteTest() {
        QuoteItem quoteItem = getQuoteItemRandomSampleGenerator();
        Quote quoteBack = getQuoteRandomSampleGenerator();

        quoteItem.setQuote(quoteBack);
        assertThat(quoteItem.getQuote()).isEqualTo(quoteBack);

        quoteItem.quote(null);
        assertThat(quoteItem.getQuote()).isNull();
    }
}
