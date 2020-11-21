package com.orange.demo.app.model;

import com.orange.demo.common.core.validator.UpdateGroup;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

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
    @NotNull(message = "数据验证失败，班级Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "class_id")
    private Long classId;

    /**
     * 学生Id。
     */
    @NotNull(message = "数据验证失败，学生Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "student_id")
    private Long studentId;
}
