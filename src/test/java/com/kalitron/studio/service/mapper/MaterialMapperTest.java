package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.MaterialAsserts.*;
import static com.kalitron.studio.domain.MaterialTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaterialMapperTest {

    private MaterialMapper materialMapper;

    @BeforeEach
    void setUp() {
        materialMapper = new MaterialMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMaterialSample1();
        var actual = materialMapper.toEntity(materialMapper.toDto(expected));
        assertMaterialAllPropertiesEquals(expected, actual);
    }
}
