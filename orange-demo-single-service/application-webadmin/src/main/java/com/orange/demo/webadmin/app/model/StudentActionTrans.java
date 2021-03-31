package com.orange.demo.webadmin.app.model;

import com.orange.demo.application.common.constant.StudentActionType;
import com.orange.demo.application.common.constant.DeviceType;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.webadmin.app.vo.StudentActionTransVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;

import java.util.Date;
import java.util.Map;

/**
 * StudentActionTrans实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_student_action_trans")
public class StudentActionTrans {

    /**
     * 主键Id。
     */
    @Id
    @Column(name = "trans_id")
    private Long transId;

    /**
     * 学生Id。
     */
    @Column(name = "student_id")
    private Long studentId;

    /**
     * 学生名称。
     */
    @Column(name = "student_name")
    private String studentName;

    /**
     * 学生校区。
     */
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 年级Id。
     */
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。
     */
    @Column(name = "action_type")
    private Integer actionType;

    /**
     * 设备类型(0: iOS 1: Android 2: PC)。
     */
    @Column(name = "device_type")
    private Integer deviceType;

    /**
     * 看视频秒数。
     */
    @Column(name = "watch_video_seconds")
    private Integer watchVideoSeconds;

    /**
     * 购买献花数量。
     */
    @Column(name = "flower_count")
    private Integer flowerCount;

    /**
     * 购买作业数量。
     */
    @Column(name = "paper_count")
    private Integer paperCount;

    /**
     * 购买视频数量。
     */
    @Column(name = "video_count")
    private Integer videoCount;

    /**
     * 购买课程数量。
     */
    @Column(name = "course_count")
    private Integer courseCount;

    /**
     * 充值学币数量。
     */
    @Column(name = "coin_count")
    private Integer coinCount;

    /**
     * 做题是否正确标记。
     */
    @Column(name = "exercise_correct_flag")
    private Integer exerciseCorrectFlag;

    /**
     * 发生时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    @Transient
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @Transient
    private String createTimeEnd;

    @RelationDict(
            masterIdField = "schoolId",
            slaveServiceName = "schoolInfoService",
            slaveModelClass = SchoolInfo.class,
            slaveIdField = "schoolId",
            slaveNameField = "schoolName")
    @Transient
    private Map<String, Object> schoolIdDictMap;

    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @Transient
    private Map<String, Object> gradeIdDictMap;

    @RelationConstDict(
            masterIdField = "actionType",
            constantDictClass = StudentActionType.class)
    @Transient
    private Map<String, Object> actionTypeDictMap;

    @RelationConstDict(
            masterIdField = "deviceType",
            constantDictClass = DeviceType.class)
    @Transient
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
