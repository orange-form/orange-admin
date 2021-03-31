package com.orange.demo.webadmin.app.dto;

import com.orange.demo.common.core.validator.UpdateGroup;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * SchoolInfoDto对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class SchoolInfoDto {

    /**
     * 学校Id。
     */
    @NotNull(message = "数据验证失败，学校Id不能为空！", groups = {UpdateGroup.class})
    private Long schoolId;

    /**
     * 学校名称。
     */
    @NotBlank(message = "数据验证失败，学校名称不能为空！")
    private String schoolName;

    /**
     * 所在省Id。
     */
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    private Long cityId;
}
