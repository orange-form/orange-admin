package com.orangeforms.common.online.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.online.model.OnlineRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 验证规则数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineRuleMapper extends BaseDaoMapper<OnlineRule> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineRuleFilter 主表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineRule> getOnlineRuleList(
            @Param("onlineRuleFilter") OnlineRule onlineRuleFilter, @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表数据列表。
     *
     * @param columnId         关联主表Id。
     * @param onlineRuleFilter 从表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 从表数据列表。
     */
    List<OnlineRule> getOnlineRuleListByColumnId(
            @Param("columnId") Long columnId,
            @Param("onlineRuleFilter") OnlineRule onlineRuleFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表中没有和主表建立关联关系的数据列表。
     *
     * @param columnId         关联主表Id。
     * @param onlineRuleFilter 过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 与主表没有建立关联的从表数据列表。
     */
    List<OnlineRule> getNotInOnlineRuleListByColumnId(
            @Param("columnId") Long columnId,
            @Param("onlineRuleFilter") OnlineRule onlineRuleFilter,
            @Param("orderBy") String orderBy);
}
