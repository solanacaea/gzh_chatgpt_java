package com.pandaai.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineConfig.class);

    @Bean("expire5MinutesCaffeine")
    public Caffeine<Object, Object> expire5MinutesCaffeine() {
        return Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(10000L)
                .expireAfterWrite(5L, TimeUnit.MINUTES)
                .removalListener((k, v, c) -> logger.info("key {} remove cause: {}", k, c))
                .recordStats();
    }

    @Bean("expire10MinutesCaffeine")
    public Caffeine<Object, Object> expire10MinutesCaffeine() {
        return Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(10000L)
                .expireAfterWrite(10L, TimeUnit.MINUTES)
                .removalListener((k, v, c) -> logger.info("key {} remove cause: {}", k, c))
                .recordStats();
    }

    @Bean("expire30MinutesCaffeine")
    public Caffeine<Object, Object> expire30MinutesCaffeine() {
        return Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(10000L)
                .expireAfterWrite(29L, TimeUnit.MINUTES)
                .removalListener((k, v, c) -> logger.info("key {} remove cause: {}", k, c))
                .recordStats();
    }

    @Bean("expire1HourCaffeine")
    public Caffeine<Object, Object> expire1HourCaffeine() {
        return Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(10000L)
                .expireAfterWrite(59L, TimeUnit.MINUTES)
                .removalListener((k, v, c) -> logger.info("key {} remove cause: {}", k, c))
                .recordStats();
    }

    @Bean("expire2HoursCaffeine")
    public Caffeine<Object, Object> expire2HoursCaffeine() {
        return Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(10000L)
                .expireAfterWrite(119L, TimeUnit.MINUTES)
                .removalListener((k, v, c) -> logger.info("key {} remove cause: {}", k, c))
                .recordStats();
    }

}
