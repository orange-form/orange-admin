package com.orangeforms.courseclassapi.dto;

import com.orangeforms.common.core.validator.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * ClassCourseDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("ClassCourseDto对象")
@Data
public class ClassCourseDto {

    /**
     * 班级Id。
     */
    @ApiModelProperty(value = "班级Id", required = true)
    @NotNull(message = "数据验证失败，班级Id不能为空！", groups = {UpdateGroup.class})
    private Long classId;

    /**
     * 课程Id。
     */
    @ApiModelProperty(value = "课程Id", required = true)
    @NotNull(message = "数据验证失败，课程Id不能为空！", groups = {UpdateGroup.class})
    private Long courseId;

    /**
     * 课程顺序(数值越小越靠前)。
     */
    @ApiModelProperty(value = "课程顺序(数值越小越靠前)", required = true)
    @NotNull(message = "数据验证失败，课程顺序(数值越小越靠前)不能为空！", groups = {UpdateGroup.class})
    private Integer courseOrder;
}
