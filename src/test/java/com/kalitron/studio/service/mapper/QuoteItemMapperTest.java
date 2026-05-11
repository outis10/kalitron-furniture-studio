package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.QuoteItemAsserts.*;
import static com.kalitron.studio.domain.QuoteItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteItemMapperTest {

    private QuoteItemMapper quoteItemMapper;

    @BeforeEach
    void setUp() {
        quoteItemMapper = new QuoteItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuoteItemSample1();
        var actual = quoteItemMapper.toEntity(quoteItemMapper.toDto(expected));
        assertQuoteItemAllPropertiesEquals(expected, actual);
    }
}
