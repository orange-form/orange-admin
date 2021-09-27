package com.flow.demo.common.redis.cache;

import com.google.common.collect.Maps;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 使用Redisson作为Redis的分布式缓存库。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Configuration
@EnableCaching
public class RedissonCacheConfig {

    private static final int DEFAULT_TTL = 3600000;

    /**
     * 定义cache名称、超时时长(毫秒)。
     */
    public enum CacheEnum {
        /**
         * session下上传文件名的缓存(时间是24小时)。
         */
        UPLOAD_FILENAME_CACHE(86400000),
        /**
         * 缺省全局缓存(时间是24小时)。
         */
        GLOBAL_CACHE(86400000);

        /**
         * 缓存的时长(单位：毫秒)
         */
        private int ttl = DEFAULT_TTL;

        CacheEnum() {
        }

        CacheEnum(int ttl) {
            this.ttl = ttl;
        }

        public int getTtl() {
            return ttl;
        }
    }

    /**
     * 初始化缓存配置。
     */
    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = Maps.newHashMap();
        for (CacheEnum c : CacheEnum.values()) {
            config.put(c.name(), new CacheConfig(c.getTtl(), 0));
        }
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
