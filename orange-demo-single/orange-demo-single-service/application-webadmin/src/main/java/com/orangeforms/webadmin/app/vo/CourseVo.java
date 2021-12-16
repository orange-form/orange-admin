package com.orangeforms.webadmin.app.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("CourseVO视图对象")
@Data
public class CourseVo {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    private Long courseId;

    /**
     * 课程名称。
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;

    /**
     * 课程价格。
     */
    @ApiModelProperty(value = "课程价格")
    private BigDecimal price;

    /**
     * 课程描述。
     */
    @ApiModelProperty(value = "课程描述")
    private String description;

    /**
     * 课程难度(0: 容易 1: 普通 2: 很难)。
     */
    @ApiModelProperty(value = "课程难度(0: 容易 1: 普通 2: 很难)")
    private Integer difficulty;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id")
    private Integer gradeId;

    /**
     * 学科Id。
     */
    @ApiModelProperty(value = "学科Id")
    private Integer subjectId;

    /**
     * 课时数量。
     */
    @ApiModelProperty(value = "课时数量")
    private Integer classHour;

    /**
     * 多张课程图片地址。
     */
    @ApiModelProperty(value = "多张课程图片地址")
    private String pictureUrl;

    /**
     * 创建用户Id。
     */
    @ApiModelProperty(value = "创建用户Id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 最后修改时间。
     */
    @ApiModelProperty(value = "最后修改时间")
    private Date updateTime;

    /**
     * courseId 的多对多关联表数据对象，数据对应类型为ClassCourseVo。
     */
    @ApiModelProperty(value = "courseId 的多对多关联表数据对象，数据对应类型为ClassCourseVo")
    private Map<String, Object> classCourse;

    /**
     * gradeId 字典关联数据。
     */
    @ApiModelProperty(value = "gradeId 字典关联数据")
    private Map<String, Object> gradeIdDictMap;

    /**
     * difficulty 常量字典关联数据。
     */
    @ApiModelProperty(value = "difficulty 常量字典关联数据")
    private Map<String, Object> difficultyDictMap;

    /**
     * subjectId 常量字典关联数据。
     */
    @ApiModelProperty(value = "subjectId 常量字典关联数据")
    private Map<String, Object> subjectIdDictMap;
}
