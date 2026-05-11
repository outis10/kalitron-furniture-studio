package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.DesignImageAsserts.*;
import static com.kalitron.studio.domain.DesignImageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DesignImageMapperTest {

    private DesignImageMapper designImageMapper;

    @BeforeEach
    void setUp() {
        designImageMapper = new DesignImageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDesignImageSample1();
        var actual = designImageMapper.toEntity(designImageMapper.toDto(expected));
        assertDesignImageAllPropertiesEquals(expected, actual);
    }
}
