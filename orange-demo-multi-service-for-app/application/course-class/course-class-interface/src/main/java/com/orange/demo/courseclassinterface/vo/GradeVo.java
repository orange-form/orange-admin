package com.orange.demo.courseclassinterface.vo;

import lombok.Data;

/**
 * GradeVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class GradeVo {

    /**
     * 主键Id。
     */
    private Integer gradeId;

    /**
     * 年级名称。
     */
    private String gradeName;
}
