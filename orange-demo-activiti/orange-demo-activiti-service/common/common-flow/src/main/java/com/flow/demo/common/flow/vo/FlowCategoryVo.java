package com.flow.demo.common.flow.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流程分类的Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowCategoryVo {

    /**
     * 主键Id。
     */
    private Long categoryId;

    /**
     * 显示名称。
     */
    private String name;

    /**
     * 分类编码。
     */
    private String code;

    /**
     * 实现顺序。
     */
    private Integer showOrder;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 更新者Id。
     */
    private Long updateUserId;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 创建者Id。
     */
    private Long createUserId;
}
