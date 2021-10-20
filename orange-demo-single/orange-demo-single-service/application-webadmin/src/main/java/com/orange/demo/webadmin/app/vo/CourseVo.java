package com.orange.demo.webadmin.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * CourseVO视图对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class CourseVo {

    /**
     * 主键Id。
     */
    private Long courseId;

    /**
     * 课程名称。
     */
    private String courseName;

    /**
     * 课程价格。
     */
    private BigDecimal price;

    /**
     * 课程描述。
     */
    private String description;

    /**
     * 课程难度(0: 容易 1: 普通 2: 很难)。
     */
    private Integer difficulty;

    /**
     * 年级Id。
     */
    private Integer gradeId;

    /**
     * 学科Id。
     */
    private Integer subjectId;

    /**
     * 课时数量。
     */
    private Integer classHour;

    /**
     * 多张课程图片地址。
     */
    private String pictureUrl;

    /**
     * 创建用户Id。
     */
    private Long createUserId;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 最后修改时间。
     */
    private Date updateTime;

    /**
     * courseId 的多对多关联表数据对象，数据对应类型为ClassCourseVo。
     */
    private Map<String, Object> classCourse;

    /**
     * gradeId 字典关联数据。
     */
    private Map<String, Object> gradeIdDictMap;

    /**
     * difficulty 常量字典关联数据。
     */
    private Map<String, Object> difficultyDictMap;

    /**
     * subjectId 常量字典关联数据。
     */
    private Map<String, Object> subjectIdDictMap;
}
