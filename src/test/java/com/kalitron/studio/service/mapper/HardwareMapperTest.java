package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.HardwareAsserts.*;
import static com.kalitron.studio.domain.HardwareTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HardwareMapperTest {

    private HardwareMapper hardwareMapper;

    @BeforeEach
    void setUp() {
        hardwareMapper = new HardwareMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHardwareSample1();
        var actual = hardwareMapper.toEntity(hardwareMapper.toDto(expected));
        assertHardwareAllPropertiesEquals(expected, actual);
    }
}
