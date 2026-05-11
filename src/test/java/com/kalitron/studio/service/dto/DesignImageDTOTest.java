package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DesignImageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DesignImageDTO.class);
        DesignImageDTO designImageDTO1 = new DesignImageDTO();
        designImageDTO1.setId(1L);
        DesignImageDTO designImageDTO2 = new DesignImageDTO();
        assertThat(designImageDTO1).isNotEqualTo(designImageDTO2);
        designImageDTO2.setId(designImageDTO1.getId());
        assertThat(designImageDTO1).isEqualTo(designImageDTO2);
        designImageDTO2.setId(2L);
        assertThat(designImageDTO1).isNotEqualTo(designImageDTO2);
        designImageDTO1.setId(null);
        assertThat(designImageDTO1).isNotEqualTo(designImageDTO2);
    }
}
