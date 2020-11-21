package com.orange.demo.common.core.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 使用Caffeine作为本地缓存库
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final int DEFAULT_MAXSIZE = 10000;
    private static final int DEFAULT_TTL = 3600;

    /**
     * 定义cache名称、超时时长秒、最大个数
     * 每个cache缺省3600秒过期，最大个数1000
     */
    public enum CacheEnum {
        /**
         * 专门存储用户权限的缓存。
         */
        USER_PERMISSION_CACHE(1800, 10000),
        /**
         * session下上传文件名的缓存(时间是24小时)。
         */
        UPLOAD_FILENAME_CACHE(86400, 20000),
        /**
         * 缺省全局缓存(时间是24小时)。
         */
        GLOBAL_CACHE(86400, 20000);

        CacheEnum() {
        }

        CacheEnum(int ttl, int maxSize) {
            this.ttl = ttl;
            this.maxSize = maxSize;
        }

        /**
         * 缓存的最大数量。
         */
        private int maxSize = DEFAULT_MAXSIZE;
        /**
         * 缓存的时长(单位：秒)
         */
        private int ttl = DEFAULT_TTL;

        public int getMaxSize() {
            return maxSize;
        }

        public int getTtl() {
            return ttl;
        }
    }

    /**
     * 初始化缓存配置。
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        // 把各个cache注册到cacheManager中，CaffeineCache实现了org.springframework.cache.Cache接口
        ArrayList<CaffeineCache> caches = new ArrayList<>();
        for (CacheEnum c : CacheEnum.values()) {
            caches.add(new CaffeineCache(c.name(),
                    Caffeine.newBuilder().recordStats()
                            .expireAfterAccess(c.getTtl(), TimeUnit.SECONDS)
                            .maximumSize(c.getMaxSize())
                            .build())
            );
        }
        manager.setCaches(caches);
        return manager;
    }
}
