package com.orange.demo.webadmin.app.vo;

import lombok.Data;

/**
 * ClassCourseVO对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class ClassCourseVo {

    /**
     * 班级Id。
     */
    private Long classId;

    /**
     * 课程Id。
     */
    private Long courseId;

    /**
     * 课程顺序(数值越小越靠前)。
     */
    private Integer courseOrder;
}
