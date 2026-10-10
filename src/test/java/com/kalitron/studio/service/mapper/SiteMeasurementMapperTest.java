package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.SiteMeasurementAsserts.*;
import static com.kalitron.studio.domain.SiteMeasurementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SiteMeasurementMapperTest {

    private SiteMeasurementMapper siteMeasurementMapper;

    @BeforeEach
    void setUp() {
        siteMeasurementMapper = new SiteMeasurementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSiteMeasurementSample1();
        var actual = siteMeasurementMapper.toEntity(siteMeasurementMapper.toDto(expected));
        assertSiteMeasurementAllPropertiesEquals(expected, actual);
    }
}
