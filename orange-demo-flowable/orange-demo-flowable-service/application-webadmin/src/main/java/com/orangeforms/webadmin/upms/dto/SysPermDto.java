package com.orangeforms.webadmin.upms.dto;

import com.orangeforms.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限资源Dto。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysPermDto {

    /**
     * 权限资源Id。
     */
    @NotNull(message = "权限Id不能为空！", groups = {UpdateGroup.class})
    private Long permId;

    /**
     * 权限资源名称。
     */
    @NotBlank(message = "权限资源名称不能为空！")
    private String permName;

    /**
     * shiro格式的权限字，如(upms:sysUser:add)。
     */
    private String permCode;

    /**
     * 权限所在的权限模块Id。
     */
    @NotNull(message = "权限模块Id不能为空！")
    private Long moduleId;

    /**
     * 关联的URL。
     */
    @NotBlank(message = "权限关联的url不能为空！")
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @NotNull(message = "权限显示顺序不能为空！")
    private Integer showOrder;
}