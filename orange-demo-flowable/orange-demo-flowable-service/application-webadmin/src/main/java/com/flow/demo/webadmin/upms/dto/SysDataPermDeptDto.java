package com.flow.demo.webadmin.upms.dto;

import lombok.Data;

/**
 * 数据权限与部门关联Dto。
 *
 * @author Jerry
 * @date 2021-06-06
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