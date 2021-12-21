package com.orangeforms.uaaauth.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.base.model.BaseModel;

/**
 * OAuth2 应用客户端表的实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "zz_auth_client_details")
public class AuthClientDetails extends BaseModel {

    /**
     * 应用标识。
     */
    @TableId(value = "client_id")
    private String clientId;

    /**
     * 应用密钥(bcyt) 加密。
     */
    @JSONField(serialize = false)
    @TableField(value = "client_secret")
    private String clientSecret;

    /**
     * 应用密钥(明文)。
     */
    @TableField(value = "client_secret_plain")
    private String clientSecretPlain;

    /**
     * 应用描述。
     */
    @TableField(value = "client_desc")
    private String clientDesc;

    /**
     * 5种oauth授权方式(authorization_code,password,refresh_token,client_credentials)。
     */
    @TableField(value = "authorized_grant_types")
    private String authorizedGrantTypes;

    /**
     * 回调地址 。
     */
    @TableField(value = "web_server_redirect_uri")
    private String webServerRedirectUri;

    /**
     * access_token有效期。
     */
    @TableField(value = "access_token_validity")
    private Integer accessTokenValidity;

    /**
     * refresh_token有效期。
     */
    @TableField(value = "refresh_token_validity")
    private Integer refreshTokenValidity;

    /**
     * 是否删除(1: 正常，-1： 删除)
     */
    @JSONField(serialize = false)
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;
}