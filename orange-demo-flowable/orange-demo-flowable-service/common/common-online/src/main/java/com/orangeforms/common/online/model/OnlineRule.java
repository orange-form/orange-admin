package com.orangeforms.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.annotation.RelationConstDict;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.common.online.model.constant.RuleType;
import com.orangeforms.common.online.vo.OnlineRuleVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单数据表字段验证规则实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_rule")
public class OnlineRule {

    /**
     * 主键Id。
     */
    @TableId(value = "rule_id")
    private Long ruleId;

    /**
     * 规则名称。
     */
    @TableField(value = "rule_name")
    private String ruleName;

    /**
     * 规则类型。
     */
    @TableField(value = "rule_type")
    private Integer ruleType;

    /**
     * 内置规则标记。
     */
    @TableField(value = "builtin")
    private Boolean builtin;

    /**
     * 自定义规则的正则表达式。
     */
    @TableField(value = "pattern")
    private String pattern;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * ruleId 的多对多关联表数据对象。
     */
    @TableField(exist = false)
    private OnlineColumnRule onlineColumnRule;

    @RelationConstDict(
            masterIdField = "ruleType",
            constantDictClass = RuleType.class)
    @TableField(exist = false)
    private Map<String, Object> ruleTypeDictMap;

    @Mapper
    public interface OnlineRuleModelMapper extends BaseModelMapper<OnlineRuleVo, OnlineRule> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param onlineRuleVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "onlineColumnRule", expression = "java(mapToBean(onlineRuleVo.getOnlineColumnRule(), com.orangeforms.common.online.model.OnlineColumnRule.class))")
        @Override
        OnlineRule toModel(OnlineRuleVo onlineRuleVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param onlineRule 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "onlineColumnRule", expression = "java(beanToMap(onlineRule.getOnlineColumnRule(), false))")
        @Override
        OnlineRuleVo fromModel(OnlineRule onlineRule);
    }
    public static final OnlineRuleModelMapper INSTANCE = Mappers.getMapper(OnlineRuleModelMapper.class);
}
