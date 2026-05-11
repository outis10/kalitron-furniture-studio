package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.CabinetTemplateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CabinetTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CabinetTemplate.class);
        CabinetTemplate cabinetTemplate1 = getCabinetTemplateSample1();
        CabinetTemplate cabinetTemplate2 = new CabinetTemplate();
        assertThat(cabinetTemplate1).isNotEqualTo(cabinetTemplate2);

        cabinetTemplate2.setId(cabinetTemplate1.getId());
        assertThat(cabinetTemplate1).isEqualTo(cabinetTemplate2);

        cabinetTemplate2 = getCabinetTemplateSample2();
        assertThat(cabinetTemplate1).isNotEqualTo(cabinetTemplate2);
    }
}
