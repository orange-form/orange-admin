package com.orange.demo.courseclassinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;

import lombok.Data;

import javax.validation.constraints.*;

import java.util.Map;

/**
 * SchoolInfoDto对象。
 *
 * @author Orange Team
 * @date 2020-08-08
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

    /**
     * provinceId 字典关联数据。
     */
    private Map<String, Object> provinceIdDictMap;

    /**
     * cityId 字典关联数据。
     */
    private Map<String, Object> cityIdDictMap;
}
