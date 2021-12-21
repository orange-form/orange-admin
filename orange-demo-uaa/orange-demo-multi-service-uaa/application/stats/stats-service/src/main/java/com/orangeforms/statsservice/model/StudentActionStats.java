package com.orangeforms.statsservice.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.statsapi.vo.StudentActionStatsVo;
import com.orangeforms.courseclassapi.vo.AreaCodeVo;
import com.orangeforms.courseclassapi.vo.GradeVo;
import com.orangeforms.courseclassapi.client.AreaCodeClient;
import com.orangeforms.courseclassapi.client.GradeClient;
import com.orangeforms.common.core.annotation.*;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * StudentActionStats实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@TableName(value = "zz_student_action_stats")
public class StudentActionStats {

    /**
     * 主键Id。
     */
    @TableId(value = "stats_id")
    private Long statsId;

    /**
     * 统计日期。
     */
    @TableField(value = "stats_date")
    private Date statsDate;

    /**
     * 统计小时。
     */
    @TableField(value = "stats_month")
    private Date statsMonth;

    /**
     * 年级Id。
     */
    @TableField(value = "grade_id")
    private Integer gradeId;

    /**
     * 学生所在省Id。
     */
    @TableField(value = "province_id")
    private Long provinceId;

    /**
     * 学生所在城市Id。
     */
    @TableField(value = "city_id")
    private Long cityId;

    /**
     * 购课学币数量。
     */
    @TableField(value = "buy_course_amount")
    private Integer buyCourseAmount;

    /**
     * 购买课程次数。
     */
    @TableField(value = "buy_course_count")
    private Integer buyCourseCount;

    /**
     * 购买视频学币数量。
     */
    @TableField(value = "buy_video_amount")
    private Integer buyVideoAmount;

    /**
     * 购买视频次数。
     */
    @TableField(value = "buy_video_count")
    private Integer buyVideoCount;

    /**
     * 购买作业学币数量。
     */
    @TableField(value = "buy_paper_amount")
    private Integer buyPaperAmount;

    /**
     * 购买作业次数。
     */
    @TableField(value = "buy_paper_count")
    private Integer buyPaperCount;

    /**
     * 购买献花数量。
     */
    @TableField(value = "buy_flower_amount")
    private Integer buyFlowerAmount;

    /**
     * 购买献花次数。
     */
    @TableField(value = "buy_flower_count")
    private Integer buyFlowerCount;

    /**
     * 充值学币数量。
     */
    @TableField(value = "recharge_coin_amount")
    private Integer rechargeCoinAmount;

    /**
     * 充值学币次数。
     */
    @TableField(value = "recharge_coin_count")
    private Integer rechargeCoinCount;

    /**
     * 线下课程上课次数。
     */
    @TableField(value = "do_course_count")
    private Integer doCourseCount;

    /**
     * 观看视频次数。
     */
    @TableField(value = "watch_video_count")
    private Integer watchVideoCount;

    /**
     * 购买献花消费学币数量。
     */
    @TableField(value = "watch_video_total_second")
    private Integer watchVideoTotalSecond;

    /**
     * 做题数量。
     */
    @TableField(value = "do_exercise_count")
    private Integer doExerciseCount;

    /**
     * 做题正确的数量。
     */
    @TableField(value = "do_exercise_correct_count")
    private Integer doExerciseCorrectCount;

    /**
     * statsDate 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String statsDateStart;

    /**
     * statsDate 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String statsDateEnd;

    @RelationDict(
            masterIdField = "gradeId",
            slaveClientClass = GradeClient.class,
            slaveModelClass = GradeVo.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @TableField(exist = false)
    private Map<String, Object> gradeIdDictMap;

    @RelationDict(
            masterIdField = "provinceId",
            slaveClientClass = AreaCodeClient.class,
            slaveModelClass = AreaCodeVo.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @TableField(exist = false)
    private Map<String, Object> provinceIdDictMap;

    @RelationDict(
            masterIdField = "cityId",
            slaveClientClass = AreaCodeClient.class,
            slaveModelClass = AreaCodeVo.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @TableField(exist = false)
    private Map<String, Object> cityIdDictMap;

    @Mapper
    public interface StudentActionStatsModelMapper extends BaseModelMapper<StudentActionStatsVo, StudentActionStats> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param studentActionStatsVo 域对象。
         * @return 实体对象。
         */
        @Override
        StudentActionStats toModel(StudentActionStatsVo studentActionStatsVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param studentActionStats 实体对象。
         * @return 域对象。
         */
        @Override
        StudentActionStatsVo fromModel(StudentActionStats studentActionStats);
    }
    public static final StudentActionStatsModelMapper INSTANCE = Mappers.getMapper(StudentActionStatsModelMapper.class);
}
