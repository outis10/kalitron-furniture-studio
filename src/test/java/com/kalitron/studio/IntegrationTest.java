package com.kalitron.studio;

import com.kalitron.studio.config.AsyncSyncConfiguration;
import com.kalitron.studio.config.DatabaseTestcontainer;
import com.kalitron.studio.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        KalitronFurnitureStudioApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.kalitron.studio.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
