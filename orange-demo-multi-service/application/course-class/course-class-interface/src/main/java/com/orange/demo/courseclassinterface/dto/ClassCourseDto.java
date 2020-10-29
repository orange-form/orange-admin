package com.orange.demo.courseclassinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * ClassCourseDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class ClassCourseDto {

    /**
     * 班级Id。
     */
    @NotNull(message = "数据验证失败，班级Id不能为空！", groups = {UpdateGroup.class})
    private Long classId;

    /**
     * 课程Id。
     */
    @NotNull(message = "数据验证失败，课程Id不能为空！", groups = {UpdateGroup.class})
    private Long courseId;

    /**
     * 课程顺序(数值越小越靠前)。
     */
    @NotNull(message = "数据验证失败，课程顺序(数值越小越靠前)不能为空！", groups = {UpdateGroup.class})
    private Integer courseOrder;
}
