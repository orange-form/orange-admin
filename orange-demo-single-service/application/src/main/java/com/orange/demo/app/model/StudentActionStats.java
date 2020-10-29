package com.orange.demo.app.model;

import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * StudentActionStats实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("StudentActionStats实体对象")
@Data
@Table(name = "zz_student_action_stats")
public class StudentActionStats {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "stats_id")
    private Long statsId;

    /**
     * 统计日期。
     */
    @ApiModelProperty(value = "统计日期", required = true)
    @NotNull(message = "数据验证失败，统计日期不能为空！")
    @Column(name = "stats_date")
    private Date statsDate;

    /**
     * 统计小时。
     */
    @ApiModelProperty(value = "统计小时")
    @Column(name = "stats_month")
    private Date statsMonth;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id", required = true)
    @NotNull(message = "数据验证失败，所属年级不能为空！")
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 学生所在省Id。
     */
    @ApiModelProperty(value = "学生所在省Id", required = true)
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 学生所在城市Id。
     */
    @ApiModelProperty(value = "学生所在城市Id", required = true)
    @NotNull(message = "数据验证失败，所在城市不能为空！", groups = {UpdateGroup.class})
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 购课学币数量。
     */
    @ApiModelProperty(value = "购课学币数量", required = true)
    @NotNull(message = "数据验证失败，购课学币数量不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_course_amount")
    private Integer buyCourseAmount;

    /**
     * 购买课程次数。
     */
    @ApiModelProperty(value = "购买课程次数", required = true)
    @NotNull(message = "数据验证失败，购买课程次数不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_course_count")
    private Integer buyCourseCount;

    /**
     * 购买视频学币数量。
     */
    @ApiModelProperty(value = "购买视频学币数量", required = true)
    @NotNull(message = "数据验证失败，购买视频学币数量不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_video_amount")
    private Integer buyVideoAmount;

    /**
     * 购买视频次数。
     */
    @ApiModelProperty(value = "购买视频次数", required = true)
    @NotNull(message = "数据验证失败，购买视频次数不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_video_count")
    private Integer buyVideoCount;

    /**
     * 购买作业学币数量。
     */
    @ApiModelProperty(value = "购买作业学币数量", required = true)
    @NotNull(message = "数据验证失败，购买作业学币数量不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_paper_amount")
    private Integer buyPaperAmount;

    /**
     * 购买作业次数。
     */
    @ApiModelProperty(value = "购买作业次数", required = true)
    @NotNull(message = "数据验证失败，购买作业次数不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_paper_count")
    private Integer buyPaperCount;

    /**
     * 购买献花数量。
     */
    @ApiModelProperty(value = "购买献花数量", required = true)
    @NotNull(message = "数据验证失败，购买献花数量不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_flower_amount")
    private Integer buyFlowerAmount;

    /**
     * 购买献花次数。
     */
    @ApiModelProperty(value = "购买献花次数", required = true)
    @NotNull(message = "数据验证失败，购买献花次数不能为空！", groups = {UpdateGroup.class})
    @Column(name = "buy_flower_count")
    private Integer buyFlowerCount;

    /**
     * 充值学币数量。
     */
    @ApiModelProperty(value = "充值学币数量", required = true)
    @NotNull(message = "数据验证失败，充值学币数量不能为空！", groups = {UpdateGroup.class})
    @Column(name = "recharge_coin_amount")
    private Integer rechargeCoinAmount;

    /**
     * 充值学币次数。
     */
    @ApiModelProperty(value = "充值学币次数", required = true)
    @NotNull(message = "数据验证失败，充值学币次数不能为空！", groups = {UpdateGroup.class})
    @Column(name = "recharge_coin_count")
    private Integer rechargeCoinCount;

    /**
     * 线下课程上课次数。
     */
    @ApiModelProperty(value = "线下课程上课次数", required = true)
    @NotNull(message = "数据验证失败，线下课程上课次数不能为空！")
    @Column(name = "do_course_count")
    private Integer doCourseCount;

    /**
     * 观看视频次数。
     */
    @ApiModelProperty(value = "观看视频次数", required = true)
    @NotNull(message = "数据验证失败，观看视频次数不能为空！", groups = {UpdateGroup.class})
    @Column(name = "watch_video_count")
    private Integer watchVideoCount;

    /**
     * 购买献花消费学币数量。
     */
    @ApiModelProperty(value = "购买献花消费学币数量", required = true)
    @NotNull(message = "数据验证失败，购买献花消费学币数量不能为空！")
    @Column(name = "watch_video_total_second")
    private Integer watchVideoTotalSecond;

    /**
     * 做题数量。
     */
    @ApiModelProperty(value = "做题数量", required = true)
    @NotNull(message = "数据验证失败，做题数量不能为空！", groups = {UpdateGroup.class})
    @Column(name = "do_exercise_count")
    private Integer doExerciseCount;

    /**
     * 做题正确的数量。
     */
    @ApiModelProperty(value = "做题正确的数量", required = true)
    @NotNull(message = "数据验证失败，做题正确的数量不能为空！", groups = {UpdateGroup.class})
    @Column(name = "do_exercise_correct_count")
    private Integer doExerciseCorrectCount;

    /**
     * statsDate 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "statsDate 范围过滤起始值(>=)")
    @Transient
    private String statsDateStart;

    /**
     * statsDate 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "statsDate 范围过滤结束值(<=)")
    @Transient
    private String statsDateEnd;

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
}
