package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.DesignArtifactAsserts.*;
import static com.kalitron.studio.domain.DesignArtifactTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DesignArtifactMapperTest {

    private DesignArtifactMapper designArtifactMapper;

    @BeforeEach
    void setUp() {
        designArtifactMapper = new DesignArtifactMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDesignArtifactSample1();
        var actual = designArtifactMapper.toEntity(designArtifactMapper.toDto(expected));
        assertDesignArtifactAllPropertiesEquals(expected, actual);
    }
}
