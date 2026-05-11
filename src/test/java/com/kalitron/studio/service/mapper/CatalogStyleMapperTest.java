package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.CatalogStyleAsserts.*;
import static com.kalitron.studio.domain.CatalogStyleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogStyleMapperTest {

    private CatalogStyleMapper catalogStyleMapper;

    @BeforeEach
    void setUp() {
        catalogStyleMapper = new CatalogStyleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCatalogStyleSample1();
        var actual = catalogStyleMapper.toEntity(catalogStyleMapper.toDto(expected));
        assertCatalogStyleAllPropertiesEquals(expected, actual);
    }
}
