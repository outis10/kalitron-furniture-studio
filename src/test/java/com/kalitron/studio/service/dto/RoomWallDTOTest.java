package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoomWallDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoomWallDTO.class);
        RoomWallDTO roomWallDTO1 = new RoomWallDTO();
        roomWallDTO1.setId(1L);
        RoomWallDTO roomWallDTO2 = new RoomWallDTO();
        assertThat(roomWallDTO1).isNotEqualTo(roomWallDTO2);
        roomWallDTO2.setId(roomWallDTO1.getId());
        assertThat(roomWallDTO1).isEqualTo(roomWallDTO2);
        roomWallDTO2.setId(2L);
        assertThat(roomWallDTO1).isNotEqualTo(roomWallDTO2);
        roomWallDTO1.setId(null);
        assertThat(roomWallDTO1).isNotEqualTo(roomWallDTO2);
    }
}
