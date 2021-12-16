package com.orangeforms.common.online.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.online.model.OnlineDatasource;
import com.orangeforms.common.online.model.OnlineDatasourceTable;
import com.orangeforms.common.online.object.SqlTable;

import java.util.List;
import java.util.Set;

/**
 * 数据模型数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineDatasourceService extends IBaseService<OnlineDatasource, Long> {

    /**
     * 保存新增对象。
     *
     * @param onlineDatasource 新增对象。
     * @param sqlTable         新增的数据表对象。
     * @param pageId           关联的页面Id。
     * @return 返回新增对象。
     */
    OnlineDatasource saveNew(OnlineDatasource onlineDatasource, SqlTable sqlTable, Long pageId);

    /**
     * 更新数据对象。
     *
     * @param onlineDatasource         更新的对象。
     * @param originalOnlineDatasource 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(OnlineDatasource onlineDatasource, OnlineDatasource originalOnlineDatasource);

    /**
     * 删除指定数据。
     *
     * @param datasourceId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long datasourceId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineDatasourceListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineDatasource> getOnlineDatasourceList(OnlineDatasource filter, String orderBy);

    /**
     * 查询指定数据源Id集合的数据源列表。
     *
     * @param datasourceIdSet 数据源Id集合。
     * @return 查询结果集。
     */
    List<OnlineDatasource> getOnlineDatasourceList(Set<Long> datasourceIdSet);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineDatasourceList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineDatasource> getOnlineDatasourceListWithRelation(OnlineDatasource filter, String orderBy);

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回不与指定主表主键Id存在对多对关系的列表。
     *
     * @param pageId  主表主键Id。
     * @param filter  从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineDatasource> getNotInOnlineDatasourceListByPageId(Long pageId, OnlineDatasource filter, String orderBy);

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回与指定主表主键Id存在对多对关系的列表。
     *
     * @param pageId  主表主键Id。
     * @param filter  从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineDatasource> getOnlineDatasourceListByPageId(Long pageId, OnlineDatasource filter, String orderBy);

    /**
     * 获取指定数据源Id集合所关联的在线表关联数据。
     *
     * @param datasourceIdSet 数据源Id集合。
     * @return 数据源和数据表的多对多关联列表。
     */
    List<OnlineDatasourceTable> getOnlineDatasourceTableList(Set<Long> datasourceIdSet);

    /**
     * 根据在线表单Id集合，获取关联的在线数据源对象列表。
     *
     * @param readFormIdSet 在线表单Id集合。
     * @return 与参数表单Id关联的数据源列表。
     */
    List<OnlineDatasource> getOnlineDatasourceListByFormIds(Set<Long> readFormIdSet);
}
