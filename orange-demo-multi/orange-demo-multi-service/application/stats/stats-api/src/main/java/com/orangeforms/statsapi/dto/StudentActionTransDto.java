package com.orangeforms.statsapi.dto;

import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.application.common.constant.StudentActionType;
import com.orangeforms.application.common.constant.DeviceType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * StudentActionTransDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("StudentActionTransDto对象")
@Data
public class StudentActionTransDto {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long transId;

    /**
     * 学生Id。
     */
    @ApiModelProperty(value = "学生Id", required = true)
    @NotNull(message = "数据验证失败，学生Id不能为空！")
    private Long studentId;

    /**
     * 学生名称。
     */
    @ApiModelProperty(value = "学生名称", required = true)
    @NotBlank(message = "数据验证失败，学生名称不能为空！")
    private String studentName;

    /**
     * 学生校区。
     */
    @ApiModelProperty(value = "学生校区", required = true)
    @NotNull(message = "数据验证失败，学生校区不能为空！")
    private Long schoolId;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id", required = true)
    @NotNull(message = "数据验证失败，所属年级不能为空！")
    private Integer gradeId;

    /**
     * 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。
     */
    @ApiModelProperty(value = "行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)", required = true)
    @NotNull(message = "数据验证失败，行为类型不能为空！")
    @ConstDictRef(constDictClass = StudentActionType.class, message = "数据验证失败，行为类型为无效值！")
    private Integer actionType;

    /**
     * 设备类型(0: iOS 1: Android 2: PC)。
     */
    @ApiModelProperty(value = "设备类型(0: iOS 1: Android 2: PC)", required = true)
    @NotNull(message = "数据验证失败，设备类型不能为空！")
    @ConstDictRef(constDictClass = DeviceType.class, message = "数据验证失败，设备类型为无效值！")
    private Integer deviceType;

    /**
     * 看视频秒数。
     */
    @ApiModelProperty(value = "看视频秒数")
    private Integer watchVideoSeconds;

    /**
     * 购买献花数量。
     */
    @ApiModelProperty(value = "购买献花数量")
    private Integer flowerCount;

    /**
     * 购买作业数量。
     */
    @ApiModelProperty(value = "购买作业数量")
    private Integer paperCount;

    /**
     * 购买视频数量。
     */
    @ApiModelProperty(value = "购买视频数量")
    private Integer videoCount;

    /**
     * 购买课程数量。
     */
    @ApiModelProperty(value = "购买课程数量")
    private Integer courseCount;

    /**
     * 充值学币数量。
     */
    @ApiModelProperty(value = "充值学币数量")
    private Integer coinCount;

    /**
     * 做题是否正确标记。
     */
    @ApiModelProperty(value = "做题是否正确标记")
    private Integer exerciseCorrectFlag;

    /**
     * 发生时间。
     */
    @ApiModelProperty(value = "发生时间")
    private Date createTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "createTime 范围过滤起始值(>=)")
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "createTime 范围过滤结束值(<=)")
    private String createTimeEnd;
}
