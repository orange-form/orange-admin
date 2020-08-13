package com.orange.demo.courseclassservice.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Grade实体对象。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_grade")
public class Grade {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @NotBlank(message = "数据验证失败，年级名称不能为空！")
    @Column(name = "grade_name")
    private String gradeName;

    /**
     * 是否正在使用（0：不是，1：是）。
     */
    @NotNull(message = "数据验证失败，是否正在使用（0：不是，1：是）不能为空！")
    private Integer status;
}
