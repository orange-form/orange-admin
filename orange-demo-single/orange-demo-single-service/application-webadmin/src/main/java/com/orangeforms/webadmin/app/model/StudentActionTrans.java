package com.orangeforms.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.webadmin.upms.model.SysDept;
import com.orangeforms.application.common.constant.StudentActionType;
import com.orangeforms.application.common.constant.DeviceType;
import com.orangeforms.common.core.annotation.*;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.webadmin.app.vo.StudentActionTransVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * StudentActionTrans实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@TableName(value = "zz_student_action_trans")
public class StudentActionTrans {

    /**
     * 主键Id。
     */
    @TableId(value = "trans_id")
    private Long transId;

    /**
     * 学生Id。
     */
    @TableField(value = "student_id")
    private Long studentId;

    /**
     * 学生名称。
     */
    @TableField(value = "student_name")
    private String studentName;

    /**
     * 学生校区。
     */
    @TableField(value = "school_id")
    private Long schoolId;

    /**
     * 年级Id。
     */
    @TableField(value = "grade_id")
    private Integer gradeId;

    /**
     * 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。
     */
    @TableField(value = "action_type")
    private Integer actionType;

    /**
     * 设备类型(0: iOS 1: Android 2: PC)。
     */
    @TableField(value = "device_type")
    private Integer deviceType;

    /**
     * 看视频秒数。
     */
    @TableField(value = "watch_video_seconds")
    private Integer watchVideoSeconds;

    /**
     * 购买献花数量。
     */
    @TableField(value = "flower_count")
    private Integer flowerCount;

    /**
     * 购买作业数量。
     */
    @TableField(value = "paper_count")
    private Integer paperCount;

    /**
     * 购买视频数量。
     */
    @TableField(value = "video_count")
    private Integer videoCount;

    /**
     * 购买课程数量。
     */
    @TableField(value = "course_count")
    private Integer courseCount;

    /**
     * 充值学币数量。
     */
    @TableField(value = "coin_count")
    private Integer coinCount;

    /**
     * 做题是否正确标记。
     */
    @TableField(value = "exercise_correct_flag")
    private Integer exerciseCorrectFlag;

    /**
     * 发生时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String createTimeEnd;

    @RelationDict(
            masterIdField = "schoolId",
            slaveServiceName = "sysDeptService",
            slaveModelClass = SysDept.class,
            slaveIdField = "deptId",
            slaveNameField = "deptName")
    @TableField(exist = false)
    private Map<String, Object> schoolIdDictMap;

    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @TableField(exist = false)
    private Map<String, Object> gradeIdDictMap;

    @RelationConstDict(
            masterIdField = "actionType",
            constantDictClass = StudentActionType.class)
    @TableField(exist = false)
    private Map<String, Object> actionTypeDictMap;

    @RelationConstDict(
            masterIdField = "deviceType",
            constantDictClass = DeviceType.class)
    @TableField(exist = false)
    private Map<String, Object> deviceTypeDictMap;

    @Mapper
    public interface StudentActionTransModelMapper extends BaseModelMapper<StudentActionTransVo, StudentActionTrans> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param studentActionTransVo 域对象。
         * @return 实体对象。
         */
        @Override
        StudentActionTrans toModel(StudentActionTransVo studentActionTransVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param studentActionTrans 实体对象。
         * @return 域对象。
         */
        @Override
        StudentActionTransVo fromModel(StudentActionTrans studentActionTrans);
    }
    public static final StudentActionTransModelMapper INSTANCE = Mappers.getMapper(StudentActionTransModelMapper.class);
}
