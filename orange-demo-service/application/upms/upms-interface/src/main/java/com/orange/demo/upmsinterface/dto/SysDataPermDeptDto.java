package com.orange.demo.upmsinterface.dto;

import lombok.Data;

/**
 * 数据权限与部门关联Dto。
 *
 * @author Orange Team
 * @date 2020-08-08
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
    private Long menuId;
}