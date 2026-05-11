package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DesignArtifactTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static DesignArtifact getDesignArtifactSample1() {
        return new DesignArtifact()
            .id(1L)
            .fileName("fileName1")
            .filePath("filePath1")
            .mimeType("mimeType1")
            .fileSizeKb(1L)
            .checksum("checksum1");
    }

    public static DesignArtifact getDesignArtifactSample2() {
        return new DesignArtifact()
            .id(2L)
            .fileName("fileName2")
            .filePath("filePath2")
            .mimeType("mimeType2")
            .fileSizeKb(2L)
            .checksum("checksum2");
    }

    public static DesignArtifact getDesignArtifactRandomSampleGenerator() {
        return new DesignArtifact()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .filePath(UUID.randomUUID().toString())
            .mimeType(UUID.randomUUID().toString())
            .fileSizeKb(longCount.incrementAndGet())
            .checksum(UUID.randomUUID().toString());
    }
}
