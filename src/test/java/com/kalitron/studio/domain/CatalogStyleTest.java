package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.CatalogStyleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CatalogStyleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CatalogStyle.class);
        CatalogStyle catalogStyle1 = getCatalogStyleSample1();
        CatalogStyle catalogStyle2 = new CatalogStyle();
        assertThat(catalogStyle1).isNotEqualTo(catalogStyle2);

        catalogStyle2.setId(catalogStyle1.getId());
        assertThat(catalogStyle1).isEqualTo(catalogStyle2);

        catalogStyle2 = getCatalogStyleSample2();
        assertThat(catalogStyle1).isNotEqualTo(catalogStyle2);
    }
}
