package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CatalogStyleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CatalogStyleDTO.class);
        CatalogStyleDTO catalogStyleDTO1 = new CatalogStyleDTO();
        catalogStyleDTO1.setId(1L);
        CatalogStyleDTO catalogStyleDTO2 = new CatalogStyleDTO();
        assertThat(catalogStyleDTO1).isNotEqualTo(catalogStyleDTO2);
        catalogStyleDTO2.setId(catalogStyleDTO1.getId());
        assertThat(catalogStyleDTO1).isEqualTo(catalogStyleDTO2);
        catalogStyleDTO2.setId(2L);
        assertThat(catalogStyleDTO1).isNotEqualTo(catalogStyleDTO2);
        catalogStyleDTO1.setId(null);
        assertThat(catalogStyleDTO1).isNotEqualTo(catalogStyleDTO2);
    }
}
