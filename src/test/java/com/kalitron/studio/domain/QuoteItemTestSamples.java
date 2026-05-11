package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuoteItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static QuoteItem getQuoteItemSample1() {
        return new QuoteItem().id(1L).description("description1").quantity(1).category("category1").notes("notes1");
    }

    public static QuoteItem getQuoteItemSample2() {
        return new QuoteItem().id(2L).description("description2").quantity(2).category("category2").notes("notes2");
    }

    public static QuoteItem getQuoteItemRandomSampleGenerator() {
        return new QuoteItem()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .quantity(intCount.incrementAndGet())
            .category(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
