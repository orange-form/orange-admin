package com.orange.demo.statsinterface.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * CourseTransStatsVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("CourseTransStatsVO实体对象")
@Data
public class CourseTransStatsVo {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    private Long statsId;

    /**
     * 统计日期。
     */
    @ApiModelProperty(value = "统计日期")
    private Date statsDate;

    /**
     * 科目Id。
     */
    @ApiModelProperty(value = "科目Id")
    private Integer subjectId;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @ApiModelProperty(value = "年级名称")
    private String gradeName;

    /**
     * 课程Id。
     */
    @ApiModelProperty(value = "课程Id")
    private Long courseId;

    /**
     * 课程名称。
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;

    /**
     * 学生上课次数。
     */
    @ApiModelProperty(value = "学生上课次数")
    private Integer studentAttendCount;

    /**
     * 学生献花数量。
     */
    @ApiModelProperty(value = "学生献花数量")
    private Integer studentFlowerAmount;

    /**
     * 学生献花次数。
     */
    @ApiModelProperty(value = "学生献花次数")
    private Integer studentFlowerCount;

    /**
     * gradeId 字典关联数据。
     */
    @ApiModelProperty(hidden = true)
    private Map<String, Object> gradeIdDictMap;

    /**
     * courseId 字典关联数据。
     */
    @ApiModelProperty(hidden = true)
    private Map<String, Object> courseIdDictMap;

    /**
     * subjectId 常量字典关联数据。
     */
    @ApiModelProperty(hidden = true)
    private Map<String, Object> subjectIdDictMap;
}
