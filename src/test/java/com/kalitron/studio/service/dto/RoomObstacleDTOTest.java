package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoomObstacleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoomObstacleDTO.class);
        RoomObstacleDTO roomObstacleDTO1 = new RoomObstacleDTO();
        roomObstacleDTO1.setId(1L);
        RoomObstacleDTO roomObstacleDTO2 = new RoomObstacleDTO();
        assertThat(roomObstacleDTO1).isNotEqualTo(roomObstacleDTO2);
        roomObstacleDTO2.setId(roomObstacleDTO1.getId());
        assertThat(roomObstacleDTO1).isEqualTo(roomObstacleDTO2);
        roomObstacleDTO2.setId(2L);
        assertThat(roomObstacleDTO1).isNotEqualTo(roomObstacleDTO2);
        roomObstacleDTO1.setId(null);
        assertThat(roomObstacleDTO1).isNotEqualTo(roomObstacleDTO2);
    }
}
