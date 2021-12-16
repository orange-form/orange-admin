package com.orangeforms.webadmin.upms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据权限与部门关联VO。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("数据权限与部门关联VO")
@Data
public class SysDataPermDeptVo {

    /**
     * 数据权限Id。
     */
    @ApiModelProperty(value = "数据权限Id")
    private Long dataPermId;

    /**
     * 关联部门Id。
     */
    @ApiModelProperty(value = "关联部门Id")
    private Long deptId;
}