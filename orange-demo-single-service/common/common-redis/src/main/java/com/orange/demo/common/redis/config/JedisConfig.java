package com.orange.demo.common.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Configuration
@ConditionalOnProperty(name = "redis.jedis.enabled", havingValue = "true")
public class JedisConfig {

    @Value("${redis.jedis.port}")
    private Integer port;

    @Value("${redis.jedis.host}")
    private String redisHost;

    @Value("${redis.jedis.timeout}")
    private int timeout;

    @Value("${redis.jedis.pool.maxTotal}")
    private Integer maxTotal;

    @Value("${redis.jedis.pool.maxIdle}")
    private Integer maxIdle;

    @Value("${redis.jedis.pool.minIdle}")
    private Integer minIdle;

    @Value("${redis.jedis.pool.maxWait}")
    private Integer maxWait;

    @Bean
    public JedisPool getJedisPool() {
        // Jedis配置信息
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setEvictorShutdownTimeoutMillis(2000);
        return new JedisPool(jedisPoolConfig, redisHost, port);
    }
}
