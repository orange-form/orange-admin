package com.flow.demo.common.online.vo;

import lombok.Data;

/**
 * 在线表单数据表字段规则和字段多对多关联VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineColumnRuleVo {

    /**
     * 字段Id。
     */
    private Long columnId;

    /**
     * 规则Id。
     */
    private Long ruleId;

    /**
     * 规则属性数据。
     */
    private String propDataJson;
}
