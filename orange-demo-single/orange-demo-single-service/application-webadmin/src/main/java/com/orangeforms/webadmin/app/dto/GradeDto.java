package com.orangeforms.webadmin.app.dto;

import com.orangeforms.common.core.validator.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * GradeDto对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("GradeDto对象")
@Data
public class GradeDto {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @ApiModelProperty(value = "年级名称", required = true)
    @NotBlank(message = "数据验证失败，年级名称不能为空！")
    private String gradeName;
}
