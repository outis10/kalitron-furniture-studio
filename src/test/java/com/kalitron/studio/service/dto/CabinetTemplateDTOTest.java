package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CabinetTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CabinetTemplateDTO.class);
        CabinetTemplateDTO cabinetTemplateDTO1 = new CabinetTemplateDTO();
        cabinetTemplateDTO1.setId(1L);
        CabinetTemplateDTO cabinetTemplateDTO2 = new CabinetTemplateDTO();
        assertThat(cabinetTemplateDTO1).isNotEqualTo(cabinetTemplateDTO2);
        cabinetTemplateDTO2.setId(cabinetTemplateDTO1.getId());
        assertThat(cabinetTemplateDTO1).isEqualTo(cabinetTemplateDTO2);
        cabinetTemplateDTO2.setId(2L);
        assertThat(cabinetTemplateDTO1).isNotEqualTo(cabinetTemplateDTO2);
        cabinetTemplateDTO1.setId(null);
        assertThat(cabinetTemplateDTO1).isNotEqualTo(cabinetTemplateDTO2);
    }
}
