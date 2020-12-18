package com.orange.demo.app.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * StudentVO对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class StudentVo {

    /**
     * 学生Id。
     */
    private Long studentId;

    /**
     * 登录手机。
     */
    private String loginMobile;

    /**
     * 学生姓名。
     */
    private String studentName;

    /**
     * 所在省份Id。
     */
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    private Long cityId;

    /**
     * 区县Id。
     */
    private Long districtId;

    /**
     * 学生性别 (0: 女生 1: 男生)。
     */
    private Integer gender;

    /**
     * 生日。
     */
    private Date birthday;

    /**
     * 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。
     */
    private Integer experienceLevel;

    /**
     * 总共充值学币数量。
     */
    private Integer totalCoin;

    /**
     * 可用学币数量。
     */
    private Integer leftCoin;

    /**
     * 年级Id。
     */
    private Integer gradeId;

    /**
     * 校区Id。
     */
    private Long schoolId;

    /**
     * 注册时间。
     */
    private Date registerTime;

    /**
     * 学生状态 (0: 正常 1: 锁定 2: 注销)。
     */
    private Integer status;

    /**
     * provinceId 字典关联数据。
     */
    private Map<String, Object> provinceIdDictMap;

    /**
     * cityId 字典关联数据。
     */
    private Map<String, Object> cityIdDictMap;

    /**
     * districtId 字典关联数据。
     */
    private Map<String, Object> districtIdDictMap;

    /**
     * gradeId 字典关联数据。
     */
    private Map<String, Object> gradeIdDictMap;

    /**
     * schoolId 字典关联数据。
     */
    private Map<String, Object> schoolIdDictMap;

    /**
     * gender 常量字典关联数据。
     */
    private Map<String, Object> genderDictMap;

    /**
     * experienceLevel 常量字典关联数据。
     */
    private Map<String, Object> experienceLevelDictMap;

    /**
     * status 常量字典关联数据。
     */
    private Map<String, Object> statusDictMap;
}
