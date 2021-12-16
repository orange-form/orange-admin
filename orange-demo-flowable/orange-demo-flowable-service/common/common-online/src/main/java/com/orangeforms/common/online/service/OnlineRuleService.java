package com.orangeforms.common.online.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.online.model.OnlineColumnRule;
import com.orangeforms.common.online.model.OnlineRule;

import java.util.List;
import java.util.Set;

/**
 * 验证规则数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineRuleService extends IBaseService<OnlineRule, Long> {

    /**
     * 保存新增对象。
     *
     * @param onlineRule 新增对象。
     * @return 返回新增对象。
     */
    OnlineRule saveNew(OnlineRule onlineRule);

    /**
     * 更新数据对象。
     *
     * @param onlineRule         更新的对象。
     * @param originalOnlineRule 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(OnlineRule onlineRule, OnlineRule originalOnlineRule);

    /**
     * 删除指定数据。
     *
     * @param ruleId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long ruleId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineRuleListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineRule> getOnlineRuleList(OnlineRule filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineRuleList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineRule> getOnlineRuleListWithRelation(OnlineRule filter, String orderBy);

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回不与指定主表主键Id存在对多对关系的列表。
     *
     * @param columnId 主表主键Id。
     * @param filter   从表的过滤对象。
     * @param orderBy  排序参数。
     * @return 查询结果集。
     */
    List<OnlineRule> getNotInOnlineRuleListByColumnId(Long columnId, OnlineRule filter, String orderBy);

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回与指定主表主键Id存在对多对关系的列表。
     *
     * @param columnId 主表主键Id。
     * @param filter   从表的过滤对象。
     * @param orderBy  排序参数。
     * @return 查询结果集。
     */
    List<OnlineRule> getOnlineRuleListByColumnId(Long columnId, OnlineRule filter, String orderBy);

    /**
     * 返回指定字段Id列表关联的字段规则对象列表。
     *
     * @param columnIdSet 指定的字段Id列表。
     * @return 关联的字段规则对象列表。
     */
    List<OnlineColumnRule> getOnlineColumnRuleListByColumnIds(Set<Long> columnIdSet);
}
