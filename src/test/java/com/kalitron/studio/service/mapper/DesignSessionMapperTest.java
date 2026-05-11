package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.DesignSessionAsserts.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DesignSessionMapperTest {

    private DesignSessionMapper designSessionMapper;

    @BeforeEach
    void setUp() {
        designSessionMapper = new DesignSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDesignSessionSample1();
        var actual = designSessionMapper.toEntity(designSessionMapper.toDto(expected));
        assertDesignSessionAllPropertiesEquals(expected, actual);
    }
}
