package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoomObstacleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RoomObstacle getRoomObstacleSample1() {
        return new RoomObstacle().id(1L).label("label1").xMm(1).yMm(1).zMm(1).widthMm(1).heightMm(1).depthMm(1).notes("notes1");
    }

    public static RoomObstacle getRoomObstacleSample2() {
        return new RoomObstacle().id(2L).label("label2").xMm(2).yMm(2).zMm(2).widthMm(2).heightMm(2).depthMm(2).notes("notes2");
    }

    public static RoomObstacle getRoomObstacleRandomSampleGenerator() {
        return new RoomObstacle()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .xMm(intCount.incrementAndGet())
            .yMm(intCount.incrementAndGet())
            .zMm(intCount.incrementAndGet())
            .widthMm(intCount.incrementAndGet())
            .heightMm(intCount.incrementAndGet())
            .depthMm(intCount.incrementAndGet())
            .notes(UUID.randomUUID().toString());
    }
}
