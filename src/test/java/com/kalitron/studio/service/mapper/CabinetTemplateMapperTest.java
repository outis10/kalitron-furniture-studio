package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.CabinetTemplateAsserts.*;
import static com.kalitron.studio.domain.CabinetTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CabinetTemplateMapperTest {

    private CabinetTemplateMapper cabinetTemplateMapper;

    @BeforeEach
    void setUp() {
        cabinetTemplateMapper = new CabinetTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCabinetTemplateSample1();
        var actual = cabinetTemplateMapper.toEntity(cabinetTemplateMapper.toDto(expected));
        assertCabinetTemplateAllPropertiesEquals(expected, actual);
    }
}
