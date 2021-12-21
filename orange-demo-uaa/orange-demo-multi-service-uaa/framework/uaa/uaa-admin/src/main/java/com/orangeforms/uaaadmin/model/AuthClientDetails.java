package com.orangeforms.uaaadmin.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.base.model.BaseModel;
import com.orangeforms.common.core.validator.AddGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

/**
 * AuthClientDetails实体对象。
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
    @NotBlank(message = "数据验证失败，应用标识不能为空！")
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
    @NotBlank(message = "数据验证失败，应用密钥为空！", groups = {AddGroup.class})
    @TableField(value = "client_secret_plain")
    private String clientSecretPlain;

    /**
     * 应用名称。
     */
    @TableField(value = "client_desc")
    private String clientDesc;

    /**
     * 5种oauth授权方式(authorization_code,password,refresh_token,client_credentials)。
     */
    @TableField(value = "authorized_grant_types")
    private String authorizedGrantTypes;

    /**
     * 回调地址。
     */
    @TableField(value = "web_server_redirect_uri")
    private String webServerRedirectUri;

    /**
     * access_token有效期。
     */
    @NotNull(message = "数据验证失败，TOKEN 有效期不能为空！")
    @TableField(value = "access_token_validity")
    private Integer accessTokenValidity;

    /**
     * refresh_token有效期。
     */
    @TableField(value = "refresh_token_validity")
    private Integer refreshTokenValidity;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;
}
