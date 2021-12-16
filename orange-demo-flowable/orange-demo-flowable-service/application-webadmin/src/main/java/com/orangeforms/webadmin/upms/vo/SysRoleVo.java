package com.orangeforms.webadmin.upms.vo;

import lombok.Data;

import java.util.*;

/**
 * 角色VO。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysRoleVo {

    /**
     * 角色Id。
     */
    private Long roleId;

    /**
     * 角色名称。
     */
    private String roleName;

    /**
     * 创建者Id。
     */
    private Long createUserId;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新者Id。
     */
    private Long updateUserId;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 角色与菜单关联对象列表。
     */
    private List<Map<String, Object>> sysRoleMenuList;
}
