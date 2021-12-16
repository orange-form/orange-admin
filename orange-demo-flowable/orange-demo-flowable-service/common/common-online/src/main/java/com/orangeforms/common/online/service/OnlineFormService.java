package com.orangeforms.common.online.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.online.model.OnlineForm;
import com.orangeforms.common.online.model.OnlineFormDatasource;

import java.util.List;
import java.util.Set;

/**
 * 在线表单数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineFormService extends IBaseService<OnlineForm, Long> {

    /**
     * 保存新增对象。
     *
     * @param onlineForm      新增对象。
     * @param datasourceIdSet 在线表单关联的数据源Id集合。
     * @return 返回新增对象。
     */
    OnlineForm saveNew(OnlineForm onlineForm, Set<Long> datasourceIdSet);

    /**
     * 更新数据对象。
     *
     * @param onlineForm         更新的对象。
     * @param originalOnlineForm 原有数据对象。
     * @param datasourceIdSet    在线表单关联的数据源Id集合。
     * @return 成功返回true，否则false。
     */
    boolean update(OnlineForm onlineForm, OnlineForm originalOnlineForm, Set<Long> datasourceIdSet);

    /**
     * 删除指定数据。
     *
     * @param formId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long formId);

    /**
     * 根据PageId，删除其所属的所有表单，以及表单关联的数据源数据。
     *
     * @param pageId 指定的pageId。
     * @return 删除数量。
     */
    int removeByPageId(Long pageId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineFormListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineForm> getOnlineFormList(OnlineForm filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineFormList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineForm> getOnlineFormListWithRelation(OnlineForm filter, String orderBy);

    /**
     * 获取使用指定数据表的表单列表。
     *
     * @param tableId 数据表Id。
     * @return 使用该数据表的表单列表。
     */
    List<OnlineForm> getOnlineFormListByTableId(Long tableId);

    /**
     * 获取指定表单的数据源列表。
     *
     * @param formId 指定的表单。
     * @return 表单和数据源的多对多关联对象列表。
     */
    List<OnlineFormDatasource> getFormDatasourceListByFormId(Long formId);

    /**
     * 查询正在使用当前数据源的表单。
     *
     * @param datasourceId 数据源Id。
     * @return 正在使用当前数据源的表单列表。
     */
    List<OnlineForm> getOnlineFormListByDatasourceId(Long datasourceId);

    /**
     * 查询指定PageId集合的在线表单列表。
     *
     * @param pageIdSet 页面Id集合。
     * @return 在线表单集合。
     */
    List<OnlineForm> getOnlineFormListByPageIds(Set<Long> pageIdSet);
}
