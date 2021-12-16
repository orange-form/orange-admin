package com.orangeforms.webadmin.upms.dto;

import com.orangeforms.common.core.validator.UpdateGroup;

import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * SysDeptDto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysDeptDto {

    /**
     * 部门Id。
     */
    @NotNull(message = "数据验证失败，部门Id不能为空！", groups = {UpdateGroup.class})
    private Long deptId;

    /**
     * 部门名称。
     */
    @NotBlank(message = "数据验证失败，部门名称不能为空！")
    private String deptName;

    /**
     * 显示顺序。
     */
    @NotNull(message = "数据验证失败，显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 父部门Id。
     */
    private Long parentId;

    /**
     * 创建者Id。
     */
    private Long createUserId;

    /**
     * 更新者Id。
     */
    private Long updateUserId;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新时间。
     */
    private Date updateTime;
}
