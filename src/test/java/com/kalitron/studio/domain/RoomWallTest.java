package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static com.kalitron.studio.domain.RoomWallTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoomWallTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoomWall.class);
        RoomWall roomWall1 = getRoomWallSample1();
        RoomWall roomWall2 = new RoomWall();
        assertThat(roomWall1).isNotEqualTo(roomWall2);

        roomWall2.setId(roomWall1.getId());
        assertThat(roomWall1).isEqualTo(roomWall2);

        roomWall2 = getRoomWallSample2();
        assertThat(roomWall1).isNotEqualTo(roomWall2);
    }

    @Test
    void sessionTest() {
        RoomWall roomWall = getRoomWallRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        roomWall.setSession(designSessionBack);
        assertThat(roomWall.getSession()).isEqualTo(designSessionBack);

        roomWall.session(null);
        assertThat(roomWall.getSession()).isNull();
    }
}
