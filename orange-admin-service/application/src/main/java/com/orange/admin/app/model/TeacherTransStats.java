package com.orange.admin.app.model;

import com.orange.admin.upms.model.SysDept;
import com.orange.admin.common.core.annotation.RelationDict;
import com.orange.admin.common.core.annotation.RelationOneToOne;
import com.orange.admin.common.core.annotation.DeptFilterColumn;
import com.orange.admin.common.core.annotation.UserFilterColumn;
import com.orange.admin.common.core.validator.UpdateGroup;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * TeacherTransStats实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_teacher_trans_stats")
public class TeacherTransStats {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "stats_id")
    private Long statsId;

    /**
     * 统计日期。
     */
    @NotNull(message = "数据验证失败，统计日期不能为空！")
    @Column(name = "stats_date")
    private Date statsDate;

    /**
     * 统计月份。
     */
    @NotNull(message = "数据验证失败，统计月份不能为空！")
    @Column(name = "stats_month")
    private Date statsMonth;

    /**
     * 省份Id。
     */
    @NotNull(message = "数据验证失败，省份不能为空！")
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 城市Id。
     */
    @NotNull(message = "数据验证失败，城市不能为空！")
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 学校Id。
     */
    @NotNull(message = "数据验证失败，校区不能为空！")
    @DeptFilterColumn
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 学校名称。
     */
    @NotBlank(message = "数据验证失败，学校名称不能为空！")
    @Column(name = "school_name")
    private String schoolName;

    /**
     * 老师Id。
     */
    @NotNull(message = "数据验证失败，老师不能为空！")
    @UserFilterColumn
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * 老师名称。
     */
    @NotBlank(message = "数据验证失败，老师名称不能为空！")
    @Column(name = "teacher_name")
    private String teacherName;

    /**
     * 视频观看数量。
     */
    @NotNull(message = "数据验证失败，视频观看数量不能为空！")
    @Column(name = "video_watch_count")
    private Integer videoWatchCount;

    /**
     * 献花数量。
     */
    @NotNull(message = "数据验证失败，献花数量不能为空！")
    @Column(name = "flower_count")
    private Integer flowerCount;

    /**
     * 新增学生数量。
     */
    @NotNull(message = "数据验证失败，新增学生数量不能为空！")
    @Column(name = "new_student")
    private Integer newStudent;

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

    @RelationOneToOne(
            masterIdField = "schoolId",
            slaveServiceName = "sysDeptService",
            slaveModelClass = SysDept.class,
            slaveIdField = "deptId")
    @Transient
    private SysDept sysDept;

    @RelationOneToOne(
            masterIdField = "teacherId",
            slaveServiceName = "teacherService",
            slaveModelClass = Teacher.class,
            slaveIdField = "teacherId")
    @Transient
    private Teacher teacher;

    @RelationDict(
            masterIdField = "provinceId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> provinceIdDictMap;

    @RelationDict(
            masterIdField = "cityId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> cityIdDictMap;

    @RelationDict(
            masterIdField = "schoolId",
            slaveServiceName = "sysDeptService",
            equalOneToOneRelationField = "sysDept",
            slaveModelClass = SysDept.class,
            slaveIdField = "deptId",
            slaveNameField = "deptName")
    @Transient
    private Map<String, Object> schoolIdDictMap;

    @RelationDict(
            masterIdField = "teacherId",
            slaveServiceName = "teacherService",
            equalOneToOneRelationField = "teacher",
            slaveModelClass = Teacher.class,
            slaveIdField = "teacherId",
            slaveNameField = "teacherName")
    @Transient
    private Map<String, Object> teacherIdDictMap;
}
