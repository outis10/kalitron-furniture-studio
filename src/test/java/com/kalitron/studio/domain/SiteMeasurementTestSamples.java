package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SiteMeasurementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SiteMeasurement getSiteMeasurementSample1() {
        return new SiteMeasurement()
            .id(1L)
            .measurementUuid(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .revision(1)
            .schemaVersion(1)
            .catalogVersion("catalogVersion1")
            .payloadSha256("payloadSha2561")
            .floorOutOfLevelMm(1)
            .floorOutOfLevelNote("floorOutOfLevelNote1")
            .deviceId("deviceId1")
            .appVersion("appVersion1")
            .laserModel("laserModel1");
    }

    public static SiteMeasurement getSiteMeasurementSample2() {
        return new SiteMeasurement()
            .id(2L)
            .measurementUuid(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .revision(2)
            .schemaVersion(2)
            .catalogVersion("catalogVersion2")
            .payloadSha256("payloadSha2562")
            .floorOutOfLevelMm(2)
            .floorOutOfLevelNote("floorOutOfLevelNote2")
            .deviceId("deviceId2")
            .appVersion("appVersion2")
            .laserModel("laserModel2");
    }

    public static SiteMeasurement getSiteMeasurementRandomSampleGenerator() {
        return new SiteMeasurement()
            .id(longCount.incrementAndGet())
            .measurementUuid(UUID.randomUUID())
            .revision(intCount.incrementAndGet())
            .schemaVersion(intCount.incrementAndGet())
            .catalogVersion(UUID.randomUUID().toString())
            .payloadSha256(UUID.randomUUID().toString())
            .floorOutOfLevelMm(intCount.incrementAndGet())
            .floorOutOfLevelNote(UUID.randomUUID().toString())
            .deviceId(UUID.randomUUID().toString())
            .appVersion(UUID.randomUUID().toString())
            .laserModel(UUID.randomUUID().toString());
    }
}
