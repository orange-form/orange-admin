package com.orange.demo.statsinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * StudentActionStatsDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("StudentActionStatsDto实体对象")
@Data
public class StudentActionStatsDto {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long statsId;

    /**
     * 统计日期。
     */
    @ApiModelProperty(value = "统计日期", required = true)
    @NotNull(message = "数据验证失败，统计日期不能为空！")
    private Date statsDate;

    /**
     * 统计小时。
     */
    @ApiModelProperty(value = "统计小时")
    private Date statsMonth;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id", required = true)
    @NotNull(message = "数据验证失败，所属年级不能为空！")
    private Integer gradeId;

    /**
     * 学生所在省Id。
     */
    @ApiModelProperty(value = "学生所在省Id", required = true)
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    private Long provinceId;

    /**
     * 学生所在城市Id。
     */
    @ApiModelProperty(value = "学生所在城市Id", required = true)
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    private Long cityId;

    /**
     * 购课学币数量。
     */
    @ApiModelProperty(value = "购课学币数量", required = true)
    @NotNull(message = "数据验证失败，购课学币数量不能为空！")
    private Integer buyCourseAmount;

    /**
     * 购买课程次数。
     */
    @ApiModelProperty(value = "购买课程次数", required = true)
    @NotNull(message = "数据验证失败，购买课程次数不能为空！")
    private Integer buyCourseCount;

    /**
     * 购买视频学币数量。
     */
    @ApiModelProperty(value = "购买视频学币数量", required = true)
    @NotNull(message = "数据验证失败，购买视频学币数量不能为空！")
    private Integer buyVideoAmount;

    /**
     * 购买视频次数。
     */
    @ApiModelProperty(value = "购买视频次数", required = true)
    @NotNull(message = "数据验证失败，购买视频次数不能为空！")
    private Integer buyVideoCount;

    /**
     * 购买作业学币数量。
     */
    @ApiModelProperty(value = "购买作业学币数量", required = true)
    @NotNull(message = "数据验证失败，购买作业学币数量不能为空！")
    private Integer buyPaperAmount;

    /**
     * 购买作业次数。
     */
    @ApiModelProperty(value = "购买作业次数", required = true)
    @NotNull(message = "数据验证失败，购买作业次数不能为空！")
    private Integer buyPaperCount;

    /**
     * 购买献花数量。
     */
    @ApiModelProperty(value = "购买献花数量", required = true)
    @NotNull(message = "数据验证失败，购买献花数量不能为空！")
    private Integer buyFlowerAmount;

    /**
     * 购买献花次数。
     */
    @ApiModelProperty(value = "购买献花次数", required = true)
    @NotNull(message = "数据验证失败，购买献花次数不能为空！")
    private Integer buyFlowerCount;

    /**
     * 充值学币数量。
     */
    @ApiModelProperty(value = "充值学币数量", required = true)
    @NotNull(message = "数据验证失败，充值学币数量不能为空！")
    private Integer rechargeCoinAmount;

    /**
     * 充值学币次数。
     */
    @ApiModelProperty(value = "充值学币次数", required = true)
    @NotNull(message = "数据验证失败，充值学币次数不能为空！")
    private Integer rechargeCoinCount;

    /**
     * 线下课程上课次数。
     */
    @ApiModelProperty(value = "线下课程上课次数", required = true)
    @NotNull(message = "数据验证失败，线下课程上课次数不能为空！")
    private Integer doCourseCount;

    /**
     * 观看视频次数。
     */
    @ApiModelProperty(value = "观看视频次数", required = true)
    @NotNull(message = "数据验证失败，观看视频次数不能为空！")
    private Integer watchVideoCount;

    /**
     * 购买献花消费学币数量。
     */
    @ApiModelProperty(value = "购买献花消费学币数量", required = true)
    @NotNull(message = "数据验证失败，购买献花消费学币数量不能为空！")
    private Integer watchVideoTotalSecond;

    /**
     * 做题数量。
     */
    @ApiModelProperty(value = "做题数量", required = true)
    @NotNull(message = "数据验证失败，做题数量不能为空！")
    private Integer doExerciseCount;

    /**
     * 做题正确的数量。
     */
    @ApiModelProperty(value = "做题正确的数量", required = true)
    @NotNull(message = "数据验证失败，做题正确的数量不能为空！")
    private Integer doExerciseCorrectCount;

    /**
     * statsDate 范围过滤起始值(>=)。
     */
    @ApiModelProperty(value = "statsDate 范围过滤起始值(>=)")
    private String statsDateStart;

    /**
     * statsDate 范围过滤结束值(<=)。
     */
    @ApiModelProperty(value = "statsDate 范围过滤结束值(<=)")
    private String statsDateEnd;

    /**
     * gradeId 字典关联数据。
     */
    @ApiModelProperty(hidden = true)
    private Map<String, Object> gradeIdDictMap;

    /**
     * provinceId 字典关联数据。
     */
    @ApiModelProperty(hidden = true)
    private Map<String, Object> provinceIdDictMap;

    /**
     * cityId 字典关联数据。
     */
    @ApiModelProperty(hidden = true)
    private Map<String, Object> cityIdDictMap;
}
