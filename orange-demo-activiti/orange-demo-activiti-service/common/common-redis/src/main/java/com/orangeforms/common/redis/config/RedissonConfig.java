package com.orangeforms.common.redis.config;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.orangeforms.common.core.exception.InvalidRedisModeException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Configuration
@ConditionalOnProperty(name = "redis.redisson.enabled", havingValue = "true")
public class RedissonConfig {

    @Value("${redis.redisson.lockWatchdogTimeout}")
    private Integer lockWatchdogTimeout;

    @Value("${redis.redisson.mode}")
    private String mode;

    /**
     * 仅仅用于sentinel模式。
     */
    @Value("${redis.redisson.masterName:}")
    private String masterName;

    @Value("${redis.redisson.address}")
    private String address;

    @Value("${redis.redisson.timeout}")
    private Integer timeout;

    @Value("${redis.redisson.password:}")
    private String password;

    @Value("${redis.redisson.pool.poolSize}")
    private Integer poolSize;

    @Value("${redis.redisson.pool.minIdle}")
    private Integer minIdle;

    @Bean
    public RedissonClient redissonClient() {
        if (StrUtil.isBlank(password)) {
            password = null;
        }
        Config config = new Config();
        if ("single".equals(mode)) {
            config.setLockWatchdogTimeout(lockWatchdogTimeout)
                    .useSingleServer()
                    .setPassword(password)
                    .setAddress(address)
                    .setConnectionPoolSize(poolSize)
                    .setConnectionMinimumIdleSize(minIdle)
                    .setConnectTimeout(timeout);
        } else if ("cluster".equals(mode)) {
            String[] clusterAddresses = StrUtil.splitToArray(address, ',');
            config.setLockWatchdogTimeout(lockWatchdogTimeout)
                    .useClusterServers()
                    .setPassword(password)
                    .addNodeAddress(clusterAddresses)
                    .setConnectTimeout(timeout)
                    .setMasterConnectionPoolSize(poolSize)
                    .setMasterConnectionMinimumIdleSize(minIdle);
        } else if ("sentinel".equals(mode)) {
            String[] sentinelAddresses = StrUtil.splitToArray(address, ',');
            config.setLockWatchdogTimeout(lockWatchdogTimeout)
                    .useSentinelServers()
                    .setPassword(password)
                    .setMasterName(masterName)
                    .addSentinelAddress(sentinelAddresses)
                    .setConnectTimeout(timeout)
                    .setMasterConnectionPoolSize(poolSize)
                    .setMasterConnectionMinimumIdleSize(minIdle);
        } else if ("master-slave".equals(mode)) {
            String[] masterSlaveAddresses = StrUtil.splitToArray(address, ',');
            if (masterSlaveAddresses.length == 1) {
                throw new IllegalArgumentException(
                        "redis.redisson.address MUST have multiple redis addresses for master-slave mode.");
            }
            String[] slaveAddresses = new String[masterSlaveAddresses.length - 1];
            ArrayUtil.copy(masterSlaveAddresses, 1, slaveAddresses, 0, slaveAddresses.length);
            config.setLockWatchdogTimeout(lockWatchdogTimeout)
                    .useMasterSlaveServers()
                    .setPassword(password)
                    .setMasterAddress(masterSlaveAddresses[0])
                    .addSlaveAddress(slaveAddresses)
                    .setConnectTimeout(timeout)
                    .setMasterConnectionPoolSize(poolSize)
                    .setMasterConnectionMinimumIdleSize(minIdle);
        } else {
            throw new InvalidRedisModeException(mode);
        }
        return Redisson.create(config);
    }
}
