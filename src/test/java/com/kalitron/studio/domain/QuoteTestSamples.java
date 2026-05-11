package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuoteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Quote getQuoteSample1() {
        return new Quote().id(1L).quoteNumber("quoteNumber1").publicToken("publicToken1").notes("notes1");
    }

    public static Quote getQuoteSample2() {
        return new Quote().id(2L).quoteNumber("quoteNumber2").publicToken("publicToken2").notes("notes2");
    }

    public static Quote getQuoteRandomSampleGenerator() {
        return new Quote()
            .id(longCount.incrementAndGet())
            .quoteNumber(UUID.randomUUID().toString())
            .publicToken(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
