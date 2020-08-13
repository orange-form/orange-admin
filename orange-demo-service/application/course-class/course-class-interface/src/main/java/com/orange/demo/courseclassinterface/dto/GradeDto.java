package com.orange.demo.courseclassinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * GradeDto对象。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Data
public class GradeDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @NotBlank(message = "数据验证失败，年级名称不能为空！")
    private String gradeName;

    /**
     * 是否正在使用（0：不是，1：是）。
     */
    @NotNull(message = "数据验证失败，是否正在使用（0：不是，1：是）不能为空！")
    private Integer status;
}
