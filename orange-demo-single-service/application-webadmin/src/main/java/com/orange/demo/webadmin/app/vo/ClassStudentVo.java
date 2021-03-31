package com.orange.demo.webadmin.app.vo;

import lombok.Data;

/**
 * ClassStudentVO对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class ClassStudentVo {

    /**
     * 班级Id。
     */
    private Long classId;

    /**
     * 学生Id。
     */
    private Long studentId;
}
