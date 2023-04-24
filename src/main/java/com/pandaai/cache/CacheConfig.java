package com.pandaai.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Resource(name = "expire5MinutesCaffeine")
    private Caffeine<Object, Object> expire5MinutesCaffeine;

    @Resource(name = "expire10MinutesCaffeine")
    private Caffeine<Object, Object> expire10MinutesCaffeine;

    @Resource(name = "expire30MinutesCaffeine")
    private Caffeine<Object, Object> expire30MinutesCaffeine;

    @Resource(name = "expire1HourCaffeine")
    private Caffeine<Object, Object> expire1HourCaffeine;

    @Resource(name = "expire2HoursCaffeine")
    private Caffeine<Object, Object> expire2HoursCaffeine;

    @Primary
    @Bean
    public Cache<String, Object> cache() {
        return expire10MinutesCaffeine.build();
    }

    @Primary
    @Bean
    @Override
    public CacheManager cacheManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager();
        cm.setCaffeine(expire10MinutesCaffeine);
        return cm;
    }

    @Bean("expire5mManager")
    public CacheManager cache5mManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager();
        cm.setCaffeine(expire5MinutesCaffeine);
        return cm;
    }

    @Bean("expire30mManager")
    public CacheManager cache30mManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager();
        cm.setCaffeine(expire30MinutesCaffeine);
        return cm;
    }

    @Bean("expire1hManager")
    public CacheManager cache1hManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager();
        cm.setCaffeine(expire1HourCaffeine);
        return cm;
    }

    @Bean("expire2hManager")
    public CacheManager cache2hManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager();
        cm.setCaffeine(expire2HoursCaffeine);
        return cm;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName()).append(".");
            sb.append(method.getName()).append(".");
            for (Object obj : objects) {
                if (obj == null) continue;
                sb.append(obj.toString());
            }
            logger.info("keyGenerator: " + sb);
            return sb.toString();
        };
    }

}
