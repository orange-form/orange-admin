package com.orangeforms.webadmin.app.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * GradeVO视图对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("GradeVO视图对象")
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
