package com.orangeforms.webadmin.upms.vo;

import lombok.Data;

import java.util.*;

/**
 * 权限字VO。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysPermCodeVo {

    /**
     * 权限字Id。
     */
    private Long permCodeId;

    /**
     * 权限字标识(一般为有含义的英文字符串)。
     */
    private String permCode;

    /**
     * 上级权限字Id。
     */
    private Long parentId;

    /**
     * 权限字类型(0: 表单 1: UI片段 2: 操作)。
     */
    private Integer permCodeType;

    /**
     * 显示名称。
     */
    private String showName;

    /**
     * 显示顺序(数值越小，越靠前)。
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
     * 权限字与权限资源关联对象列表。
     */
    private List<Map<String, Object>> sysPermCodePermList;
}