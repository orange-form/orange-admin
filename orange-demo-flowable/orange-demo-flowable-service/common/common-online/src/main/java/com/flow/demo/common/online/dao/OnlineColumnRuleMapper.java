package com.flow.demo.common.online.dao;

import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.online.model.OnlineColumnRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineColumnRuleMapper extends BaseDaoMapper<OnlineColumnRule> {

    /**
     * 获取指定字段Id关联的字段规则对象列表，同时还关联了每个OnlineRule对象。
     *
     * @param columnIdSet 字段Id集合。
     * @return 关联的字段规则对象列表。
     */
    List<OnlineColumnRule> getOnlineColumnRuleListByColumnIds(@Param("columnIdSet") Set<Long> columnIdSet);
}
