package com.orange.demo.courseclassservice.model;

import com.orange.demo.courseclassapi.vo.CourseVo;
import com.orange.demo.courseclassapi.constant.CourseDifficult;
import com.orange.demo.application.common.constant.Subject;
import com.orange.demo.common.core.upload.UploadStoreTypeEnum;
import com.orange.demo.common.core.annotation.UploadFlagColumn;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Course实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_course")
public class Course {

    /**
     * 主键Id。
     */
    @Id
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 课程名称。
     */
    @Column(name = "course_name")
    private String courseName;

    /**
     * 课程价格。
     */
    private BigDecimal price;

    /**
     * 课程描述。
     */
    private String description;

    /**
     * 课程难度(0: 容易 1: 普通 2: 很难)。
     */
    private Integer difficulty;

    /**
     * 年级Id。
     */
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 学科Id。
     */
    @Column(name = "subject_id")
    private Integer subjectId;

    /**
     * 课时数量。
     */
    @Column(name = "class_hour")
    private Integer classHour;

    /**
     * 多张课程图片地址。
     */
    @UploadFlagColumn(storeType = UploadStoreTypeEnum.LOCAL_SYSTEM)
    @Column(name = "picture_url")
    private String pictureUrl;

    /**
     * 创建用户Id。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 最后修改时间。
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * price 范围过滤起始值(>=)。
     */
    @Transient
    private BigDecimal priceStart;

    /**
     * price 范围过滤结束值(<=)。
     */
    @Transient
    private BigDecimal priceEnd;

    /**
     * classHour 范围过滤起始值(>=)。
     */
    @Transient
    private Integer classHourStart;

    /**
     * classHour 范围过滤结束值(<=)。
     */
    @Transient
    private Integer classHourEnd;

    /**
     * updateTime 范围过滤起始值(>=)。
     */
    @Transient
    private String updateTimeStart;

    /**
     * updateTime 范围过滤结束值(<=)。
     */
    @Transient
    private String updateTimeEnd;

    /**
     * courseId 的多对多关联表数据对象。
     */
    @Transient
    private ClassCourse classCourse;

    @RelationDict(
            masterIdField = "gradeId",
            slaveServiceName = "gradeService",
            slaveModelClass = Grade.class,
            slaveIdField = "gradeId",
            slaveNameField = "gradeName")
    @Transient
    private Map<String, Object> gradeIdDictMap;

    @RelationConstDict(
            masterIdField = "difficulty",
            constantDictClass = CourseDifficult.class)
    @Transient
    private Map<String, Object> difficultyDictMap;

    @RelationConstDict(
            masterIdField = "subjectId",
            constantDictClass = Subject.class)
    @Transient
    private Map<String, Object> subjectIdDictMap;

    @Mapper
    public interface CourseModelMapper extends BaseModelMapper<CourseVo, Course> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param courseVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "classCourse", expression = "java(mapToBean(courseVo.getClassCourse(), com.orange.demo.courseclassservice.model.ClassCourse.class))")
        @Override
        Course toModel(CourseVo courseVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param course 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "classCourse", expression = "java(beanToMap(course.getClassCourse(), false))")
        @Override
        CourseVo fromModel(Course course);
    }
    public static final CourseModelMapper INSTANCE = Mappers.getMapper(CourseModelMapper.class);
}
