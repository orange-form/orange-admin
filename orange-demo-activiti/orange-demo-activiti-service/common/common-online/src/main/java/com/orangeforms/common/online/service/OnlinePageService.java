package com.orangeforms.common.online.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.online.model.OnlinePage;
import com.orangeforms.common.online.model.OnlinePageDatasource;

import java.util.List;

/**
 * 在线表单页面数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlinePageService extends IBaseService<OnlinePage, Long> {

    /**
     * 保存新增对象。
     *
     * @param onlinePage 新增对象。
     * @return 返回新增对象。
     */
    OnlinePage saveNew(OnlinePage onlinePage);

    /**
     * 更新数据对象。
     *
     * @param onlinePage         更新的对象。
     * @param originalOnlinePage 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(OnlinePage onlinePage, OnlinePage originalOnlinePage);

    /**
     * 更新页面对象的发布状态。
     *
     * @param pageId    页面对象Id。
     * @param published 新的状态。
     */
    void updatePublished(Long pageId, Boolean published);

    /**
     * 删除指定数据，及其包含的表单和数据源等。
     *
     * @param pageId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long pageId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlinePageListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlinePage> getOnlinePageList(OnlinePage filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlinePageList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlinePage> getOnlinePageListWithRelation(OnlinePage filter, String orderBy);

    /**
     * 批量添加多对多关联关系。
     *
     * @param onlinePageDatasourceList 多对多关联表对象集合。
     * @param pageId 主表Id。
     */
    void addOnlinePageDatasourceList(List<OnlinePageDatasource> onlinePageDatasourceList, Long pageId);

    /**
     * 获取中间表数据。
     *
     * @param pageId       主表Id。
     * @param datasourceId 从表Id。
     * @return 中间表对象。
     */
    OnlinePageDatasource getOnlinePageDatasource(Long pageId, Long datasourceId);

    /**
     * 获取在线页面和数据源中间表数据列表。
     *
     * @param pageId 主表Id。
     * @return 在线页面和数据源中间表对象列表。
     */
    List<OnlinePageDatasource> getOnlinePageDatasourceListByPageId(Long pageId);

    /**
     * 根据数据源Id，返回使用该数据源的OnlinePage对象。
     *
     * @param datasourceId 数据源Id。
     * @return 使用该数据源的页面列表。
     */
    List<OnlinePage> getOnlinePageListByDatasourceId(Long datasourceId);

    /**
     * 移除单条多对多关系。
     *
     * @param pageId       主表Id。
     * @param datasourceId 从表Id。
     * @return 成功返回true，否则false。
     */
    boolean removeOnlinePageDatasource(Long pageId, Long datasourceId);
}
