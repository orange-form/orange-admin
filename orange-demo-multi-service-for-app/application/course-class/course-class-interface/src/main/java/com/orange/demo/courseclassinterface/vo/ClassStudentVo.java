package com.orange.demo.courseclassinterface.vo;

import lombok.Data;

/**
 * ClassStudentVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
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
