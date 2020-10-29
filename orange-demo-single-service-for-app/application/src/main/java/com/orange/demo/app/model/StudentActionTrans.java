package com.orange.demo.app.model;

import com.orange.demo.application.common.constant.StudentActionType;
import com.orange.demo.application.common.constant.DeviceType;
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
 * StudentActionTrans实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("StudentActionTrans实体对象")
@Data
@Table(name = "zz_student_action_trans")
public class StudentActionTrans {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "trans_id")
    private Long transId;

    /**
     * 学生Id。
     */
    @ApiModelProperty(value = "学生Id", required = true)
    @NotNull(message = "数据验证失败，学生Id不能为空！")
    @Column(name = "student_id")
    private Long studentId;

    /**
     * 学生名称。
     */
    @ApiModelProperty(value = "学生名称", required = true)
    @NotBlank(message = "数据验证失败，学生名称不能为空！")
    @Column(name = "student_name")
    private String studentName;

    /**
     * 学生校区。
     */
    @ApiModelProperty(value = "学生校区", required = true)
    @NotNull(message = "数据验证失败，学生校区不能为空！")
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id", required = true)
    @NotNull(message = "数据验证失败，学生年级不能为空！")
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。
     */
    @ApiModelProperty(value = "行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)", required = true)
    @NotNull(message = "数据验证失败，行为类型不能为空！")
    @ConstDictRef(constDictClass = StudentActionType.class, message = "数据验证失败，行为类型为无效值！")
    @Column(name = "action_type")
    private Integer actionType;

    /**
     * 设备类型(0: iOS 1: Android 2: PC)。
     */
    @ApiModelProperty(value = "设备类型(0: iOS 1: Android 2: PC)", required = true)
    @NotNull(message = "数据验证失败，设备类型不能为空！")
    @ConstDictRef(constDictClass = DeviceType.class, message = "数据验证失败，设备类型为无效值！")
    @Column(name = "device_type")
    private Integer deviceType;

    /**
     * 看视频秒数。
     */
    @ApiModelProperty(value = "看视频秒数")
    @Column(name = "watch_video_seconds")
    private Integer watchVideoSeconds;

    /**
     * 购买献花数量。
     */
    @ApiModelProperty(value = "购买献花数量")
    @Column(name = "flower_count")
    private Integer flowerCount;

    /**
     * 购买作业数量。
     */
    @ApiModelProperty(value = "购买作业数量")
    @Column(name = "paper_count")
    private Integer paperCount;

    /**
     * 购买视频数量。
     */
    @ApiModelProperty(value = "购买视频数量")
    @Column(name = "video_count")
    private Integer videoCount;

    /**
     * 购买课程数量。
     */
    @ApiModelProperty(value = "购买课程数量")
    @Column(name = "course_count")
    private Integer courseCount;

    /**
     * 充值学币数量。
     */
    @ApiModelProperty(value = "充值学币数量")
    @Column(name = "coin_count")
    private Integer coinCount;

    /**
     * 做题是否正确标记。
     */
    @ApiModelProperty(value = "做题是否正确标记")
    @Column(name = "exercise_correct_flag")
    private Integer exerciseCorrectFlag;

    /**
     * 发生时间。
     */
    @ApiModelProperty(value = "发生时间", required = true)
    @NotNull(message = "数据验证失败，发生时间不能为空！")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "createTime 范围过滤起始值(>=)")
    @Transient
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "createTime 范围过滤结束值(<=)")
    @Transient
    private String createTimeEnd;

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
    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @Transient
    private Map<String, Object> gradeIdDictMap;

    @ApiModelProperty(hidden = true)
    @RelationConstDict(
            masterIdField = "actionType",
            constantDictClass = StudentActionType.class)
    @Transient
    private Map<String, Object> actionTypeDictMap;

    @ApiModelProperty(hidden = true)
    @RelationConstDict(
            masterIdField = "deviceType",
            constantDictClass = DeviceType.class)
    @Transient
    private Map<String, Object> deviceTypeDictMap;
}
