package com.orange.demo.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orange.demo.webadmin.upms.model.SysDept;
import com.orange.demo.webadmin.app.model.constant.ClassLevel;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.webadmin.app.vo.StudentClassVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * StudentClass实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@TableName(value = "zz_class")
public class StudentClass {

    /**
     * 班级Id。
     */
    @TableId(value = "class_id")
    private Long classId;

    /**
     * 班级名称。
     */
    @TableField(value = "class_name")
    private String className;

    /**
     * 学校Id。
     */
    @TableField(value = "school_id")
    private Long schoolId;

    /**
     * 学生班长Id。
     */
    @TableField(value = "leader_id")
    private Long leaderId;

    /**
     * 已完成课时数量。
     */
    @TableField(value = "finish_class_hour")
    private Integer finishClassHour;

    /**
     * 班级级别(0: 初级班 1: 培优班 2: 冲刺提分班 3: 竞赛班)。
     */
    @TableField(value = "class_level")
    private Integer classLevel;

    /**
     * 创建用户。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 班级创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    private Integer status;

    @RelationDict(
            masterIdField = "schoolId",
            slaveServiceName = "sysDeptService",
            slaveModelClass = SysDept.class,
            slaveIdField = "deptId",
            slaveNameField = "deptName")
    @TableField(exist = false)
    private Map<String, Object> schoolIdDictMap;

    @RelationDict(
            masterIdField = "leaderId",
            slaveServiceName = "studentService",
            slaveModelClass = Student.class,
            slaveIdField = "studentId",
            slaveNameField = "studentName")
    @TableField(exist = false)
    private Map<String, Object> leaderIdDictMap;

    @RelationConstDict(
            masterIdField = "classLevel",
            constantDictClass = ClassLevel.class)
    @TableField(exist = false)
    private Map<String, Object> classLevelDictMap;

    @Mapper
    public interface StudentClassModelMapper extends BaseModelMapper<StudentClassVo, StudentClass> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param studentClassVo 域对象。
         * @return 实体对象。
         */
        @Override
        StudentClass toModel(StudentClassVo studentClassVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param studentClass 实体对象。
         * @return 域对象。
         */
        @Override
        StudentClassVo fromModel(StudentClass studentClass);
    }
    public static final StudentClassModelMapper INSTANCE = Mappers.getMapper(StudentClassModelMapper.class);
}
