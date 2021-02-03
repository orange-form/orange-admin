package com.orange.demo.courseclassinterface.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassStudentVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("ClassStudentVO实体对象")
@Data
public class ClassStudentVo {

    /**
     * 班级Id。
     */
    @ApiModelProperty(value = "班级Id")
    private Long classId;

    /**
     * 学生Id。
     */
    @ApiModelProperty(value = "学生Id")
    private Long studentId;
}
