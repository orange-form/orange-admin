package com.orange.demo.statsservice.model;

import com.orange.demo.courseclassinterface.client.GradeClient;
import com.orange.demo.courseclassinterface.dto.GradeDto;
import com.orange.demo.courseclassinterface.client.CourseClient;
import com.orange.demo.courseclassinterface.dto.CourseDto;
import com.orange.demo.application.common.constant.Subject;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.statsinterface.dto.CourseTransStatsDto;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * CourseTransStats实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_course_trans_stats")
public class CourseTransStats {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id")
    private Long statsId;

    /**
     * 统计日期。
     */
    @NotNull(message = "数据验证失败，统计日期不能为空！")
    @Column(name = "stats_date")
    private Date statsDate;

    /**
     * 科目Id。
     */
    @NotNull(message = "数据验证失败，所属科目不能为空！")
    @ConstDictRef(constDictClass = Subject.class, message = "数据验证失败，所属科目为无效值！")
    @Column(name = "subject_id")
    private Integer subjectId;

    /**
     * 年级Id。
     */
    @NotNull(message = "数据验证失败，所属年级不能为空！")
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @Column(name = "grade_name")
    private String gradeName;

    /**
     * 课程Id。
     */
    @NotNull(message = "数据验证失败，课程ID不能为空！")
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 课程名称。
     */
    @Column(name = "course_name")
    private String courseName;

    /**
     * 学生上课次数。
     */
    @NotNull(message = "数据验证失败，上课次数不能为空！")
    @Column(name = "student_attend_count")
    private Integer studentAttendCount;

    /**
     * 学生献花数量。
     */
    @NotNull(message = "数据验证失败，献花数量不能为空！")
    @Column(name = "student_flower_amount")
    private Integer studentFlowerAmount;

    /**
     * 学生献花次数。
     */
    @NotNull(message = "数据验证失败，献花次数不能为空！")
    @Column(name = "student_flower_count")
    private Integer studentFlowerCount;

    /**
     * statsDate 范围过滤起始值(>=)。
     */
    @Transient
    private String statsDateStart;

    /**
     * statsDate 范围过滤结束值(<=)。
     */
    @Transient
    private String statsDateEnd;

    @RelationDict(
            masterIdField = "gradeId",
            slaveClientClass = GradeClient.class,
            slaveModelClass = GradeDto.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @Transient
    private Map<String, Object> gradeIdDictMap;

    @RelationDict(
            masterIdField = "courseId",
            slaveClientClass = CourseClient.class,
            slaveModelClass = CourseDto.class,
            slaveIdField = "courseId",
            slaveNameField = "courseName")
    @Transient
    private Map<String, Object> courseIdDictMap;

    @RelationConstDict(
            masterIdField = "subjectId",
            constantDictClass = Subject.class)
    @Transient
    private Map<String, Object> subjectIdDictMap;

    @Mapper
    public interface CourseTransStatsModelMapper extends BaseModelMapper<CourseTransStatsDto, CourseTransStats> {
        /**
         * 转换Dto对象到实体对象。
         *
         * @param courseTransStatsDto 域对象。
         * @return 实体对象。
         */
        @Override
        CourseTransStats toModel(CourseTransStatsDto courseTransStatsDto);
        /**
         * 转换实体对象到Dto对象。
         *
         * @param courseTransStats 实体对象。
         * @return 域对象。
         */
        @Override
        CourseTransStatsDto fromModel(CourseTransStats courseTransStats);
    }
    public static final CourseTransStatsModelMapper INSTANCE = Mappers.getMapper(CourseTransStatsModelMapper.class);
}
