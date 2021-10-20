package com.orange.demo.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orange.demo.application.common.constant.Subject;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.webadmin.app.vo.CourseTransStatsVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * CourseTransStats实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@TableName(value = "zz_course_trans_stats")
public class CourseTransStats {

    /**
     * 主键Id。
     */
    @TableId(value = "stats_id", type = IdType.AUTO)
    private Long statsId;

    /**
     * 统计日期。
     */
    @TableField(value = "stats_date")
    private Date statsDate;

    /**
     * 科目Id。
     */
    @TableField(value = "subject_id")
    private Integer subjectId;

    /**
     * 年级Id。
     */
    @TableField(value = "grade_id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @TableField(value = "grade_name")
    private String gradeName;

    /**
     * 课程Id。
     */
    @TableField(value = "course_id")
    private Long courseId;

    /**
     * 课程名称。
     */
    @TableField(value = "course_name")
    private String courseName;

    /**
     * 学生上课次数。
     */
    @TableField(value = "student_attend_count")
    private Integer studentAttendCount;

    /**
     * 学生献花数量。
     */
    @TableField(value = "student_flower_amount")
    private Integer studentFlowerAmount;

    /**
     * 学生献花次数。
     */
    @TableField(value = "student_flower_count")
    private Integer studentFlowerCount;

    /**
     * statsDate 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String statsDateStart;

    /**
     * statsDate 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String statsDateEnd;

    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @TableField(exist = false)
    private Map<String, Object> gradeIdDictMap;

    @RelationConstDict(
            masterIdField = "subjectId",
            constantDictClass = Subject.class)
    @TableField(exist = false)
    private Map<String, Object> subjectIdDictMap;

    @Mapper
    public interface CourseTransStatsModelMapper extends BaseModelMapper<CourseTransStatsVo, CourseTransStats> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param courseTransStatsVo 域对象。
         * @return 实体对象。
         */
        @Override
        CourseTransStats toModel(CourseTransStatsVo courseTransStatsVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param courseTransStats 实体对象。
         * @return 域对象。
         */
        @Override
        CourseTransStatsVo fromModel(CourseTransStats courseTransStats);
    }
    public static final CourseTransStatsModelMapper INSTANCE = Mappers.getMapper(CourseTransStatsModelMapper.class);
}
