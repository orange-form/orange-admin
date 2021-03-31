package com.orange.demo.webadmin.app.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.application.common.constant.Subject;

import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * CourseTransStatsDto对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class CourseTransStatsDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long statsId;

    /**
     * 统计日期。
     */
    @NotNull(message = "数据验证失败，统计日期不能为空！")
    private Date statsDate;

    /**
     * 科目Id。
     */
    @NotNull(message = "数据验证失败，所属科目不能为空！")
    @ConstDictRef(constDictClass = Subject.class, message = "数据验证失败，所属科目为无效值！")
    private Integer subjectId;

    /**
     * 年级Id。
     */
    @NotNull(message = "数据验证失败，所属年级不能为空！")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    private String gradeName;

    /**
     * 课程Id。
     */
    @NotNull(message = "数据验证失败，课程Id不能为空！")
    private Long courseId;

    /**
     * 课程名称。
     */
    private String courseName;

    /**
     * 学生上课次数。
     */
    @NotNull(message = "数据验证失败，上课次数不能为空！")
    private Integer studentAttendCount;

    /**
     * 学生献花数量。
     */
    @NotNull(message = "数据验证失败，献花数量不能为空！")
    private Integer studentFlowerAmount;

    /**
     * 学生献花次数。
     */
    @NotNull(message = "数据验证失败，献花次数不能为空！")
    private Integer studentFlowerCount;

    /**
     * statsDate 范围过滤起始值(>=)。
     */
    private String statsDateStart;

    /**
     * statsDate 范围过滤结束值(<=)。
     */
    private String statsDateEnd;
}
