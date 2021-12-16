package com.orangeforms.courseclassapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * StudentClassVO视图对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("StudentClassVO视图对象")
@Data
public class StudentClassVo {

    /**
     * 班级Id。
     */
    @ApiModelProperty(value = "班级Id")
    private Long classId;

    /**
     * 班级名称。
     */
    @ApiModelProperty(value = "班级名称")
    private String className;

    /**
     * 学校Id。
     */
    @ApiModelProperty(value = "学校Id")
    private Long schoolId;

    /**
     * 学生班长Id。
     */
    @ApiModelProperty(value = "学生班长Id")
    private Long leaderId;

    /**
     * 已完成课时数量。
     */
    @ApiModelProperty(value = "已完成课时数量")
    private Integer finishClassHour;

    /**
     * 班级级别(0: 初级班 1: 培优班 2: 冲刺提分班 3: 竞赛班)。
     */
    @ApiModelProperty(value = "班级级别(0: 初级班 1: 培优班 2: 冲刺提分班 3: 竞赛班)")
    private Integer classLevel;

    /**
     * 创建用户。
     */
    @ApiModelProperty(value = "创建用户")
    private Long createUserId;

    /**
     * 班级创建时间。
     */
    @ApiModelProperty(value = "班级创建时间")
    private Date createTime;

    /**
     * schoolId 字典关联数据。
     */
    @ApiModelProperty(value = "schoolId 字典关联数据")
    private Map<String, Object> schoolIdDictMap;

    /**
     * leaderId 字典关联数据。
     */
    @ApiModelProperty(value = "leaderId 字典关联数据")
    private Map<String, Object> leaderIdDictMap;

    /**
     * classLevel 常量字典关联数据。
     */
    @ApiModelProperty(value = "classLevel 常量字典关联数据")
    private Map<String, Object> classLevelDictMap;
}
