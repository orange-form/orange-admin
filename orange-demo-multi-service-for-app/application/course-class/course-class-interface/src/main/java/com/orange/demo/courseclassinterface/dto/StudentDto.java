package com.orange.demo.courseclassinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.application.common.constant.Gender;
import com.orange.demo.application.common.constant.ExpLevel;
import com.orange.demo.application.common.constant.StudentStatus;

import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * StudentDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class StudentDto {

    /**
     * 学生Id。
     */
    @NotNull(message = "数据验证失败，学生Id不能为空！", groups = {UpdateGroup.class})
    private Long studentId;

    /**
     * 登录手机。
     */
    @NotBlank(message = "数据验证失败，手机号码不能为空！")
    private String loginMobile;

    /**
     * 学生姓名。
     */
    @NotBlank(message = "数据验证失败，姓名不能为空！")
    private String studentName;

    /**
     * 所在省份Id。
     */
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    private Long cityId;

    /**
     * 区县Id。
     */
    @NotNull(message = "数据验证失败，所在区县不能为空！")
    private Long districtId;

    /**
     * 学生性别 (0: 女生 1: 男生)。
     */
    @NotNull(message = "数据验证失败，性别不能为空！")
    @ConstDictRef(constDictClass = Gender.class, message = "数据验证失败，性别为无效值！")
    private Integer gender;

    /**
     * 生日。
     */
    @NotNull(message = "数据验证失败，生日不能为空！")
    private Date birthday;

    /**
     * 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。
     */
    @NotNull(message = "数据验证失败，经验等级不能为空！")
    @ConstDictRef(constDictClass = ExpLevel.class, message = "数据验证失败，经验等级为无效值！")
    private Integer experienceLevel;

    /**
     * 总共充值学币数量。
     */
    @NotNull(message = "数据验证失败，充值学币不能为空！", groups = {UpdateGroup.class})
    private Integer totalCoin;

    /**
     * 可用学币数量。
     */
    @NotNull(message = "数据验证失败，剩余学币不能为空！", groups = {UpdateGroup.class})
    private Integer leftCoin;

    /**
     * 年级Id。
     */
    @NotNull(message = "数据验证失败，年级不能为空！")
    private Integer gradeId;

    /**
     * 校区Id。
     */
    @NotNull(message = "数据验证失败，所属校区不能为空！")
    private Long schoolId;

    /**
     * 注册时间。
     */
    private Date registerTime;

    /**
     * 学生状态 (0: 正常 1: 锁定 2: 注销)。
     */
    @NotNull(message = "数据验证失败，状态 不能为空！", groups = {UpdateGroup.class})
    @ConstDictRef(constDictClass = StudentStatus.class, message = "数据验证失败，状态 为无效值！")
    private Integer status;

    /**
     * birthday 范围过滤起始值(>=)。
     */
    private String birthdayStart;

    /**
     * birthday 范围过滤结束值(<=)。
     */
    private String birthdayEnd;

    /**
     * registerTime 范围过滤起始值(>=)。
     */
    private String registerTimeStart;

    /**
     * registerTime 范围过滤结束值(<=)。
     */
    private String registerTimeEnd;

    /**
     * true LIKE搜索字符串。
     */
    private String searchString;
}
