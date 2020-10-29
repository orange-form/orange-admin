package com.orange.demo.app.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Grade实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("Grade实体对象")
@Data
@Table(name = "zz_grade")
public class Grade {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 年级名称。
     */
    @ApiModelProperty(value = "年级名称", required = true)
    @NotBlank(message = "数据验证失败，年级名称不能为空！")
    @Column(name = "grade_name")
    private String gradeName;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false)
    @DeletedFlagColumn
    private Integer status;
}
