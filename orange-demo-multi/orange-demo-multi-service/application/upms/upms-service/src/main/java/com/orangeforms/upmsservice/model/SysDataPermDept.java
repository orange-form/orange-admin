package com.orangeforms.upmsservice.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

/**
 * 数据权限与部门关联实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@ToString(of = {"deptId"})
@TableName(value = "zz_sys_data_perm_dept")
public class SysDataPermDept {

    /**
     * 数据权限Id。
     */
    @TableField(value = "data_perm_id")
    private Long dataPermId;

    /**
     * 关联部门Id。
     */
    @TableField(value = "dept_id")
    private Long deptId;
}
