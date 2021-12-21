package com.orangeforms.courseclassapi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 行政区划Dto。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("行政区划实体对象")
@Data
public class AreaCodeDto {

    /**
     * 行政区划主键Id
     */
    @ApiModelProperty(value = "行政区划主键Id", required = true)
    private Long areaId;

    /**
     * 行政区划名称
     */
    @ApiModelProperty(value = "行政区划名称")
    private String areaName;

    /**
     * 行政区划级别 (1: 省级别 2: 市级别 3: 区级别)
     */
    @ApiModelProperty(value = "行政区划级别")
    private Integer areaLevel;

    /**
     * 父级行政区划Id
     */
    @ApiModelProperty(value = "父级行政区划Id")
    private Long parentId;
}