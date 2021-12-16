package com.orangeforms.webadmin.upms.dto;

import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.webadmin.upms.model.constant.SysPermCodeType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限字Dto。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysPermCodeDto {

    /**
     * 权限字Id。
     */
    @NotNull(message = "权限字Id不能为空！", groups = {UpdateGroup.class})
    private Long permCodeId;

    /**
     * 权限字标识(一般为有含义的英文字符串)。
     */
    @NotBlank(message = "权限字编码不能为空！")
    private String permCode;

    /**
     * 上级权限字Id。
     */
    private Long parentId;

    /**
     * 权限字类型(0: 表单 1: UI片段 2: 操作)。
     */
    @NotNull(message = "权限字类型不能为空！")
    @ConstDictRef(constDictClass = SysPermCodeType.class, message = "数据验证失败，权限类型为无效值！")
    private Integer permCodeType;

    /**
     * 显示名称。
     */
    @NotBlank(message = "权限字显示名称不能为空！")
    private String showName;

    /**
     * 显示顺序(数值越小，越靠前)。
     */
    @NotNull(message = "权限字显示顺序不能为空！")
    private Integer showOrder;
}