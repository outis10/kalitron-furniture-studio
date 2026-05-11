package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.CabinetPartTestSamples.*;
import static com.kalitron.studio.domain.CabinetTestSamples.*;
import static com.kalitron.studio.domain.MaterialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CabinetPartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CabinetPart.class);
        CabinetPart cabinetPart1 = getCabinetPartSample1();
        CabinetPart cabinetPart2 = new CabinetPart();
        assertThat(cabinetPart1).isNotEqualTo(cabinetPart2);

        cabinetPart2.setId(cabinetPart1.getId());
        assertThat(cabinetPart1).isEqualTo(cabinetPart2);

        cabinetPart2 = getCabinetPartSample2();
        assertThat(cabinetPart1).isNotEqualTo(cabinetPart2);
    }

    @Test
    void materialTest() {
        CabinetPart cabinetPart = getCabinetPartRandomSampleGenerator();
        Material materialBack = getMaterialRandomSampleGenerator();

        cabinetPart.setMaterial(materialBack);
        assertThat(cabinetPart.getMaterial()).isEqualTo(materialBack);

        cabinetPart.material(null);
        assertThat(cabinetPart.getMaterial()).isNull();
    }

    @Test
    void cabinetTest() {
        CabinetPart cabinetPart = getCabinetPartRandomSampleGenerator();
        Cabinet cabinetBack = getCabinetRandomSampleGenerator();

        cabinetPart.setCabinet(cabinetBack);
        assertThat(cabinetPart.getCabinet()).isEqualTo(cabinetBack);

        cabinetPart.cabinet(null);
        assertThat(cabinetPart.getCabinet()).isNull();
    }
}
