package com.orange.demo.common.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类。和Jedis一样都是Redis客户端，但是Redisson提供了更多的数据结构抽象。
 * 这里我们只是使用了Redisson的分布式锁，以及map等数据结构作为字典缓存使用。更多用法请参考其文档。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Configuration
@ConditionalOnProperty(name = "redis.redisson.enabled", havingValue = "true")
public class RedissonConfig {

    @Value("${redis.redisson.lockWatchdogTimeout}")
    private Integer lockWatchdogTimeout;

    @Value("${redis.redisson.address}")
    private String address;

    @Value("${redis.redisson.timeout}")
    private Integer timeout;

    @Value("${redis.redisson.pool.poolSize}")
    private Integer poolSize;

    @Value("${redis.redisson.pool.minIdle}")
    private Integer minIdle;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 这里config还支持其他redis集群模式，可根据实际需求更换。
        // 比如useClusterServers()/useMasterSlaveServers()等。
        config.setLockWatchdogTimeout(lockWatchdogTimeout)
                .useSingleServer()
                .setAddress("redis://" + address)
                .setConnectionPoolSize(poolSize)
                .setConnectionMinimumIdleSize(minIdle)
                .setConnectTimeout(timeout);
        return Redisson.create(config);
    }
}
