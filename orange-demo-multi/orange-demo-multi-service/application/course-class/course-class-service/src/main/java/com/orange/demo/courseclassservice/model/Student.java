package com.orange.demo.courseclassservice.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orange.demo.courseclassapi.vo.StudentVo;
import com.orange.demo.upmsapi.client.SysDeptClient;
import com.orange.demo.upmsapi.vo.SysDeptVo;
import com.orange.demo.application.common.constant.Gender;
import com.orange.demo.application.common.constant.ExpLevel;
import com.orange.demo.application.common.constant.StudentStatus;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.annotation.DeptFilterColumn;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * Student实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@TableName(value = "zz_student")
public class Student {

    /**
     * 学生Id。
     */
    @TableId(value = "student_id")
    private Long studentId;

    /**
     * 登录手机。
     */
    @TableField(value = "login_mobile")
    private String loginMobile;

    /**
     * 学生姓名。
     */
    @TableField(value = "student_name")
    private String studentName;

    /**
     * 所在省份Id。
     */
    @TableField(value = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @TableField(value = "city_id")
    private Long cityId;

    /**
     * 区县Id。
     */
    @TableField(value = "district_id")
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
    @TableField(value = "experience_level")
    private Integer experienceLevel;

    /**
     * 总共充值学币数量。
     */
    @TableField(value = "total_coin")
    private Integer totalCoin;

    /**
     * 可用学币数量。
     */
    @TableField(value = "left_coin")
    private Integer leftCoin;

    /**
     * 年级Id。
     */
    @TableField(value = "grade_id")
    private Integer gradeId;

    /**
     * 校区Id。
     */
    @DeptFilterColumn
    @TableField(value = "school_id")
    private Long schoolId;

    /**
     * 注册时间。
     */
    @TableField(value = "register_time")
    private Date registerTime;

    /**
     * 学生状态 (0: 正常 1: 锁定 2: 注销)。
     */
    private Integer status;

    /**
     * birthday 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String birthdayStart;

    /**
     * birthday 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String birthdayEnd;

    /**
     * registerTime 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String registerTimeStart;

    /**
     * registerTime 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String registerTimeEnd;

    /**
     * true LIKE搜索字符串。
     */
    @TableField(exist = false)
    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = MyCommonUtil.replaceSqlWildcard(searchString);
    }

    @RelationDict(
            masterIdField = "schoolId",
            slaveClientClass = SysDeptClient.class,
            slaveModelClass = SysDeptVo.class,
            slaveIdField = "deptId",
            slaveNameField = "deptName")
    @TableField(exist = false)
    private Map<String, Object> schoolIdDictMap;

    @RelationDict(
            masterIdField = "provinceId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @TableField(exist = false)
    private Map<String, Object> provinceIdDictMap;

    @RelationDict(
            masterIdField = "cityId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @TableField(exist = false)
    private Map<String, Object> cityIdDictMap;

    @RelationDict(
            masterIdField = "districtId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @TableField(exist = false)
    private Map<String, Object> districtIdDictMap;

    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @TableField(exist = false)
    private Map<String, Object> gradeIdDictMap;

    @RelationConstDict(
            masterIdField = "gender",
            constantDictClass = Gender.class)
    @TableField(exist = false)
    private Map<String, Object> genderDictMap;

    @RelationConstDict(
            masterIdField = "experienceLevel",
            constantDictClass = ExpLevel.class)
    @TableField(exist = false)
    private Map<String, Object> experienceLevelDictMap;

    @RelationConstDict(
            masterIdField = "status",
            constantDictClass = StudentStatus.class)
    @TableField(exist = false)
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
