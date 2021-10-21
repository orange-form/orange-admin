package com.orange.demo.upmsapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * SysDeptVO视图对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("SysDeptVO视图对象")
@Data
public class SysDeptVo {

    /**
     * 部门Id。
     */
    @ApiModelProperty(value = "部门Id")
    private Long deptId;

    /**
     * 部门名称。
     */
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
     * 显示顺序。
     */
    @ApiModelProperty(value = "显示顺序")
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
