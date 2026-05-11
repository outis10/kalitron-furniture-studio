package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HardwareTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Hardware getHardwareSample1() {
        return new Hardware().id(1L).code("code1").name("name1").supplierName("supplierName1").notes("notes1");
    }

    public static Hardware getHardwareSample2() {
        return new Hardware().id(2L).code("code2").name("name2").supplierName("supplierName2").notes("notes2");
    }

    public static Hardware getHardwareRandomSampleGenerator() {
        return new Hardware()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .supplierName(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
