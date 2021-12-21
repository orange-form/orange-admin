package com.orangeforms.uaaauth.service;

import com.orangeforms.uaaauth.dao.AuthClientDetailsMapper;
import com.orangeforms.uaaauth.model.AuthClientDetails;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * UAA客户端应用数据操作服务类。该类必须为ClientDetailsService接口的实现类。
 * loadClientByClientId方法，被Oauth2框架调用获取客户端数据。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    private AuthClientDetailsMapper clientDetailsMapper;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        AuthClientDetails authClientDetails = clientDetailsMapper.selectById(clientId);
        if (authClientDetails == null || authClientDetails.getDeletedFlag() == GlobalDeletedFlag.DELETED) {
            throw new ClientRegistrationException("应用Id不存在！");
        }
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId(authClientDetails.getClientId());
        clientDetails.setClientSecret(authClientDetails.getClientSecret());
        clientDetails.setAccessTokenValiditySeconds(authClientDetails.getAccessTokenValidity());
        clientDetails.setRefreshTokenValiditySeconds(authClientDetails.getRefreshTokenValidity());
        clientDetails.setScope(Collections.singletonList("all"));
        // 这里设置为"true"，表示所有的scope都会自动审批通过。
        clientDetails.setAutoApproveScopes(Collections.singletonList("true"));
        if (StringUtils.isNotBlank(authClientDetails.getAuthorizedGrantTypes())) {
            clientDetails.setAuthorizedGrantTypes(Arrays.stream(StringUtils.split(
                    authClientDetails.getAuthorizedGrantTypes(), ",")).collect(Collectors.toSet()));
        }
        if (StringUtils.isNotBlank(authClientDetails.getWebServerRedirectUri())) {
            clientDetails.setRegisteredRedirectUri(Arrays.stream(StringUtils.split(
                    authClientDetails.getWebServerRedirectUri(), ",")).collect(Collectors.toSet()));
        }
        return clientDetails;
    }
}
