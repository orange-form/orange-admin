package com.orange.demo.app.model;

import lombok.Data;
import javax.persistence.*;

/**
 * ClassStudent实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_class_student")
public class ClassStudent {

    /**
     * 班级Id。
     */
    @Id
    @Column(name = "class_id")
    private Long classId;

    /**
     * 学生Id。
     */
    @Id
    @Column(name = "student_id")
    private Long studentId;
}
