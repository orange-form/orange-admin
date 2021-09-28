package com.orange.demo.courseclassservice.model;

import lombok.Data;
import javax.persistence.*;

/**
 * ClassCourse实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_class_course")
public class ClassCourse {

    /**
     * 班级Id。
     */
    @Id
    @Column(name = "class_id")
    private Long classId;

    /**
     * 课程Id。
     */
    @Id
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 课程顺序(数值越小越靠前)。
     */
    @Column(name = "course_order")
    private Integer courseOrder;
}
