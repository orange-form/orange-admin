package com.orangeforms.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * Grade实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@TableName(value = "zz_grade")
public class Grade {

    /**
     * 主键Id。
     */
    @TableId(value = "grade_id", type = IdType.AUTO)
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @TableField(value = "grade_name")
    private String gradeName;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    private Integer status;
}
