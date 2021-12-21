package com.orangeforms.uaaauth.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义访问令牌对象的数据增强器。
 * 在/oauth/token接口中，仅仅返回最基本的验证数据，这里我们通过增强器，将username也连同返回，
 * 其目的主要为两点：
 * 1. 避免uaa客户端在获取access_token后，再次发出请求获取username，提升验证效率。
 * 2. 给出一个典型的例子，今后如果需要添加更多的信息，可以直接在这里修改。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>(1);
        Object principal = authentication.getPrincipal();
        // 增加id参数
        if (principal instanceof User) {
            User user = (User) principal;
            additionalInfo.put("username", user.getUsername());
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
