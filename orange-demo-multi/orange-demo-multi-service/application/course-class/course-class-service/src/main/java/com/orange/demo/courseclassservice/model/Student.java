package com.orange.demo.courseclassservice.model;

import com.orange.demo.courseclassapi.vo.StudentVo;
import com.orange.demo.application.common.constant.Gender;
import com.orange.demo.application.common.constant.ExpLevel;
import com.orange.demo.application.common.constant.StudentStatus;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.util.MyCommonUtil;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;

import java.util.Date;
import java.util.Map;

/**
 * Student实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_student")
public class Student {

    /**
     * 学生Id。
     */
    @Id
    @Column(name = "student_id")
    private Long studentId;

    /**
     * 登录手机。
     */
    @Column(name = "login_mobile")
    private String loginMobile;

    /**
     * 学生姓名。
     */
    @Column(name = "student_name")
    private String studentName;

    /**
     * 所在省份Id。
     */
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 区县Id。
     */
    @Column(name = "district_id")
    private Long districtId;

    /**
     * 学生性别 (0: 女生 1: 男生)。
     */
    private Integer gender;

    /**
     * 生日。
     */
    private Date birthday;

    /**
     * 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。
     */
    @Column(name = "experience_level")
    private Integer experienceLevel;

    /**
     * 总共充值学币数量。
     */
    @Column(name = "total_coin")
    private Integer totalCoin;

    /**
     * 可用学币数量。
     */
    @Column(name = "left_coin")
    private Integer leftCoin;

    /**
     * 年级Id。
     */
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 校区Id。
     */
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
     * true LIKE搜索字符串。
     */
    @Transient
    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = MyCommonUtil.replaceSqlWildcard(searchString);
    }

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
