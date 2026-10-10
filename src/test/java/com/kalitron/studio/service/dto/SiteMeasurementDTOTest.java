package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SiteMeasurementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SiteMeasurementDTO.class);
        SiteMeasurementDTO siteMeasurementDTO1 = new SiteMeasurementDTO();
        siteMeasurementDTO1.setId(1L);
        SiteMeasurementDTO siteMeasurementDTO2 = new SiteMeasurementDTO();
        assertThat(siteMeasurementDTO1).isNotEqualTo(siteMeasurementDTO2);
        siteMeasurementDTO2.setId(siteMeasurementDTO1.getId());
        assertThat(siteMeasurementDTO1).isEqualTo(siteMeasurementDTO2);
        siteMeasurementDTO2.setId(2L);
        assertThat(siteMeasurementDTO1).isNotEqualTo(siteMeasurementDTO2);
        siteMeasurementDTO1.setId(null);
        assertThat(siteMeasurementDTO1).isNotEqualTo(siteMeasurementDTO2);
    }
}
