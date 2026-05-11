package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CabinetTemplateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CabinetTemplate getCabinetTemplateSample1() {
        return new CabinetTemplate()
            .id(1L)
            .code("code1")
            .name("name1")
            .defaultWidthMm(1)
            .defaultHeightMm(1)
            .defaultDepthMm(1)
            .minWidthMm(1)
            .maxWidthMm(1)
            .fusionTemplateName("fusionTemplateName1")
            .sortOrder(1);
    }

    public static CabinetTemplate getCabinetTemplateSample2() {
        return new CabinetTemplate()
            .id(2L)
            .code("code2")
            .name("name2")
            .defaultWidthMm(2)
            .defaultHeightMm(2)
            .defaultDepthMm(2)
            .minWidthMm(2)
            .maxWidthMm(2)
            .fusionTemplateName("fusionTemplateName2")
            .sortOrder(2);
    }

    public static CabinetTemplate getCabinetTemplateRandomSampleGenerator() {
        return new CabinetTemplate()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .defaultWidthMm(intCount.incrementAndGet())
            .defaultHeightMm(intCount.incrementAndGet())
            .defaultDepthMm(intCount.incrementAndGet())
            .minWidthMm(intCount.incrementAndGet())
            .maxWidthMm(intCount.incrementAndGet())
            .fusionTemplateName(UUID.randomUUID().toString())
            .sortOrder(intCount.incrementAndGet());
    }
}
