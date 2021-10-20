package com.orange.demo.webadmin.upms.dto;

import lombok.Data;

/**
 * 数据权限与部门关联Dto。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class SysDataPermDeptDto {

    /**
     * 数据权限Id。
     */
    private Long dataPermId;

    /**
     * 关联部门Id。
     */
    private Long deptId;
}