package com.orange.demo.courseclassapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassCourseVO视图对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("ClassCourseVO视图对象")
@Data
public class ClassCourseVo {

    /**
     * 班级Id。
     */
    @ApiModelProperty(value = "班级Id")
    private Long classId;

    /**
     * 课程Id。
     */
    @ApiModelProperty(value = "课程Id")
    private Long courseId;

    /**
     * 课程顺序(数值越小越靠前)。
     */
    @ApiModelProperty(value = "课程顺序(数值越小越靠前)")
    private Integer courseOrder;
}
