package com.orange.demo.webadmin.app.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * CourseTransStatsVO视图对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class CourseTransStatsVo {

    /**
     * 主键Id。
     */
    private Long statsId;

    /**
     * 统计日期。
     */
    private Date statsDate;

    /**
     * 科目Id。
     */
    private Integer subjectId;

    /**
     * 年级Id。
     */
    private Integer gradeId;

    /**
     * 年级名称。
     */
    private String gradeName;

    /**
     * 课程Id。
     */
    private Long courseId;

    /**
     * 课程名称。
     */
    private String courseName;

    /**
     * 学生上课次数。
     */
    private Integer studentAttendCount;

    /**
     * 学生献花数量。
     */
    private Integer studentFlowerAmount;

    /**
     * 学生献花次数。
     */
    private Integer studentFlowerCount;

    /**
     * gradeId 字典关联数据。
     */
    private Map<String, Object> gradeIdDictMap;

    /**
     * subjectId 常量字典关联数据。
     */
    private Map<String, Object> subjectIdDictMap;
}
