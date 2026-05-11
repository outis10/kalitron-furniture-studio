package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DesignImageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static DesignImage getDesignImageSample1() {
        return new DesignImage()
            .id(1L)
            .fileName("fileName1")
            .filePath("filePath1")
            .mimeType("mimeType1")
            .fileSizeKb(1L)
            .widthPx(1)
            .heightPx(1)
            .description("description1");
    }

    public static DesignImage getDesignImageSample2() {
        return new DesignImage()
            .id(2L)
            .fileName("fileName2")
            .filePath("filePath2")
            .mimeType("mimeType2")
            .fileSizeKb(2L)
            .widthPx(2)
            .heightPx(2)
            .description("description2");
    }

    public static DesignImage getDesignImageRandomSampleGenerator() {
        return new DesignImage()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .filePath(UUID.randomUUID().toString())
            .mimeType(UUID.randomUUID().toString())
            .fileSizeKb(longCount.incrementAndGet())
            .widthPx(intCount.incrementAndGet())
            .heightPx(intCount.incrementAndGet())
            .description(UUID.randomUUID().toString());
    }
}
