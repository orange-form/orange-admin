package com.orange.admin.common.core.exception;

/**
 * Redis缓存访问失败。比如：获取分布式数据锁超时、等待线程中断等。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public class RedisCacheAccessException  extends RuntimeException {

    public RedisCacheAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
