package com.orange.demo.courseclassservice.model;

import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import lombok.Data;
import javax.persistence.*;

/**
 * Grade实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_grade")
public class Grade {

    /**
     * 主键Id。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @Column(name = "grade_name")
    private String gradeName;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @DeletedFlagColumn
    private Integer status;
}
