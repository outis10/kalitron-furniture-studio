package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static com.kalitron.studio.domain.RoomObstacleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoomObstacleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoomObstacle.class);
        RoomObstacle roomObstacle1 = getRoomObstacleSample1();
        RoomObstacle roomObstacle2 = new RoomObstacle();
        assertThat(roomObstacle1).isNotEqualTo(roomObstacle2);

        roomObstacle2.setId(roomObstacle1.getId());
        assertThat(roomObstacle1).isEqualTo(roomObstacle2);

        roomObstacle2 = getRoomObstacleSample2();
        assertThat(roomObstacle1).isNotEqualTo(roomObstacle2);
    }

    @Test
    void sessionTest() {
        RoomObstacle roomObstacle = getRoomObstacleRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        roomObstacle.setSession(designSessionBack);
        assertThat(roomObstacle.getSession()).isEqualTo(designSessionBack);

        roomObstacle.session(null);
        assertThat(roomObstacle.getSession()).isNull();
    }
}
