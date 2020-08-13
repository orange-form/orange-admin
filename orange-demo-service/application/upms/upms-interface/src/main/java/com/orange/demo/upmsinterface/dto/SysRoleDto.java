package com.orange.demo.upmsinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 角色Dto。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Data
public class SysRoleDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "角色Id不能为空！", groups = {UpdateGroup.class})
    private Long roleId;

    /**
     * 角色名称。
     */
    @NotBlank(message = "角色名称不能为空！")
    private String roleName;

    /**
     * 创建者。
     */
    private Long createUserId;

    /**
     * 创建者显示名称。
     */
    private String createUsername;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 角色与菜单关联对象列表。
     */
    private List<Map<String, Object>> sysRoleMenuList;

    private Date createTimeStart;

    private Date createTimeEnd;

    private String searchString;
}
