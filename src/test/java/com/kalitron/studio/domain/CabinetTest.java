package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.CabinetPartTestSamples.*;
import static com.kalitron.studio.domain.CabinetTemplateTestSamples.*;
import static com.kalitron.studio.domain.CabinetTestSamples.*;
import static com.kalitron.studio.domain.KitchenSpecTestSamples.*;
import static com.kalitron.studio.domain.MaterialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CabinetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cabinet.class);
        Cabinet cabinet1 = getCabinetSample1();
        Cabinet cabinet2 = new Cabinet();
        assertThat(cabinet1).isNotEqualTo(cabinet2);

        cabinet2.setId(cabinet1.getId());
        assertThat(cabinet1).isEqualTo(cabinet2);

        cabinet2 = getCabinetSample2();
        assertThat(cabinet1).isNotEqualTo(cabinet2);
    }

    @Test
    void partsTest() {
        Cabinet cabinet = getCabinetRandomSampleGenerator();
        CabinetPart cabinetPartBack = getCabinetPartRandomSampleGenerator();

        cabinet.addParts(cabinetPartBack);
        assertThat(cabinet.getPartses()).containsOnly(cabinetPartBack);
        assertThat(cabinetPartBack.getCabinet()).isEqualTo(cabinet);

        cabinet.removeParts(cabinetPartBack);
        assertThat(cabinet.getPartses()).doesNotContain(cabinetPartBack);
        assertThat(cabinetPartBack.getCabinet()).isNull();

        cabinet.partses(new HashSet<>(Set.of(cabinetPartBack)));
        assertThat(cabinet.getPartses()).containsOnly(cabinetPartBack);
        assertThat(cabinetPartBack.getCabinet()).isEqualTo(cabinet);

        cabinet.setPartses(new HashSet<>());
        assertThat(cabinet.getPartses()).doesNotContain(cabinetPartBack);
        assertThat(cabinetPartBack.getCabinet()).isNull();
    }

    @Test
    void templateTest() {
        Cabinet cabinet = getCabinetRandomSampleGenerator();
        CabinetTemplate cabinetTemplateBack = getCabinetTemplateRandomSampleGenerator();

        cabinet.setTemplate(cabinetTemplateBack);
        assertThat(cabinet.getTemplate()).isEqualTo(cabinetTemplateBack);

        cabinet.template(null);
        assertThat(cabinet.getTemplate()).isNull();
    }

    @Test
    void materialTest() {
        Cabinet cabinet = getCabinetRandomSampleGenerator();
        Material materialBack = getMaterialRandomSampleGenerator();

        cabinet.setMaterial(materialBack);
        assertThat(cabinet.getMaterial()).isEqualTo(materialBack);

        cabinet.material(null);
        assertThat(cabinet.getMaterial()).isNull();
    }

    @Test
    void specTest() {
        Cabinet cabinet = getCabinetRandomSampleGenerator();
        KitchenSpec kitchenSpecBack = getKitchenSpecRandomSampleGenerator();

        cabinet.setSpec(kitchenSpecBack);
        assertThat(cabinet.getSpec()).isEqualTo(kitchenSpecBack);

        cabinet.spec(null);
        assertThat(cabinet.getSpec()).isNull();
    }
}
