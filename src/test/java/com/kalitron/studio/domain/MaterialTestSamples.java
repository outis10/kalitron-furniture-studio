package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MaterialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Material getMaterialSample1() {
        return new Material()
            .id(1L)
            .code("code1")
            .name("name1")
            .thicknessMm(1)
            .sheetWidthMm(1)
            .sheetHeightMm(1)
            .supplierName("supplierName1")
            .notes("notes1");
    }

    public static Material getMaterialSample2() {
        return new Material()
            .id(2L)
            .code("code2")
            .name("name2")
            .thicknessMm(2)
            .sheetWidthMm(2)
            .sheetHeightMm(2)
            .supplierName("supplierName2")
            .notes("notes2");
    }

    public static Material getMaterialRandomSampleGenerator() {
        return new Material()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .thicknessMm(intCount.incrementAndGet())
            .sheetWidthMm(intCount.incrementAndGet())
            .sheetHeightMm(intCount.incrementAndGet())
            .supplierName(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
