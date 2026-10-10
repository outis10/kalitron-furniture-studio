package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static com.kalitron.studio.domain.SiteMeasurementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SiteMeasurementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SiteMeasurement.class);
        SiteMeasurement siteMeasurement1 = getSiteMeasurementSample1();
        SiteMeasurement siteMeasurement2 = new SiteMeasurement();
        assertThat(siteMeasurement1).isNotEqualTo(siteMeasurement2);

        siteMeasurement2.setId(siteMeasurement1.getId());
        assertThat(siteMeasurement1).isEqualTo(siteMeasurement2);

        siteMeasurement2 = getSiteMeasurementSample2();
        assertThat(siteMeasurement1).isNotEqualTo(siteMeasurement2);
    }

    @Test
    void sessionTest() {
        SiteMeasurement siteMeasurement = getSiteMeasurementRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        siteMeasurement.setSession(designSessionBack);
        assertThat(siteMeasurement.getSession()).isEqualTo(designSessionBack);

        siteMeasurement.session(null);
        assertThat(siteMeasurement.getSession()).isNull();
    }
}
