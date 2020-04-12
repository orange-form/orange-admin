package com.orange.admin.upms.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@ToString(of = {"deptId"})
@Table(name = "zz_sys_data_perm_dept")
public class SysDataPermDept {

    /**
     * 数据权限Id。
     */
    @Id
    @Column(name = "data_perm_id")
    private Long dataPermId;

    /**
     * 关联部门Id。
     */
    @Id
    @Column(name = "dept_id")
    private Long deptId;
}