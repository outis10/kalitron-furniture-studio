package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuoteItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuoteItemDTO.class);
        QuoteItemDTO quoteItemDTO1 = new QuoteItemDTO();
        quoteItemDTO1.setId(1L);
        QuoteItemDTO quoteItemDTO2 = new QuoteItemDTO();
        assertThat(quoteItemDTO1).isNotEqualTo(quoteItemDTO2);
        quoteItemDTO2.setId(quoteItemDTO1.getId());
        assertThat(quoteItemDTO1).isEqualTo(quoteItemDTO2);
        quoteItemDTO2.setId(2L);
        assertThat(quoteItemDTO1).isNotEqualTo(quoteItemDTO2);
        quoteItemDTO1.setId(null);
        assertThat(quoteItemDTO1).isNotEqualTo(quoteItemDTO2);
    }
}
