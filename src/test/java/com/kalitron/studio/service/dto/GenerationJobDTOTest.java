package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenerationJobDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenerationJobDTO.class);
        GenerationJobDTO generationJobDTO1 = new GenerationJobDTO();
        generationJobDTO1.setId(1L);
        GenerationJobDTO generationJobDTO2 = new GenerationJobDTO();
        assertThat(generationJobDTO1).isNotEqualTo(generationJobDTO2);
        generationJobDTO2.setId(generationJobDTO1.getId());
        assertThat(generationJobDTO1).isEqualTo(generationJobDTO2);
        generationJobDTO2.setId(2L);
        assertThat(generationJobDTO1).isNotEqualTo(generationJobDTO2);
        generationJobDTO1.setId(null);
        assertThat(generationJobDTO1).isNotEqualTo(generationJobDTO2);
    }
}
