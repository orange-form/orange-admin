package com.orangeforms.courseclassservice.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * ClassStudent实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@TableName(value = "zz_class_student")
public class ClassStudent {

    /**
     * 班级Id。
     */
    @TableField(value = "class_id")
    private Long classId;

    /**
     * 学生Id。
     */
    @TableField(value = "student_id")
    private Long studentId;
}
