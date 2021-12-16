package com.orangeforms.webadmin.upms.vo;

import lombok.Data;

import java.util.*;

/**
 * 权限资源模块VO。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysPermModuleVo {

    /**
     * 权限模块Id。
     */
    private Long moduleId;

    /**
     * 权限模块名称。
     */
    private String moduleName;

    /**
     * 上级权限模块Id。
     */
    private Long parentId;

    /**
     * 权限模块类型(0: 普通模块 1: Controller模块)。
     */
    private Integer moduleType;

    /**
     * 权限模块在当前层级下的顺序，由小到大。
     */
    private Integer showOrder;

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
     * 权限资源对象列表。
     */
    private List<SysPermVo> sysPermList;
}