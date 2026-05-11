package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CabinetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CabinetDTO.class);
        CabinetDTO cabinetDTO1 = new CabinetDTO();
        cabinetDTO1.setId(1L);
        CabinetDTO cabinetDTO2 = new CabinetDTO();
        assertThat(cabinetDTO1).isNotEqualTo(cabinetDTO2);
        cabinetDTO2.setId(cabinetDTO1.getId());
        assertThat(cabinetDTO1).isEqualTo(cabinetDTO2);
        cabinetDTO2.setId(2L);
        assertThat(cabinetDTO1).isNotEqualTo(cabinetDTO2);
        cabinetDTO1.setId(null);
        assertThat(cabinetDTO1).isNotEqualTo(cabinetDTO2);
    }
}
