package com.orange.demo.app.model;

import com.orange.demo.application.common.constant.Subject;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * CourseTransStats实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("CourseTransStats实体对象")
@Data
@Table(name = "zz_course_trans_stats")
public class CourseTransStats {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id")
    private Long statsId;

    /**
     * 统计日期。
     */
    @ApiModelProperty(value = "统计日期", required = true)
    @NotNull(message = "数据验证失败，统计日期不能为空！")
    @Column(name = "stats_date")
    private Date statsDate;

    /**
     * 科目Id。
     */
    @ApiModelProperty(value = "科目Id", required = true)
    @NotNull(message = "数据验证失败，所属科目不能为空！")
    @ConstDictRef(constDictClass = Subject.class, message = "数据验证失败，所属科目为无效值！")
    @Column(name = "subject_id")
    private Integer subjectId;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id", required = true)
    @NotNull(message = "数据验证失败，所属年级不能为空！")
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @ApiModelProperty(value = "年级名称")
    @Column(name = "grade_name")
    private String gradeName;

    /**
     * 课程Id。
     */
    @ApiModelProperty(value = "课程Id", required = true)
    @NotNull(message = "数据验证失败，课程Id不能为空！")
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 课程名称。
     */
    @ApiModelProperty(value = "课程名称")
    @Column(name = "course_name")
    private String courseName;

    /**
     * 学生上课次数。
     */
    @ApiModelProperty(value = "学生上课次数", required = true)
    @NotNull(message = "数据验证失败，上课次数不能为空！")
    @Column(name = "student_attend_count")
    private Integer studentAttendCount;

    /**
     * 学生献花数量。
     */
    @ApiModelProperty(value = "学生献花数量", required = true)
    @NotNull(message = "数据验证失败，献花数量不能为空！")
    @Column(name = "student_flower_amount")
    private Integer studentFlowerAmount;

    /**
     * 学生献花次数。
     */
    @ApiModelProperty(value = "学生献花次数", required = true)
    @NotNull(message = "数据验证失败，献花次数不能为空！")
    @Column(name = "student_flower_count")
    private Integer studentFlowerCount;

    /**
     * statsDate 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "statsDate 范围过滤起始值(>=)")
    @Transient
    private String statsDateStart;

    /**
     * statsDate 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "statsDate 范围过滤结束值(<=)")
    @Transient
    private String statsDateEnd;

    @ApiModelProperty(hidden = true)
    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @Transient
    private Map<String, Object> gradeIdDictMap;

    @ApiModelProperty(hidden = true)
    @RelationConstDict(
            masterIdField = "subjectId",
            constantDictClass = Subject.class)
    @Transient
    private Map<String, Object> subjectIdDictMap;
}
