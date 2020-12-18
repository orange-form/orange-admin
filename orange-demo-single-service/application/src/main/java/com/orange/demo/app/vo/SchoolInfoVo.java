package com.orange.demo.app.vo;

import lombok.Data;

import java.util.Map;

/**
 * SchoolInfoVO对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class SchoolInfoVo {

    /**
     * 学校Id。
     */
    private Long schoolId;

    /**
     * 学校名称。
     */
    private String schoolName;

    /**
     * 所在省Id。
     */
    private Long provinceId;

    /**
     * 所在城市Id。
     */
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
