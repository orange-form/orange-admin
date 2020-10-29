package com.orange.demo.app.model;

import com.orange.demo.application.common.constant.Gender;
import com.orange.demo.application.common.constant.ExpLevel;
import com.orange.demo.application.common.constant.StudentStatus;
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
 * Student实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("Student实体对象")
@Data
@Table(name = "zz_student")
public class Student {

    /**
     * 学生Id。
     */
    @ApiModelProperty(value = "学生Id", required = true)
    @NotNull(message = "数据验证失败，学生Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "student_id")
    private Long studentId;

    /**
     * 登录手机。
     */
    @ApiModelProperty(value = "登录手机", required = true)
    @NotBlank(message = "数据验证失败，手机号码不能为空！")
    @Column(name = "login_mobile")
    private String loginMobile;

    /**
     * 学生姓名。
     */
    @ApiModelProperty(value = "学生姓名", required = true)
    @NotBlank(message = "数据验证失败，学生姓名不能为空！")
    @Column(name = "student_name")
    private String studentName;

    /**
     * 所在省份Id。
     */
    @ApiModelProperty(value = "所在省份Id", required = true)
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @ApiModelProperty(value = "所在城市Id", required = true)
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 区县Id。
     */
    @ApiModelProperty(value = "区县Id", required = true)
    @NotNull(message = "数据验证失败，所在区县不能为空！")
    @Column(name = "district_id")
    private Long districtId;

    /**
     * 学生性别 (0: 女生 1: 男生)。
     */
    @ApiModelProperty(value = "学生性别 (0: 女生 1: 男生)", required = true)
    @NotNull(message = "数据验证失败，学生性别不能为空！")
    @ConstDictRef(constDictClass = Gender.class, message = "数据验证失败，学生性别为无效值！")
    private Integer gender;

    /**
     * 生日。
     */
    @ApiModelProperty(value = "生日", required = true)
    @NotNull(message = "数据验证失败，出生日期不能为空！")
    private Date birthday;

    /**
     * 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。
     */
    @ApiModelProperty(value = "经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)", required = true)
    @NotNull(message = "数据验证失败，经验等级不能为空！")
    @ConstDictRef(constDictClass = ExpLevel.class, message = "数据验证失败，经验等级为无效值！")
    @Column(name = "experience_level")
    private Integer experienceLevel;

    /**
     * 总共充值学币数量。
     */
    @ApiModelProperty(value = "总共充值学币数量", required = true)
    @NotNull(message = "数据验证失败，充值学币不能为空！", groups = {UpdateGroup.class})
    @Column(name = "total_coin")
    private Integer totalCoin;

    /**
     * 可用学币数量。
     */
    @ApiModelProperty(value = "可用学币数量", required = true)
    @NotNull(message = "数据验证失败，剩余学币不能为空！", groups = {UpdateGroup.class})
    @Column(name = "left_coin")
    private Integer leftCoin;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id", required = true)
    @NotNull(message = "数据验证失败，年级不能为空！")
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 校区Id。
     */
    @ApiModelProperty(value = "校区Id", required = true)
    @NotNull(message = "数据验证失败，所属校区不能为空！")
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 注册时间。
     */
    @ApiModelProperty(value = "注册时间")
    @Column(name = "register_time")
    private Date registerTime;

    /**
     * 学生状态 (0: 正常 1: 锁定 2: 注销)。
     */
    @ApiModelProperty(value = "学生状态 (0: 正常 1: 锁定 2: 注销)", required = true)
    @NotNull(message = "数据验证失败，学生状态不能为空！", groups = {UpdateGroup.class})
    @ConstDictRef(constDictClass = StudentStatus.class, message = "数据验证失败，学生状态为无效值！")
    private Integer status;

    /**
     * birthday 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "birthday 范围过滤起始值(>=)")
    @Transient
    private String birthdayStart;

    /**
     * birthday 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "birthday 范围过滤结束值(<=)")
    @Transient
    private String birthdayEnd;

    /**
     * registerTime 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "registerTime 范围过滤起始值(>=)")
    @Transient
    private String registerTimeStart;

    /**
     * registerTime 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "registerTime 范围过滤结束值(<=)")
    @Transient
    private String registerTimeEnd;

    /**
     * login_mobile / student_name LIKE搜索字符串。
     */
    @ApiModelProperty(value = "LIKE模糊搜索字符串")
    @Transient
    private String searchString;

    @ApiModelProperty(hidden = true)
    @RelationDict(
            masterIdField = "provinceId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> provinceIdDictMap;

    @ApiModelProperty(hidden = true)
    @RelationDict(
            masterIdField = "cityId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> cityIdDictMap;

    @ApiModelProperty(hidden = true)
    @RelationDict(
            masterIdField = "districtId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> districtIdDictMap;

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
    @RelationDict(
            masterIdField = "schoolId",
            slaveServiceName = "schoolInfoService",
            slaveModelClass = SchoolInfo.class,
            slaveIdField = "schoolId",
            slaveNameField = "schoolName")
    @Transient
    private Map<String, Object> schoolIdDictMap;

    @ApiModelProperty(hidden = true)
    @RelationConstDict(
            masterIdField = "gender",
            constantDictClass = Gender.class)
    @Transient
    private Map<String, Object> genderDictMap;

    @ApiModelProperty(hidden = true)
    @RelationConstDict(
            masterIdField = "experienceLevel",
            constantDictClass = ExpLevel.class)
    @Transient
    private Map<String, Object> experienceLevelDictMap;

    @ApiModelProperty(hidden = true)
    @RelationConstDict(
            masterIdField = "status",
            constantDictClass = StudentStatus.class)
    @Transient
    private Map<String, Object> statusDictMap;
}
