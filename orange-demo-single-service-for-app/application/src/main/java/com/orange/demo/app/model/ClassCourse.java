package com.orange.demo.app.model;

import com.orange.demo.common.core.validator.UpdateGroup;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * ClassCourse实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_class_course")
public class ClassCourse {

    /**
     * 班级Id。
     */
    @NotNull(message = "数据验证失败，班级Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "class_id")
    private Long classId;

    /**
     * 课程Id。
     */
    @NotNull(message = "数据验证失败，课程Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 课程顺序(数值越小越靠前)。
     */
    @NotNull(message = "数据验证失败，课程顺序(数值越小越靠前)不能为空！", groups = {UpdateGroup.class})
    @Column(name = "course_order")
    private Integer courseOrder;
}
