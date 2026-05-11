package com.kalitron.studio.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Caffeine caffeine = jHipsterProperties.getCache().getCaffeine();

        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(caffeine.getMaxEntries()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.getTimeToLiveSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        jcacheConfiguration = caffeineConfiguration;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.kalitron.studio.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.kalitron.studio.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.kalitron.studio.domain.User.class.getName());
            createCache(cm, com.kalitron.studio.domain.Authority.class.getName());
            createCache(cm, com.kalitron.studio.domain.User.class.getName() + ".authorities");
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName());
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName() + ".messageses");
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName() + ".imageses");
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName() + ".artifactses");
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName() + ".jobses");
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName() + ".quoteses");
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName() + ".wallses");
            createCache(cm, com.kalitron.studio.domain.DesignSession.class.getName() + ".obstacleses");
            createCache(cm, com.kalitron.studio.domain.ChatMessage.class.getName());
            createCache(cm, com.kalitron.studio.domain.KitchenSpec.class.getName());
            createCache(cm, com.kalitron.studio.domain.KitchenSpec.class.getName() + ".cabinetses");
            createCache(cm, com.kalitron.studio.domain.RoomWall.class.getName());
            createCache(cm, com.kalitron.studio.domain.RoomObstacle.class.getName());
            createCache(cm, com.kalitron.studio.domain.DesignImage.class.getName());
            createCache(cm, com.kalitron.studio.domain.DesignArtifact.class.getName());
            createCache(cm, com.kalitron.studio.domain.GenerationJob.class.getName());
            createCache(cm, com.kalitron.studio.domain.CatalogStyle.class.getName());
            createCache(cm, com.kalitron.studio.domain.Material.class.getName());
            createCache(cm, com.kalitron.studio.domain.Hardware.class.getName());
            createCache(cm, com.kalitron.studio.domain.CabinetTemplate.class.getName());
            createCache(cm, com.kalitron.studio.domain.Cabinet.class.getName());
            createCache(cm, com.kalitron.studio.domain.Cabinet.class.getName() + ".partses");
            createCache(cm, com.kalitron.studio.domain.CabinetPart.class.getName());
            createCache(cm, com.kalitron.studio.domain.Quote.class.getName());
            createCache(cm, com.kalitron.studio.domain.Quote.class.getName() + ".itemses");
            createCache(cm, com.kalitron.studio.domain.QuoteItem.class.getName());
            // jhipster-needle-caffeine-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
