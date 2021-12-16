package com.orangeforms.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 在线表单数据表字段规则和字段多对多关联实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_column_rule")
public class OnlineColumnRule {

    /**
     * 字段Id。
     */
    @TableField(value = "column_id")
    private Long columnId;

    /**
     * 规则Id。
     */
    @TableField(value = "rule_id")
    private Long ruleId;

    /**
     * 规则属性数据。
     */
    @TableField(value = "prop_data_json")
    private String propDataJson;

    @TableField(exist = false)
    private OnlineRule onlineRule;
}
