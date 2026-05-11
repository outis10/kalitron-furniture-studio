package com.kalitron.studio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KitchenSpecDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KitchenSpecDTO.class);
        KitchenSpecDTO kitchenSpecDTO1 = new KitchenSpecDTO();
        kitchenSpecDTO1.setId(1L);
        KitchenSpecDTO kitchenSpecDTO2 = new KitchenSpecDTO();
        assertThat(kitchenSpecDTO1).isNotEqualTo(kitchenSpecDTO2);
        kitchenSpecDTO2.setId(kitchenSpecDTO1.getId());
        assertThat(kitchenSpecDTO1).isEqualTo(kitchenSpecDTO2);
        kitchenSpecDTO2.setId(2L);
        assertThat(kitchenSpecDTO1).isNotEqualTo(kitchenSpecDTO2);
        kitchenSpecDTO1.setId(null);
        assertThat(kitchenSpecDTO1).isNotEqualTo(kitchenSpecDTO2);
    }
}
