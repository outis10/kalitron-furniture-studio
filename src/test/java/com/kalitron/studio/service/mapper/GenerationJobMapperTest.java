package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.GenerationJobAsserts.*;
import static com.kalitron.studio.domain.GenerationJobTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GenerationJobMapperTest {

    private GenerationJobMapper generationJobMapper;

    @BeforeEach
    void setUp() {
        generationJobMapper = new GenerationJobMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGenerationJobSample1();
        var actual = generationJobMapper.toEntity(generationJobMapper.toDto(expected));
        assertGenerationJobAllPropertiesEquals(expected, actual);
    }
}
