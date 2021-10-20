package com.orange.demo.webadmin.app.vo;

import lombok.Data;

/**
 * GradeVO视图对象。
 *
 * @author Jerry
 * @date 2020-09-24
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
