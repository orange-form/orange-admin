package com.orange.demo.common.core.util;

/**
 * Redis 键生成工具类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public class RedisKeyUtil {

    /**
     * 计算SessionId关联的权限数据存储于Redis中的键。
     *
     * @param sessionId 会话Id。
     * @return 会话关联的权限数据存储于Redis中的键值。
     */
    public static String makeSessionPermIdKeyForRedis(String sessionId) {
        return "PERM__" + sessionId;
    }

    /**
     * 计算SessionId关联的数据权限数据存储于Redis中的键。
     *
     * @param sessionId 会话Id。
     * @return 会话关联的数据权限数据存储于Redis中的键值。
     */
    public static String makeSessionDataPermIdKeyForRedis(String sessionId) {
        return "DATA_PERM__" + sessionId;
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private RedisKeyUtil() {
    }
}
