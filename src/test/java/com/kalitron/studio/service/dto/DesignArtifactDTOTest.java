package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DesignArtifactDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DesignArtifactDTO.class);
        DesignArtifactDTO designArtifactDTO1 = new DesignArtifactDTO();
        designArtifactDTO1.setId(1L);
        DesignArtifactDTO designArtifactDTO2 = new DesignArtifactDTO();
        assertThat(designArtifactDTO1).isNotEqualTo(designArtifactDTO2);
        designArtifactDTO2.setId(designArtifactDTO1.getId());
        assertThat(designArtifactDTO1).isEqualTo(designArtifactDTO2);
        designArtifactDTO2.setId(2L);
        assertThat(designArtifactDTO1).isNotEqualTo(designArtifactDTO2);
        designArtifactDTO1.setId(null);
        assertThat(designArtifactDTO1).isNotEqualTo(designArtifactDTO2);
    }
}
