package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CabinetPartDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CabinetPartDTO.class);
        CabinetPartDTO cabinetPartDTO1 = new CabinetPartDTO();
        cabinetPartDTO1.setId(1L);
        CabinetPartDTO cabinetPartDTO2 = new CabinetPartDTO();
        assertThat(cabinetPartDTO1).isNotEqualTo(cabinetPartDTO2);
        cabinetPartDTO2.setId(cabinetPartDTO1.getId());
        assertThat(cabinetPartDTO1).isEqualTo(cabinetPartDTO2);
        cabinetPartDTO2.setId(2L);
        assertThat(cabinetPartDTO1).isNotEqualTo(cabinetPartDTO2);
        cabinetPartDTO1.setId(null);
        assertThat(cabinetPartDTO1).isNotEqualTo(cabinetPartDTO2);
    }
}
