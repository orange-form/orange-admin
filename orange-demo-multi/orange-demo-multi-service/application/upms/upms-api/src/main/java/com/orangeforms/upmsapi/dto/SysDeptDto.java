package com.orangeforms.upmsapi.dto;

import com.orangeforms.common.core.validator.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * SysDeptDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("SysDeptDto对象")
@Data
public class SysDeptDto {

    /**
     * 部门Id。
     */
    @ApiModelProperty(value = "部门Id", required = true)
    @NotNull(message = "数据验证失败，部门Id不能为空！", groups = {UpdateGroup.class})
    private Long deptId;

    /**
     * 部门名称。
     */
    @ApiModelProperty(value = "部门名称", required = true)
    @NotBlank(message = "数据验证失败，部门名称不能为空！")
    private String deptName;

    /**
     * 显示顺序。
     */
    @ApiModelProperty(value = "显示顺序", required = true)
    @NotNull(message = "数据验证失败，显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 父部门Id。
     */
    @ApiModelProperty(value = "父部门Id")
    private Long parentId;

    /**
     * 创建者Id。
     */
    @ApiModelProperty(value = "创建者Id")
    private Long createUserId;

    /**
     * 更新者Id。
     */
    @ApiModelProperty(value = "更新者Id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
