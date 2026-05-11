package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CatalogStyleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CatalogStyle getCatalogStyleSample1() {
        return new CatalogStyle()
            .id(1L)
            .name("name1")
            .description("description1")
            .thumbnailPath("thumbnailPath1")
            .style("style1")
            .priceRange("priceRange1")
            .sortOrder(1);
    }

    public static CatalogStyle getCatalogStyleSample2() {
        return new CatalogStyle()
            .id(2L)
            .name("name2")
            .description("description2")
            .thumbnailPath("thumbnailPath2")
            .style("style2")
            .priceRange("priceRange2")
            .sortOrder(2);
    }

    public static CatalogStyle getCatalogStyleRandomSampleGenerator() {
        return new CatalogStyle()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .thumbnailPath(UUID.randomUUID().toString())
            .style(UUID.randomUUID().toString())
            .priceRange(UUID.randomUUID().toString())
            .sortOrder(intCount.incrementAndGet());
    }
}
