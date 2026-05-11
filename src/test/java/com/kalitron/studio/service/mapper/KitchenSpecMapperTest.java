package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.KitchenSpecAsserts.*;
import static com.kalitron.studio.domain.KitchenSpecTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KitchenSpecMapperTest {

    private KitchenSpecMapper kitchenSpecMapper;

    @BeforeEach
    void setUp() {
        kitchenSpecMapper = new KitchenSpecMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKitchenSpecSample1();
        var actual = kitchenSpecMapper.toEntity(kitchenSpecMapper.toDto(expected));
        assertKitchenSpecAllPropertiesEquals(expected, actual);
    }
}
