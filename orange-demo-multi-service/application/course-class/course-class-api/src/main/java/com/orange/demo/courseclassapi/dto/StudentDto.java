package com.orange.demo.courseclassapi.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.application.common.constant.Gender;
import com.orange.demo.application.common.constant.ExpLevel;
import com.orange.demo.application.common.constant.StudentStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * StudentDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("StudentDto对象")
@Data
public class StudentDto {

    /**
     * 学生Id。
     */
    @ApiModelProperty(value = "学生Id", required = true)
    @NotNull(message = "数据验证失败，学生Id不能为空！", groups = {UpdateGroup.class})
    private Long studentId;

    /**
     * 登录手机。
     */
    @ApiModelProperty(value = "登录手机", required = true)
    @NotBlank(message = "数据验证失败，手机号码不能为空！")
    private String loginMobile;

    /**
     * 学生姓名。
     */
    @ApiModelProperty(value = "学生姓名", required = true)
    @NotBlank(message = "数据验证失败，姓名不能为空！")
    private String studentName;

    /**
     * 所在省份Id。
     */
    @ApiModelProperty(value = "所在省份Id", required = true)
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @ApiModelProperty(value = "所在城市Id", required = true)
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    private Long cityId;

    /**
     * 区县Id。
     */
    @ApiModelProperty(value = "区县Id", required = true)
    @NotNull(message = "数据验证失败，所在区县不能为空！")
    private Long districtId;

    /**
     * 学生性别 (0: 女生 1: 男生)。
     */
    @ApiModelProperty(value = "学生性别 (0: 女生 1: 男生)", required = true)
    @NotNull(message = "数据验证失败，性别不能为空！")
    @ConstDictRef(constDictClass = Gender.class, message = "数据验证失败，性别为无效值！")
    private Integer gender;

    /**
     * 生日。
     */
    @ApiModelProperty(value = "生日", required = true)
    @NotNull(message = "数据验证失败，生日不能为空！")
    private Date birthday;

    /**
     * 经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)。
     */
    @ApiModelProperty(value = "经验等级 (0: 初级 1: 中级 2: 高级 3: 资深)", required = true)
    @NotNull(message = "数据验证失败，经验等级不能为空！")
    @ConstDictRef(constDictClass = ExpLevel.class, message = "数据验证失败，经验等级为无效值！")
    private Integer experienceLevel;

    /**
     * 总共充值学币数量。
     */
    @ApiModelProperty(value = "总共充值学币数量", required = true)
    @NotNull(message = "数据验证失败，充值学币不能为空！", groups = {UpdateGroup.class})
    private Integer totalCoin;

    /**
     * 可用学币数量。
     */
    @ApiModelProperty(value = "可用学币数量", required = true)
    @NotNull(message = "数据验证失败，剩余学币不能为空！", groups = {UpdateGroup.class})
    private Integer leftCoin;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id", required = true)
    @NotNull(message = "数据验证失败，年级不能为空！")
    private Integer gradeId;

    /**
     * 校区Id。
     */
    @ApiModelProperty(value = "校区Id", required = true)
    @NotNull(message = "数据验证失败，所属校区不能为空！")
    private Long schoolId;

    /**
     * 注册时间。
     */
    @ApiModelProperty(value = "注册时间")
    private Date registerTime;

    /**
     * 学生状态 (0: 正常 1: 锁定 2: 注销)。
     */
    @ApiModelProperty(value = "学生状态 (0: 正常 1: 锁定 2: 注销)", required = true)
    @NotNull(message = "数据验证失败，状态 不能为空！", groups = {UpdateGroup.class})
    @ConstDictRef(constDictClass = StudentStatus.class, message = "数据验证失败，状态 为无效值！")
    private Integer status;

    /**
     * birthday 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "birthday 范围过滤起始值(>=)")
    private String birthdayStart;

    /**
     * birthday 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "birthday 范围过滤结束值(<=)")
    private String birthdayEnd;

    /**
     * registerTime 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "registerTime 范围过滤起始值(>=)")
    private String registerTimeStart;

    /**
     * registerTime 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "registerTime 范围过滤结束值(<=)")
    private String registerTimeEnd;

    /**
     * true LIKE搜索字符串。
     */
    @ApiModelProperty(value = "LIKE模糊搜索字符串")
    private String searchString;
}
