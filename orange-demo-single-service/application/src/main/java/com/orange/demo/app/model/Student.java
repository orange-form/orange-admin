package com.orange.demo.app.model;

import com.orange.demo.application.common.constant.Gender;
import com.orange.demo.application.common.constant.ExpLevel;
import com.orange.demo.application.common.constant.StudentStatus;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.app.vo.StudentVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * Student实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_student")
public class Student {

    /**
     * 学生Id。
     */
    @NotNull(message = "数据验证失败，学生Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "student_id")
    private Long studentId;

    /**
     * 登录手机。
     */
    @NotBlank(message = "数据验证失败，手机号码不能为空！")
    @Column(name = "login_mobile")
    private String loginMobile;

    /**
     * 学生姓名。
     */
    @NotBlank(message = "数据验证失败，学生姓名不能为空！")
    @Column(name = "student_name")
    private String studentName;

    /**
     * 所在省份Id。
     */
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 区县Id。
     */
    @NotNull(message = "数据验证失败，所在区县不能为空！")
    @Column(name = "district_id")
    private Long districtId;

    /**
     * 学生性别 (0: 女生 1: 男生)。
     */
    @NotNull(message = "数据验证失败，学生性别不能为空！")
    @ConstDictRef(constDictClass = Gender.class, message = "数据验证失败，学生性别为无效值！")
    private Integer gender;

    /**
     * 生日。
     */
    @NotNull(message = "数据验证失败，出生日期不能为空！")
    private Date birthday;

    /**
     * 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。
     */
    @NotNull(message = "数据验证失败，经验等级不能为空！")
    @ConstDictRef(constDictClass = ExpLevel.class, message = "数据验证失败，经验等级为无效值！")
    @Column(name = "experience_level")
    private Integer experienceLevel;

    /**
     * 总共充值学币数量。
     */
    @NotNull(message = "数据验证失败，充值学币不能为空！", groups = {UpdateGroup.class})
    @Column(name = "total_coin")
    private Integer totalCoin;

    /**
     * 可用学币数量。
     */
    @NotNull(message = "数据验证失败，剩余学币不能为空！", groups = {UpdateGroup.class})
    @Column(name = "left_coin")
    private Integer leftCoin;

    /**
     * 年级Id。
     */
    @NotNull(message = "数据验证失败，年级不能为空！")
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 校区Id。
     */
    @NotNull(message = "数据验证失败，所属校区不能为空！")
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 注册时间。
     */
    @Column(name = "register_time")
    private Date registerTime;

    /**
     * 学生状态 (0: 正常 1: 锁定 2: 注销)。
     */
    @NotNull(message = "数据验证失败，学生状态不能为空！", groups = {UpdateGroup.class})
    @ConstDictRef(constDictClass = StudentStatus.class, message = "数据验证失败，学生状态为无效值！")
    private Integer status;

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
     * registerTime 范围过滤起始值(>=)。
     */
    @Transient
    private String registerTimeStart;

    /**
     * registerTime 范围过滤结束值(<=)。
     */
    @Transient
    private String registerTimeEnd;

    /**
     * login_mobile / student_name LIKE搜索字符串。
     */
    @Transient
    private String searchString;

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
            masterIdField = "districtId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> districtIdDictMap;

    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @Transient
    private Map<String, Object> gradeIdDictMap;

    @RelationDict(
            masterIdField = "schoolId",
            slaveServiceName = "schoolInfoService",
            slaveModelClass = SchoolInfo.class,
            slaveIdField = "schoolId",
            slaveNameField = "schoolName")
    @Transient
    private Map<String, Object> schoolIdDictMap;

    @RelationConstDict(
            masterIdField = "gender",
            constantDictClass = Gender.class)
    @Transient
    private Map<String, Object> genderDictMap;

    @RelationConstDict(
            masterIdField = "experienceLevel",
            constantDictClass = ExpLevel.class)
    @Transient
    private Map<String, Object> experienceLevelDictMap;

    @RelationConstDict(
            masterIdField = "status",
            constantDictClass = StudentStatus.class)
    @Transient
    private Map<String, Object> statusDictMap;

    @Mapper
    public interface StudentModelMapper extends BaseModelMapper<StudentVo, Student> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param studentVo 域对象。
         * @return 实体对象。
         */
        @Override
        Student toModel(StudentVo studentVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param student 实体对象。
         * @return 域对象。
         */
        @Override
        StudentVo fromModel(Student student);
    }
    public static final StudentModelMapper INSTANCE = Mappers.getMapper(StudentModelMapper.class);
}
