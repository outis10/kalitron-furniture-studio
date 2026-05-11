package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CabinetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Cabinet getCabinetSample1() {
        return new Cabinet()
            .id(1L)
            .cabinetCode("cabinetCode1")
            .label("label1")
            .widthMm(1)
            .heightMm(1)
            .depthMm(1)
            .doors(1)
            .drawers(1)
            .shelves(1)
            .positionX(1)
            .positionY(1)
            .positionZ(1)
            .rotationDeg(1)
            .positionSeq(1)
            .notes("notes1");
    }

    public static Cabinet getCabinetSample2() {
        return new Cabinet()
            .id(2L)
            .cabinetCode("cabinetCode2")
            .label("label2")
            .widthMm(2)
            .heightMm(2)
            .depthMm(2)
            .doors(2)
            .drawers(2)
            .shelves(2)
            .positionX(2)
            .positionY(2)
            .positionZ(2)
            .rotationDeg(2)
            .positionSeq(2)
            .notes("notes2");
    }

    public static Cabinet getCabinetRandomSampleGenerator() {
        return new Cabinet()
            .id(longCount.incrementAndGet())
            .cabinetCode(UUID.randomUUID().toString())
            .label(UUID.randomUUID().toString())
            .widthMm(intCount.incrementAndGet())
            .heightMm(intCount.incrementAndGet())
            .depthMm(intCount.incrementAndGet())
            .doors(intCount.incrementAndGet())
            .drawers(intCount.incrementAndGet())
            .shelves(intCount.incrementAndGet())
            .positionX(intCount.incrementAndGet())
            .positionY(intCount.incrementAndGet())
            .positionZ(intCount.incrementAndGet())
            .rotationDeg(intCount.incrementAndGet())
            .positionSeq(intCount.incrementAndGet())
            .notes(UUID.randomUUID().toString());
    }
}
