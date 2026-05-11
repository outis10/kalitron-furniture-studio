package com.kalitron.studio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DesignSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static DesignSession getDesignSessionSample1() {
        return new DesignSession()
            .id(1L)
            .sessionCode("sessionCode1")
            .clientName("clientName1")
            .clientEmail("clientEmail1")
            .clientPhone("clientPhone1")
            .selectedStyle("selectedStyle1")
            .notes("notes1");
    }

    public static DesignSession getDesignSessionSample2() {
        return new DesignSession()
            .id(2L)
            .sessionCode("sessionCode2")
            .clientName("clientName2")
            .clientEmail("clientEmail2")
            .clientPhone("clientPhone2")
            .selectedStyle("selectedStyle2")
            .notes("notes2");
    }

    public static DesignSession getDesignSessionRandomSampleGenerator() {
        return new DesignSession()
            .id(longCount.incrementAndGet())
            .sessionCode(UUID.randomUUID().toString())
            .clientName(UUID.randomUUID().toString())
            .clientEmail(UUID.randomUUID().toString())
            .clientPhone(UUID.randomUUID().toString())
            .selectedStyle(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
