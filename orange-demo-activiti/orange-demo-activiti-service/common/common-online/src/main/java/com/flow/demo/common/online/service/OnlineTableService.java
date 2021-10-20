package com.flow.demo.common.online.service;

import com.flow.demo.common.core.base.service.IBaseService;
import com.flow.demo.common.online.model.OnlineTable;
import com.flow.demo.common.online.object.SqlTable;

import java.util.List;
import java.util.Set;

/**
 * 数据表数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineTableService extends IBaseService<OnlineTable, Long> {

    /**
     * 基于数据库表保存新增对象。
     *
     * @param sqlTable 数据库表对象。
     * @return 返回新增对象。
     */
    OnlineTable saveNewFromSqlTable(SqlTable sqlTable);

    /**
     * 更新数据对象。
     *
     * @param onlineTable         更新的对象。
     * @param originalOnlineTable 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(OnlineTable onlineTable, OnlineTable originalOnlineTable);

    /**
     * 删除指定表及其关联的字段数据。
     *
     * @param tableId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long tableId);

    /**
     * 删除指定数据表Id集合中的表，及其关联字段。
     *
     * @param tableIdSet 待删除的数据表Id集合。
     */
    void removeByTableIdSet(Set<Long> tableIdSet);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineTableListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineTable> getOnlineTableList(OnlineTable filter, String orderBy);

    /**
     * 获取指定在线表Id集合的对象列表。
     *
     * @param tableIdSet 主键Id集合。
     * @return 指定的数据表对象列表。
     */
    List<OnlineTable> getOnlineTableList(Set<Long> tableIdSet);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineTableList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineTable> getOnlineTableListWithRelation(OnlineTable filter, String orderBy);

    /**
     * 根据数据源Id，获取该数据源及其关联所引用的数据表列表。
     *
     * @param datasourceId 指定的数据源Id。
     * @return 该数据源及其关联所引用的数据表列表。
     */
    List<OnlineTable> getOnlineTableListByDatasourceId(Long datasourceId);

    /**
     * 从缓存中获取指定的表数据及其关联字段列表。优先从缓存中读取，如果不存在则从数据库中读取，并同步到缓存。
     * 该接口方法仅仅用户在线表单的动态数据操作接口，而非在线表单的配置接口。
     *
     * @param tableId 表主键Id。
     * @return 查询后的在线表对象。
     */
    OnlineTable getOnlineTableFromCache(Long tableId);
}
