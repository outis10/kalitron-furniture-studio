package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.DesignImageTestSamples.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DesignImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DesignImage.class);
        DesignImage designImage1 = getDesignImageSample1();
        DesignImage designImage2 = new DesignImage();
        assertThat(designImage1).isNotEqualTo(designImage2);

        designImage2.setId(designImage1.getId());
        assertThat(designImage1).isEqualTo(designImage2);

        designImage2 = getDesignImageSample2();
        assertThat(designImage1).isNotEqualTo(designImage2);
    }

    @Test
    void sessionTest() {
        DesignImage designImage = getDesignImageRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        designImage.setSession(designSessionBack);
        assertThat(designImage.getSession()).isEqualTo(designSessionBack);

        designImage.session(null);
        assertThat(designImage.getSession()).isNull();
    }
}
