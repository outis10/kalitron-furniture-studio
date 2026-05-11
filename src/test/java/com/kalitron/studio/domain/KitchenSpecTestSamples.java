package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class KitchenSpecTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static KitchenSpec getKitchenSpecSample1() {
        return new KitchenSpec()
            .id(1L)
            .totalWidthMm(1)
            .totalHeightMm(1)
            .totalDepthMm(1)
            .style("style1")
            .handleType("handleType1")
            .countertopMaterial("countertopMaterial1")
            .sinkPosition("sinkPosition1");
    }

    public static KitchenSpec getKitchenSpecSample2() {
        return new KitchenSpec()
            .id(2L)
            .totalWidthMm(2)
            .totalHeightMm(2)
            .totalDepthMm(2)
            .style("style2")
            .handleType("handleType2")
            .countertopMaterial("countertopMaterial2")
            .sinkPosition("sinkPosition2");
    }

    public static KitchenSpec getKitchenSpecRandomSampleGenerator() {
        return new KitchenSpec()
            .id(longCount.incrementAndGet())
            .totalWidthMm(intCount.incrementAndGet())
            .totalHeightMm(intCount.incrementAndGet())
            .totalDepthMm(intCount.incrementAndGet())
            .style(UUID.randomUUID().toString())
            .handleType(UUID.randomUUID().toString())
            .countertopMaterial(UUID.randomUUID().toString())
            .sinkPosition(UUID.randomUUID().toString());
    }
}
