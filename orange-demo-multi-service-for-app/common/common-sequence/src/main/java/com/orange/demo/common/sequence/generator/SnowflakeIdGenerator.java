package com.orange.demo.common.sequence.generator;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.orange.demo.common.core.util.IpUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Snowflake Id生成器。该实现完全copy美团的leaf。
 *
 * @author MeiTuan.Team
 * @date 2020-08-08
 */
@Slf4j
public class SnowflakeIdGenerator implements MyIdGenerator {
    private static final long TWEPOCH = 1288834974657L;
    private static final long WORKER_ID_BITS = 10L;
    /**
     * 最大能够分配的workerid =1023
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long SEQUENCE_BITS = 12L;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static final Random RANDOM = RandomUtil.getRandom();

    /**
     * @param zkAddress zk地址
     * @param idPort    用于识别相同ip内不同服务的端口号。仅作为标识用，不会对该端口进行监听。
     */
    public SnowflakeIdGenerator(String zkAddress, int idPort, String zkPath) {
        Preconditions.checkArgument(
                timeGen() > TWEPOCH, "Snowflake not support twepoch greater than currentTime");
        final String ip = IpUtil.getFirstLocalIpAddress();
        SnowflakeZookeeperHolder holder =
                new SnowflakeZookeeperHolder(ip, String.valueOf(idPort), zkAddress, zkPath);
        log.info("twepoch:{} ,ip:{} ,zkAddress:{} port:{}", TWEPOCH, ip, zkAddress, idPort);
        boolean initFlag = holder.init();
        Preconditions.checkArgument(initFlag, "Snowflake Id Gen is not init ok");
        workerId = holder.getWorkerId();
        log.info("START SUCCESS USE ZK WORKERID-{}", workerId);
        Preconditions.checkArgument(
                workerId >= 0 && workerId <= MAX_WORKER_ID, "WorkerId must (>= 0 and <=> 1023");
    }

    /**
     * 获取字符型分布式Id。
     *
     * @return 生成后的Id。
     */
    @Override
    public synchronized String nextStringId() {
        return String.valueOf(this.nextLongId());
    }

    /**
     * 获取数值型分布式Id。
     *
     * @return 生成后的Id。
     */
    @Override
    public synchronized long nextLongId() {
        long timestamp = timeGen();
        int maxGap = 10;
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= maxGap) {
                if (!ThreadUtil.sleep(offset << 1)) {
                    log.error("Thread is interrupted while synchronizing to LastTimeStamp.");
                    throw new SnowflakeGenerateException(
                            "Thread is interrupted while synchronizing to LastTimeStamp..");
                }
                timestamp = timeGen();
                if (timestamp < lastTimestamp) {
                    log.error("CurrentTime is less than LastTimeStamp too much (> 10ms) after synchronized.");
                    throw new SnowflakeGenerateException(
                            "CurrentTime is less than LastTimeStamp too much (> 10ms) after synchronized.");
                }
            } else {
                log.error("CurrentTime is less than LastTimeStamp too much (> 10ms).");
                throw new SnowflakeGenerateException(
                        "CurrentTime is less than LastTimeStamp too much (> 10ms).");
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) | (workerId << SEQUENCE_BITS) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static class SnowflakeGenerateException extends RuntimeException {
        public SnowflakeGenerateException(String msg, Throwable e) {
            super(msg, e);
        }
        public SnowflakeGenerateException(String msg) {
            super(msg);
        }
    }
}
