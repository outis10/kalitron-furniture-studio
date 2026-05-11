package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.RoomWallAsserts.*;
import static com.kalitron.studio.domain.RoomWallTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomWallMapperTest {

    private RoomWallMapper roomWallMapper;

    @BeforeEach
    void setUp() {
        roomWallMapper = new RoomWallMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRoomWallSample1();
        var actual = roomWallMapper.toEntity(roomWallMapper.toDto(expected));
        assertRoomWallAllPropertiesEquals(expected, actual);
    }
}
