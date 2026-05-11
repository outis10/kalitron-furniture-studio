package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.DesignArtifactTestSamples.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static com.kalitron.studio.domain.GenerationJobTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenerationJobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenerationJob.class);
        GenerationJob generationJob1 = getGenerationJobSample1();
        GenerationJob generationJob2 = new GenerationJob();
        assertThat(generationJob1).isNotEqualTo(generationJob2);

        generationJob2.setId(generationJob1.getId());
        assertThat(generationJob1).isEqualTo(generationJob2);

        generationJob2 = getGenerationJobSample2();
        assertThat(generationJob1).isNotEqualTo(generationJob2);
    }

    @Test
    void artifactTest() {
        GenerationJob generationJob = getGenerationJobRandomSampleGenerator();
        DesignArtifact designArtifactBack = getDesignArtifactRandomSampleGenerator();

        generationJob.setArtifact(designArtifactBack);
        assertThat(generationJob.getArtifact()).isEqualTo(designArtifactBack);

        generationJob.artifact(null);
        assertThat(generationJob.getArtifact()).isNull();
    }

    @Test
    void sessionTest() {
        GenerationJob generationJob = getGenerationJobRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        generationJob.setSession(designSessionBack);
        assertThat(generationJob.getSession()).isEqualTo(designSessionBack);

        generationJob.session(null);
        assertThat(generationJob.getSession()).isNull();
    }
}
