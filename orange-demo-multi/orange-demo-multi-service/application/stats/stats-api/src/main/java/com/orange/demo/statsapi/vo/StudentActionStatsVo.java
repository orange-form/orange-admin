package com.orange.demo.statsapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * StudentActionStatsVO视图对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("StudentActionStatsVO视图对象")
@Data
public class StudentActionStatsVo {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    private Long statsId;

    /**
     * 统计日期。
     */
    @ApiModelProperty(value = "统计日期")
    private Date statsDate;

    /**
     * 统计小时。
     */
    @ApiModelProperty(value = "统计小时")
    private Date statsMonth;

    /**
     * 年级Id。
     */
    @ApiModelProperty(value = "年级Id")
    private Integer gradeId;

    /**
     * 学生所在省Id。
     */
    @ApiModelProperty(value = "学生所在省Id")
    private Long provinceId;

    /**
     * 学生所在城市Id。
     */
    @ApiModelProperty(value = "学生所在城市Id")
    private Long cityId;

    /**
     * 购课学币数量。
     */
    @ApiModelProperty(value = "购课学币数量")
    private Integer buyCourseAmount;

    /**
     * 购买课程次数。
     */
    @ApiModelProperty(value = "购买课程次数")
    private Integer buyCourseCount;

    /**
     * 购买视频学币数量。
     */
    @ApiModelProperty(value = "购买视频学币数量")
    private Integer buyVideoAmount;

    /**
     * 购买视频次数。
     */
    @ApiModelProperty(value = "购买视频次数")
    private Integer buyVideoCount;

    /**
     * 购买作业学币数量。
     */
    @ApiModelProperty(value = "购买作业学币数量")
    private Integer buyPaperAmount;

    /**
     * 购买作业次数。
     */
    @ApiModelProperty(value = "购买作业次数")
    private Integer buyPaperCount;

    /**
     * 购买献花数量。
     */
    @ApiModelProperty(value = "购买献花数量")
    private Integer buyFlowerAmount;

    /**
     * 购买献花次数。
     */
    @ApiModelProperty(value = "购买献花次数")
    private Integer buyFlowerCount;

    /**
     * 充值学币数量。
     */
    @ApiModelProperty(value = "充值学币数量")
    private Integer rechargeCoinAmount;

    /**
     * 充值学币次数。
     */
    @ApiModelProperty(value = "充值学币次数")
    private Integer rechargeCoinCount;

    /**
     * 线下课程上课次数。
     */
    @ApiModelProperty(value = "线下课程上课次数")
    private Integer doCourseCount;

    /**
     * 观看视频次数。
     */
    @ApiModelProperty(value = "观看视频次数")
    private Integer watchVideoCount;

    /**
     * 购买献花消费学币数量。
     */
    @ApiModelProperty(value = "购买献花消费学币数量")
    private Integer watchVideoTotalSecond;

    /**
     * 做题数量。
     */
    @ApiModelProperty(value = "做题数量")
    private Integer doExerciseCount;

    /**
     * 做题正确的数量。
     */
    @ApiModelProperty(value = "做题正确的数量")
    private Integer doExerciseCorrectCount;

    /**
     * gradeId 字典关联数据。
     */
    @ApiModelProperty(value = "gradeId 字典关联数据")
    private Map<String, Object> gradeIdDictMap;

    /**
     * provinceId 字典关联数据。
     */
    @ApiModelProperty(value = "provinceId 字典关联数据")
    private Map<String, Object> provinceIdDictMap;

    /**
     * cityId 字典关联数据。
     */
    @ApiModelProperty(value = "cityId 字典关联数据")
    private Map<String, Object> cityIdDictMap;
}
