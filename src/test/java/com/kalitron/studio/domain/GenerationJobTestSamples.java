package com.kalitron.studio.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class GenerationJobTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static GenerationJob getGenerationJobSample1() {
        return new GenerationJob().id(1L);
    }

    public static GenerationJob getGenerationJobSample2() {
        return new GenerationJob().id(2L);
    }

    public static GenerationJob getGenerationJobRandomSampleGenerator() {
        return new GenerationJob().id(longCount.incrementAndGet());
    }
}
