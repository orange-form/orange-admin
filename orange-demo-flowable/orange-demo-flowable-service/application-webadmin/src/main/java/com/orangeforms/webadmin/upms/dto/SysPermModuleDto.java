package com.orangeforms.webadmin.upms.dto;

import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.webadmin.upms.model.constant.SysPermModuleType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限资源模块Dto。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysPermModuleDto {

    /**
     * 权限模块Id。
     */
    @NotNull(message = "权限模块Id不能为空！", groups = {UpdateGroup.class})
    private Long moduleId;

    /**
     * 权限模块名称。
     */
    @NotBlank(message = "权限模块名称不能为空！")
    private String moduleName;

    /**
     * 上级权限模块Id。
     */
    private Long parentId;

    /**
     * 权限模块类型(0: 普通模块 1: Controller模块)。
     */
    @NotNull(message = "模块类型不能为空！")
    @ConstDictRef(constDictClass = SysPermModuleType.class, message = "数据验证失败，权限模块类型为无效值！")
    private Integer moduleType;

    /**
     * 权限模块在当前层级下的顺序，由小到大。
     */
    @NotNull(message = "权限模块显示顺序不能为空！")
    private Integer showOrder;
}