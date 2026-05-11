package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.CabinetAsserts.*;
import static com.kalitron.studio.domain.CabinetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CabinetMapperTest {

    private CabinetMapper cabinetMapper;

    @BeforeEach
    void setUp() {
        cabinetMapper = new CabinetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCabinetSample1();
        var actual = cabinetMapper.toEntity(cabinetMapper.toDto(expected));
        assertCabinetAllPropertiesEquals(expected, actual);
    }
}
