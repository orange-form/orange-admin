package com.orange.demo.app.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.application.common.constant.StudentActionType;
import com.orange.demo.application.common.constant.DeviceType;

import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * StudentActionTransDto对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class StudentActionTransDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long transId;

    /**
     * 学生Id。
     */
    @NotNull(message = "数据验证失败，学生Id不能为空！")
    private Long studentId;

    /**
     * 学生名称。
     */
    @NotBlank(message = "数据验证失败，学生名称不能为空！")
    private String studentName;

    /**
     * 学生校区。
     */
    @NotNull(message = "数据验证失败，学生校区不能为空！")
    private Long schoolId;

    /**
     * 年级Id。
     */
    @NotNull(message = "数据验证失败，学生年级不能为空！")
    private Integer gradeId;

    /**
     * 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。
     */
    @NotNull(message = "数据验证失败，行为类型不能为空！")
    @ConstDictRef(constDictClass = StudentActionType.class, message = "数据验证失败，行为类型为无效值！")
    private Integer actionType;

    /**
     * 设备类型(0: iOS 1: Android 2: PC)。
     */
    @NotNull(message = "数据验证失败，设备类型不能为空！")
    @ConstDictRef(constDictClass = DeviceType.class, message = "数据验证失败，设备类型为无效值！")
    private Integer deviceType;

    /**
     * 看视频秒数。
     */
    private Integer watchVideoSeconds;

    /**
     * 购买献花数量。
     */
    private Integer flowerCount;

    /**
     * 购买作业数量。
     */
    private Integer paperCount;

    /**
     * 购买视频数量。
     */
    private Integer videoCount;

    /**
     * 购买课程数量。
     */
    private Integer courseCount;

    /**
     * 充值学币数量。
     */
    private Integer coinCount;

    /**
     * 做题是否正确标记。
     */
    private Integer exerciseCorrectFlag;

    /**
     * 发生时间。
     */
    @NotNull(message = "数据验证失败，发生时间不能为空！")
    private Date createTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    private String createTimeEnd;
}
