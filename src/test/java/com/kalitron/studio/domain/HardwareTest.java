package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.HardwareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HardwareTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hardware.class);
        Hardware hardware1 = getHardwareSample1();
        Hardware hardware2 = new Hardware();
        assertThat(hardware1).isNotEqualTo(hardware2);

        hardware2.setId(hardware1.getId());
        assertThat(hardware1).isEqualTo(hardware2);

        hardware2 = getHardwareSample2();
        assertThat(hardware1).isNotEqualTo(hardware2);
    }
}
