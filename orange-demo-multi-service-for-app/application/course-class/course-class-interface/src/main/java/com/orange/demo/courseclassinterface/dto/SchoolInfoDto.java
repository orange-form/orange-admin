package com.orange.demo.courseclassinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * SchoolInfoDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("SchoolInfoDto对象")
@Data
public class SchoolInfoDto {

    /**
     * 学校Id。
     */
    @ApiModelProperty(value = "学校Id", required = true)
    @NotNull(message = "数据验证失败，学校Id不能为空！", groups = {UpdateGroup.class})
    private Long schoolId;

    /**
     * 学校名称。
     */
    @ApiModelProperty(value = "学校名称", required = true)
    @NotBlank(message = "数据验证失败，学校名称不能为空！")
    private String schoolName;

    /**
     * 所在省Id。
     */
    @ApiModelProperty(value = "所在省Id", required = true)
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @ApiModelProperty(value = "所在城市Id", required = true)
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    private Long cityId;
}
