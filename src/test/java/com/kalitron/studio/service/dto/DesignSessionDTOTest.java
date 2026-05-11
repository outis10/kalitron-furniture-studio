package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DesignSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DesignSessionDTO.class);
        DesignSessionDTO designSessionDTO1 = new DesignSessionDTO();
        designSessionDTO1.setId(1L);
        DesignSessionDTO designSessionDTO2 = new DesignSessionDTO();
        assertThat(designSessionDTO1).isNotEqualTo(designSessionDTO2);
        designSessionDTO2.setId(designSessionDTO1.getId());
        assertThat(designSessionDTO1).isEqualTo(designSessionDTO2);
        designSessionDTO2.setId(2L);
        assertThat(designSessionDTO1).isNotEqualTo(designSessionDTO2);
        designSessionDTO1.setId(null);
        assertThat(designSessionDTO1).isNotEqualTo(designSessionDTO2);
    }
}
