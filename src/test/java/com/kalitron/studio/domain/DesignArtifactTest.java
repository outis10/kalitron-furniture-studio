package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.DesignArtifactTestSamples.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DesignArtifactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DesignArtifact.class);
        DesignArtifact designArtifact1 = getDesignArtifactSample1();
        DesignArtifact designArtifact2 = new DesignArtifact();
        assertThat(designArtifact1).isNotEqualTo(designArtifact2);

        designArtifact2.setId(designArtifact1.getId());
        assertThat(designArtifact1).isEqualTo(designArtifact2);

        designArtifact2 = getDesignArtifactSample2();
        assertThat(designArtifact1).isNotEqualTo(designArtifact2);
    }

    @Test
    void sessionTest() {
        DesignArtifact designArtifact = getDesignArtifactRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        designArtifact.setSession(designSessionBack);
        assertThat(designArtifact.getSession()).isEqualTo(designSessionBack);

        designArtifact.session(null);
        assertThat(designArtifact.getSession()).isNull();
    }
}
