package com.orange.demo.courseclassservice.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * ClassCourse实体对象。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_class_course")
public class ClassCourse {

    /**
     * 班级Id。
     */
    @NotNull(message = "数据验证失败，班级Id不能为空！")
    @Id
    @Column(name = "class_id")
    private Long classId;

    /**
     * 课程Id。
     */
    @NotNull(message = "数据验证失败，课程Id不能为空！")
    @Id
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 课程顺序(数值越小越靠前)。
     */
    @NotNull(message = "数据验证失败，课程顺序(数值越小越靠前)不能为空！")
    @Column(name = "course_order")
    private Integer courseOrder;
}
