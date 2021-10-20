package com.orange.demo.webadmin.upms.vo;

import lombok.Data;

/**
 * 数据权限与部门关联VO。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class SysDataPermDeptVo {

    /**
     * 数据权限Id。
     */
    private Long dataPermId;

    /**
     * 关联部门Id。
     */
    private Long deptId;
}