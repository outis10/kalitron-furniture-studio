package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoomWallTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RoomWall getRoomWallSample1() {
        return new RoomWall().id(1L).name("name1").lengthMm(1).heightMm(1).angleDeg(1).positionX(1).positionY(1).sortOrder(1);
    }

    public static RoomWall getRoomWallSample2() {
        return new RoomWall().id(2L).name("name2").lengthMm(2).heightMm(2).angleDeg(2).positionX(2).positionY(2).sortOrder(2);
    }

    public static RoomWall getRoomWallRandomSampleGenerator() {
        return new RoomWall()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .lengthMm(intCount.incrementAndGet())
            .heightMm(intCount.incrementAndGet())
            .angleDeg(intCount.incrementAndGet())
            .positionX(intCount.incrementAndGet())
            .positionY(intCount.incrementAndGet())
            .sortOrder(intCount.incrementAndGet());
    }
}
