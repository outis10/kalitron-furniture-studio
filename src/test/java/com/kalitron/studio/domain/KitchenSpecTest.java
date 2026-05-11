package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.CabinetTestSamples.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static com.kalitron.studio.domain.KitchenSpecTestSamples.*;
import static com.kalitron.studio.domain.MaterialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class KitchenSpecTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KitchenSpec.class);
        KitchenSpec kitchenSpec1 = getKitchenSpecSample1();
        KitchenSpec kitchenSpec2 = new KitchenSpec();
        assertThat(kitchenSpec1).isNotEqualTo(kitchenSpec2);

        kitchenSpec2.setId(kitchenSpec1.getId());
        assertThat(kitchenSpec1).isEqualTo(kitchenSpec2);

        kitchenSpec2 = getKitchenSpecSample2();
        assertThat(kitchenSpec1).isNotEqualTo(kitchenSpec2);
    }

    @Test
    void cabinetsTest() {
        KitchenSpec kitchenSpec = getKitchenSpecRandomSampleGenerator();
        Cabinet cabinetBack = getCabinetRandomSampleGenerator();

        kitchenSpec.addCabinets(cabinetBack);
        assertThat(kitchenSpec.getCabinetses()).containsOnly(cabinetBack);
        assertThat(cabinetBack.getSpec()).isEqualTo(kitchenSpec);

        kitchenSpec.removeCabinets(cabinetBack);
        assertThat(kitchenSpec.getCabinetses()).doesNotContain(cabinetBack);
        assertThat(cabinetBack.getSpec()).isNull();

        kitchenSpec.cabinetses(new HashSet<>(Set.of(cabinetBack)));
        assertThat(kitchenSpec.getCabinetses()).containsOnly(cabinetBack);
        assertThat(cabinetBack.getSpec()).isEqualTo(kitchenSpec);

        kitchenSpec.setCabinetses(new HashSet<>());
        assertThat(kitchenSpec.getCabinetses()).doesNotContain(cabinetBack);
        assertThat(cabinetBack.getSpec()).isNull();
    }

    @Test
    void primaryMaterialTest() {
        KitchenSpec kitchenSpec = getKitchenSpecRandomSampleGenerator();
        Material materialBack = getMaterialRandomSampleGenerator();

        kitchenSpec.setPrimaryMaterial(materialBack);
        assertThat(kitchenSpec.getPrimaryMaterial()).isEqualTo(materialBack);

        kitchenSpec.primaryMaterial(null);
        assertThat(kitchenSpec.getPrimaryMaterial()).isNull();
    }

    @Test
    void sessionTest() {
        KitchenSpec kitchenSpec = getKitchenSpecRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        kitchenSpec.setSession(designSessionBack);
        assertThat(kitchenSpec.getSession()).isEqualTo(designSessionBack);
        assertThat(designSessionBack.getSpec()).isEqualTo(kitchenSpec);

        kitchenSpec.session(null);
        assertThat(kitchenSpec.getSession()).isNull();
        assertThat(designSessionBack.getSpec()).isNull();
    }
}
