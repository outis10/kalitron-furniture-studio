package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CabinetPartTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CabinetPart getCabinetPartSample1() {
        return new CabinetPart()
            .id(1L)
            .partCode("partCode1")
            .name("name1")
            .widthMm(1)
            .heightMm(1)
            .thicknessMm(1)
            .quantity(1)
            .edgeBanding("edgeBanding1")
            .grainDirection("grainDirection1")
            .cncOperation("cncOperation1")
            .notes("notes1");
    }

    public static CabinetPart getCabinetPartSample2() {
        return new CabinetPart()
            .id(2L)
            .partCode("partCode2")
            .name("name2")
            .widthMm(2)
            .heightMm(2)
            .thicknessMm(2)
            .quantity(2)
            .edgeBanding("edgeBanding2")
            .grainDirection("grainDirection2")
            .cncOperation("cncOperation2")
            .notes("notes2");
    }

    public static CabinetPart getCabinetPartRandomSampleGenerator() {
        return new CabinetPart()
            .id(longCount.incrementAndGet())
            .partCode(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .widthMm(intCount.incrementAndGet())
            .heightMm(intCount.incrementAndGet())
            .thicknessMm(intCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .edgeBanding(UUID.randomUUID().toString())
            .grainDirection(UUID.randomUUID().toString())
            .cncOperation(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
