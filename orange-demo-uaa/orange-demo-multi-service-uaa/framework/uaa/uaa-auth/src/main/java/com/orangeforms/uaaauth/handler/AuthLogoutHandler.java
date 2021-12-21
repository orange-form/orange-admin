package com.orangeforms.uaaauth.handler;

import com.orangeforms.uaaauth.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登出操作处理器对象。这里将同时清空redis中的access_token。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class AuthLogoutHandler implements LogoutHandler {
    @Autowired
    private TokenStore tokenStore;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            token = AuthUtils.extractToken(request);
        }
        if (StringUtils.isNotBlank(token)) {
            OAuth2AccessToken existingAccessToken = tokenStore.readAccessToken(token);
            if (existingAccessToken != null) {
                tokenStore.removeAccessToken(existingAccessToken);
            }
        }
    }
}
