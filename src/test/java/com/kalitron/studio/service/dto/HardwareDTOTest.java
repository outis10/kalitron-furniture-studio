package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HardwareDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HardwareDTO.class);
        HardwareDTO hardwareDTO1 = new HardwareDTO();
        hardwareDTO1.setId(1L);
        HardwareDTO hardwareDTO2 = new HardwareDTO();
        assertThat(hardwareDTO1).isNotEqualTo(hardwareDTO2);
        hardwareDTO2.setId(hardwareDTO1.getId());
        assertThat(hardwareDTO1).isEqualTo(hardwareDTO2);
        hardwareDTO2.setId(2L);
        assertThat(hardwareDTO1).isNotEqualTo(hardwareDTO2);
        hardwareDTO1.setId(null);
        assertThat(hardwareDTO1).isNotEqualTo(hardwareDTO2);
    }
}
