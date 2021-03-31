package com.orange.demo.courseclassapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * SchoolInfoVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("SchoolInfoVO实体对象")
@Data
public class SchoolInfoVo {

    /**
     * 学校Id。
     */
    @ApiModelProperty(value = "学校Id")
    private Long schoolId;

    /**
     * 学校名称。
     */
    @ApiModelProperty(value = "学校名称")
    private String schoolName;

    /**
     * 所在省Id。
     */
    @ApiModelProperty(value = "所在省Id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @ApiModelProperty(value = "所在城市Id")
    private Long cityId;

    /**
     * provinceId 字典关联数据。
     */
    @ApiModelProperty(value = "provinceId 字典关联数据")
    private Map<String, Object> provinceIdDictMap;

    /**
     * cityId 字典关联数据。
     */
    @ApiModelProperty(value = "cityId 字典关联数据")
    private Map<String, Object> cityIdDictMap;
}
