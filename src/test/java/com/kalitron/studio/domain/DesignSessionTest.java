package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.CatalogStyleTestSamples.*;
import static com.kalitron.studio.domain.ChatMessageTestSamples.*;
import static com.kalitron.studio.domain.DesignArtifactTestSamples.*;
import static com.kalitron.studio.domain.DesignImageTestSamples.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static com.kalitron.studio.domain.GenerationJobTestSamples.*;
import static com.kalitron.studio.domain.KitchenSpecTestSamples.*;
import static com.kalitron.studio.domain.QuoteTestSamples.*;
import static com.kalitron.studio.domain.RoomObstacleTestSamples.*;
import static com.kalitron.studio.domain.RoomWallTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DesignSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DesignSession.class);
        DesignSession designSession1 = getDesignSessionSample1();
        DesignSession designSession2 = new DesignSession();
        assertThat(designSession1).isNotEqualTo(designSession2);

        designSession2.setId(designSession1.getId());
        assertThat(designSession1).isEqualTo(designSession2);

        designSession2 = getDesignSessionSample2();
        assertThat(designSession1).isNotEqualTo(designSession2);
    }

    @Test
    void specTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        KitchenSpec kitchenSpecBack = getKitchenSpecRandomSampleGenerator();

        designSession.setSpec(kitchenSpecBack);
        assertThat(designSession.getSpec()).isEqualTo(kitchenSpecBack);

        designSession.spec(null);
        assertThat(designSession.getSpec()).isNull();
    }

    @Test
    void messagesTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        ChatMessage chatMessageBack = getChatMessageRandomSampleGenerator();

        designSession.addMessages(chatMessageBack);
        assertThat(designSession.getMessageses()).containsOnly(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isEqualTo(designSession);

        designSession.removeMessages(chatMessageBack);
        assertThat(designSession.getMessageses()).doesNotContain(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isNull();

        designSession.messageses(new HashSet<>(Set.of(chatMessageBack)));
        assertThat(designSession.getMessageses()).containsOnly(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isEqualTo(designSession);

        designSession.setMessageses(new HashSet<>());
        assertThat(designSession.getMessageses()).doesNotContain(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isNull();
    }

    @Test
    void imagesTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        DesignImage designImageBack = getDesignImageRandomSampleGenerator();

        designSession.addImages(designImageBack);
        assertThat(designSession.getImageses()).containsOnly(designImageBack);
        assertThat(designImageBack.getSession()).isEqualTo(designSession);

        designSession.removeImages(designImageBack);
        assertThat(designSession.getImageses()).doesNotContain(designImageBack);
        assertThat(designImageBack.getSession()).isNull();

        designSession.imageses(new HashSet<>(Set.of(designImageBack)));
        assertThat(designSession.getImageses()).containsOnly(designImageBack);
        assertThat(designImageBack.getSession()).isEqualTo(designSession);

        designSession.setImageses(new HashSet<>());
        assertThat(designSession.getImageses()).doesNotContain(designImageBack);
        assertThat(designImageBack.getSession()).isNull();
    }

    @Test
    void artifactsTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        DesignArtifact designArtifactBack = getDesignArtifactRandomSampleGenerator();

        designSession.addArtifacts(designArtifactBack);
        assertThat(designSession.getArtifactses()).containsOnly(designArtifactBack);
        assertThat(designArtifactBack.getSession()).isEqualTo(designSession);

        designSession.removeArtifacts(designArtifactBack);
        assertThat(designSession.getArtifactses()).doesNotContain(designArtifactBack);
        assertThat(designArtifactBack.getSession()).isNull();

        designSession.artifactses(new HashSet<>(Set.of(designArtifactBack)));
        assertThat(designSession.getArtifactses()).containsOnly(designArtifactBack);
        assertThat(designArtifactBack.getSession()).isEqualTo(designSession);

        designSession.setArtifactses(new HashSet<>());
        assertThat(designSession.getArtifactses()).doesNotContain(designArtifactBack);
        assertThat(designArtifactBack.getSession()).isNull();
    }

    @Test
    void jobsTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        GenerationJob generationJobBack = getGenerationJobRandomSampleGenerator();

        designSession.addJobs(generationJobBack);
        assertThat(designSession.getJobses()).containsOnly(generationJobBack);
        assertThat(generationJobBack.getSession()).isEqualTo(designSession);

        designSession.removeJobs(generationJobBack);
        assertThat(designSession.getJobses()).doesNotContain(generationJobBack);
        assertThat(generationJobBack.getSession()).isNull();

        designSession.jobses(new HashSet<>(Set.of(generationJobBack)));
        assertThat(designSession.getJobses()).containsOnly(generationJobBack);
        assertThat(generationJobBack.getSession()).isEqualTo(designSession);

        designSession.setJobses(new HashSet<>());
        assertThat(designSession.getJobses()).doesNotContain(generationJobBack);
        assertThat(generationJobBack.getSession()).isNull();
    }

    @Test
    void quotesTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        Quote quoteBack = getQuoteRandomSampleGenerator();

        designSession.addQuotes(quoteBack);
        assertThat(designSession.getQuoteses()).containsOnly(quoteBack);
        assertThat(quoteBack.getSession()).isEqualTo(designSession);

        designSession.removeQuotes(quoteBack);
        assertThat(designSession.getQuoteses()).doesNotContain(quoteBack);
        assertThat(quoteBack.getSession()).isNull();

        designSession.quoteses(new HashSet<>(Set.of(quoteBack)));
        assertThat(designSession.getQuoteses()).containsOnly(quoteBack);
        assertThat(quoteBack.getSession()).isEqualTo(designSession);

        designSession.setQuoteses(new HashSet<>());
        assertThat(designSession.getQuoteses()).doesNotContain(quoteBack);
        assertThat(quoteBack.getSession()).isNull();
    }

    @Test
    void wallsTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        RoomWall roomWallBack = getRoomWallRandomSampleGenerator();

        designSession.addWalls(roomWallBack);
        assertThat(designSession.getWallses()).containsOnly(roomWallBack);
        assertThat(roomWallBack.getSession()).isEqualTo(designSession);

        designSession.removeWalls(roomWallBack);
        assertThat(designSession.getWallses()).doesNotContain(roomWallBack);
        assertThat(roomWallBack.getSession()).isNull();

        designSession.wallses(new HashSet<>(Set.of(roomWallBack)));
        assertThat(designSession.getWallses()).containsOnly(roomWallBack);
        assertThat(roomWallBack.getSession()).isEqualTo(designSession);

        designSession.setWallses(new HashSet<>());
        assertThat(designSession.getWallses()).doesNotContain(roomWallBack);
        assertThat(roomWallBack.getSession()).isNull();
    }

    @Test
    void obstaclesTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        RoomObstacle roomObstacleBack = getRoomObstacleRandomSampleGenerator();

        designSession.addObstacles(roomObstacleBack);
        assertThat(designSession.getObstacleses()).containsOnly(roomObstacleBack);
        assertThat(roomObstacleBack.getSession()).isEqualTo(designSession);

        designSession.removeObstacles(roomObstacleBack);
        assertThat(designSession.getObstacleses()).doesNotContain(roomObstacleBack);
        assertThat(roomObstacleBack.getSession()).isNull();

        designSession.obstacleses(new HashSet<>(Set.of(roomObstacleBack)));
        assertThat(designSession.getObstacleses()).containsOnly(roomObstacleBack);
        assertThat(roomObstacleBack.getSession()).isEqualTo(designSession);

        designSession.setObstacleses(new HashSet<>());
        assertThat(designSession.getObstacleses()).doesNotContain(roomObstacleBack);
        assertThat(roomObstacleBack.getSession()).isNull();
    }

    @Test
    void catalogStyleTest() {
        DesignSession designSession = getDesignSessionRandomSampleGenerator();
        CatalogStyle catalogStyleBack = getCatalogStyleRandomSampleGenerator();

        designSession.setCatalogStyle(catalogStyleBack);
        assertThat(designSession.getCatalogStyle()).isEqualTo(catalogStyleBack);

        designSession.catalogStyle(null);
        assertThat(designSession.getCatalogStyle()).isNull();
    }
}
