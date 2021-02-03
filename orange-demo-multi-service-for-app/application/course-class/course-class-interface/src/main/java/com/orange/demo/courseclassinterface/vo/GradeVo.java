package com.orange.demo.courseclassinterface.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * GradeVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("GradeVO实体对象")
@Data
public class GradeVo {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @ApiModelProperty(value = "年级名称")
    private String gradeName;
}
