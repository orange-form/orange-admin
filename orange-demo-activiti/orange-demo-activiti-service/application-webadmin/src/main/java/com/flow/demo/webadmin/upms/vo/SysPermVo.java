package com.flow.demo.webadmin.upms.vo;

import lombok.Data;

import java.util.*;

/**
 * 权限资源VO。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysPermVo {

    /**
     * 权限资源Id。
     */
    private Long permId;

    /**
     * 权限资源名称。
     */
    private String permName;

    /**
     * shiro格式的权限字，如(upms:sysUser:add)。
     */
    private String permCode;

    /**
     * 权限所在的权限模块Id。
     */
    private Long moduleId;

    /**
     * 关联的URL。
     */
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
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
     * 模块Id的字典关联数据。
     */
    private Map<String, Object> moduleIdDictMap;
}