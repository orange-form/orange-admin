package com.orange.demo.upmsinterface.dto;

import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.upmsinterface.constant.SysPermCodeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限字Dto。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("权限字Dto")
@Data
public class SysPermCodeDto {

    /**
     * 权限字Id。
     */
    @ApiModelProperty(value = "权限字Id", required = true)
    @NotNull(message = "权限字Id不能为空！", groups = {UpdateGroup.class})
    private Long permCodeId;

    /**
     * 权限字标识(一般为有含义的英文字符串)。
     */
    @ApiModelProperty(value = "权限字标识", required = true)
    @NotBlank(message = "权限字编码不能为空！")
    private String permCode;

    /**
     * 上级权限字Id。
     */
    @ApiModelProperty(value = "上级权限字Id")
    private Long parentId;

    /**
     * 权限字类型(0: 表单 1: UI片段 2: 操作)。
     */
    @ApiModelProperty(value = "权限字类型", required = true)
    @NotNull(message = "权限字类型不能为空！")
    @ConstDictRef(constDictClass = SysPermCodeType.class, message = "数据验证失败，权限类型为无效值！")
    private Integer permCodeType;

    /**
     * 显示名称。
     */
    @ApiModelProperty(value = "显示名称", required = true)
    @NotBlank(message = "权限字显示名称不能为空！")
    private String showName;

    /**
     * 显示顺序(数值越小，越靠前)。
     */
    @ApiModelProperty(value = "显示顺序", required = true)
    @NotNull(message = "权限字显示顺序不能为空！")
    private Integer showOrder;
}