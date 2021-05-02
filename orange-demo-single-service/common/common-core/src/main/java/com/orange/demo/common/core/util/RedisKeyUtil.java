package com.orange.demo.common.core.util;

/**
 * Redis 键生成工具类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public class RedisKeyUtil {

    /**
     * 获取通用的session缓存的键前缀。
     *
     * @return session缓存的键前缀。
     */
    public static String getSessionIdPrefix() {
        return "SESSIONID__";
    }

    /**
     * 获取指定用户Id的session缓存的键前缀。
     *
     * @param loginName 指定的用户登录名。
     * @return session缓存的键前缀。
     */
    public static String getSessionIdPrefix(String loginName) {
        return "SESSIONID__" + loginName + "_";
    }

    /**
     * 计算SessionId返回存储于Redis中的键。
     *
     * @param sessionId 会话Id。
     * @return 会话存储于Redis中的键值。
     */
    public static String makeSessionIdKeyForRedis(String sessionId) {
        return "SESSIONID__" + sessionId;
    }

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
