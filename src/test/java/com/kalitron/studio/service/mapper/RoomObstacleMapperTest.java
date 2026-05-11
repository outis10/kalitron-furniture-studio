package com.kalitron.studio.service.mapper;

import static com.kalitron.studio.domain.RoomObstacleAsserts.*;
import static com.kalitron.studio.domain.RoomObstacleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomObstacleMapperTest {

    private RoomObstacleMapper roomObstacleMapper;

    @BeforeEach
    void setUp() {
        roomObstacleMapper = new RoomObstacleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRoomObstacleSample1();
        var actual = roomObstacleMapper.toEntity(roomObstacleMapper.toDto(expected));
        assertRoomObstacleAllPropertiesEquals(expected, actual);
    }
}
