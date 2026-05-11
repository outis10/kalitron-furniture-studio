package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.CabinetPartAsserts.*;
import static com.kalitron.studio.domain.CabinetPartTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CabinetPartMapperTest {

    private CabinetPartMapper cabinetPartMapper;

    @BeforeEach
    void setUp() {
        cabinetPartMapper = new CabinetPartMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCabinetPartSample1();
        var actual = cabinetPartMapper.toEntity(cabinetPartMapper.toDto(expected));
        assertCabinetPartAllPropertiesEquals(expected, actual);
    }
}
