package com.orange.demo.courseclassapi.dto;

import com.orange.demo.common.core.validator.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * ClassStudentDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("ClassStudentDto对象")
@Data
public class ClassStudentDto {

    /**
     * 班级Id。
     */
    @ApiModelProperty(value = "班级Id", required = true)
    @NotNull(message = "数据验证失败，班级Id不能为空！", groups = {UpdateGroup.class})
    private Long classId;

    /**
     * 学生Id。
     */
    @ApiModelProperty(value = "学生Id", required = true)
    @NotNull(message = "数据验证失败，学生Id不能为空！", groups = {UpdateGroup.class})
    private Long studentId;
}
