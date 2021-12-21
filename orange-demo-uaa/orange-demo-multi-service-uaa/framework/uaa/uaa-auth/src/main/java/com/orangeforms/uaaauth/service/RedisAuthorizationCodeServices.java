package com.orangeforms.uaaauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Service;

/**
 * 在OAuth2授权码验证模式下，请求的授权码都会存储到redis中，以实现UAA服务的分布式部署。
 * 同时也提升了验证过程的执行效率。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    private final RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    private static final int EXPIRED_SECONDS = 10 * 60;

    private RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }

    /**
     * 将授权码存储到redis中。
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        RedisConnection connection = getConnection();
        try {
            byte[] key = serializationStrategy.serialize(redisKey(code));
            connection.set(key, serializationStrategy.serialize(authentication));
            connection.expire(key, EXPIRED_SECONDS);
        } finally {
            connection.close();
        }
    }

    /**
     * 将授权码从redis中删除。
     */
    @Override
    protected OAuth2Authentication remove(final String code) {
        OAuth2Authentication token = null;
        RedisConnection connection = getConnection();
        try {
            byte[] key = serializationStrategy.serialize(redisKey(code));
            byte[] value = connection.get(key);
            if (value != null) {
                connection.del(key);
                token = serializationStrategy.deserialize(value, OAuth2Authentication.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        return token;
    }

    private String redisKey(String code) {
        return "oauth:code:" + code;
    }
}
