package com.orange.admin.app.model;

import com.orange.admin.upms.model.SysDept;
import com.orange.admin.upms.model.SysUser;
import com.orange.admin.app.model.constant.Gender;
import com.orange.admin.common.biz.constant.Subject;
import com.orange.admin.app.model.constant.TeacherLevelType;
import com.orange.admin.common.biz.constant.YesNo;
import com.orange.admin.common.core.annotation.RelationDict;
import com.orange.admin.common.core.annotation.RelationConstDict;
import com.orange.admin.common.core.annotation.RelationOneToOne;
import com.orange.admin.common.core.annotation.DeptFilterColumn;
import com.orange.admin.common.core.annotation.UserFilterColumn;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.validator.ConstDictRef;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * Teacher实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_teacher")
public class Teacher {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @UserFilterColumn
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * 教师名称。
     */
    @NotBlank(message = "数据验证失败，教师名称不能为空！")
    @Column(name = "teacher_name")
    private String teacherName;

    /**
     * 教师生日。
     */
    @NotNull(message = "数据验证失败，出生日期不能为空！")
    private Date birthday;

    /**
     * 教师性别(0: 女 1: 男)。
     */
    @NotNull(message = "数据验证失败，性别不能为空！")
    @ConstDictRef(constDictClass = Gender.class, message = "数据验证失败，性别为无效值！")
    private Integer gender;

    /**
     * 所教的科目Id。
     */
    @NotNull(message = "数据验证失败，所教科目不能为空！")
    @ConstDictRef(constDictClass = Subject.class, message = "数据验证失败，所教科目为无效值！")
    @Column(name = "subject_id")
    private Integer subjectId;

    /**
     * 教师职级(0: 初级 1: 中级 2: 高级)。
     */
    @NotNull(message = "数据验证失败，职级不能为空！")
    @ConstDictRef(constDictClass = TeacherLevelType.class, message = "数据验证失败，职级为无效值！")
    private Integer level;

    /**
     * 鲜花数量。
     */
    @Column(name = "flower_count")
    private Integer flowerCount;

    /**
     * 校区Id。
     */
    @NotNull(message = "数据验证失败，所属校区不能为空！")
    @DeptFilterColumn
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 用户Id。
     */
    @NotNull(message = "数据验证失败，绑定用户不能为空！")
    @Column(name = "user_id")
    private Long userId;

    /**
     * 入职时间。
     */
    @Column(name = "register_date")
    private Date registerDate;

    /**
     * 是否在职。
     */
    @NotNull(message = "数据验证失败，是否在职不能为空！")
    @ConstDictRef(constDictClass = YesNo.class, message = "数据验证失败，是否在职为无效值！")
    private Integer available;

    /**
     * birthday 范围过滤起始值(>=)。
     */
    @Transient
    private String birthdayStart;

    /**
     * birthday 范围过滤结束值(<=)。
     */
    @Transient
    private String birthdayEnd;

    /**
     * registerDate 范围过滤起始值(>=)。
     */
    @Transient
    private String registerDateStart;

    /**
     * registerDate 范围过滤结束值(<=)。
     */
    @Transient
    private String registerDateEnd;

    @RelationOneToOne(
            masterIdField = "schoolId",
            slaveServiceName = "sysDeptService",
            slaveModelClass = SysDept.class,
            slaveIdField = "deptId")
    @Transient
    private SysDept sysDept;

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
            masterIdField = "userId",
            slaveServiceName = "sysUserService",
            slaveModelClass = SysUser.class,
            slaveIdField = "userId",
            slaveNameField = "loginName")
    @Transient
    private Map<String, Object> userIdDictMap;

    @RelationConstDict(
            masterIdField = "gender",
            constantDictClass = Gender.class)
    @Transient
    private Map<String, Object> genderDictMap;

    @RelationConstDict(
            masterIdField = "subjectId",
            constantDictClass = Subject.class)
    @Transient
    private Map<String, Object> subjectIdDictMap;

    @RelationConstDict(
            masterIdField = "level",
            constantDictClass = TeacherLevelType.class)
    @Transient
    private Map<String, Object> levelDictMap;

    @RelationConstDict(
            masterIdField = "available",
            constantDictClass = YesNo.class)
    @Transient
    private Map<String, Object> availableDictMap;
}
