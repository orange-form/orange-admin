package com.orangeforms.common.online.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.online.model.OnlineVirtualColumn;

import java.util.*;

/**
 * 虚拟字段数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineVirtualColumnService extends IBaseService<OnlineVirtualColumn, Long> {

    /**
     * 保存新增对象。
     *
     * @param onlineVirtualColumn 新增对象。
     * @return 返回新增对象。
     */
    OnlineVirtualColumn saveNew(OnlineVirtualColumn onlineVirtualColumn);

    /**
     * 更新数据对象。
     *
     * @param onlineVirtualColumn         更新的对象。
     * @param originalOnlineVirtualColumn 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(OnlineVirtualColumn onlineVirtualColumn, OnlineVirtualColumn originalOnlineVirtualColumn);

    /**
     * 删除指定数据。
     *
     * @param virtualColumnId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long virtualColumnId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineVirtualColumnListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineVirtualColumn> getOnlineVirtualColumnList(OnlineVirtualColumn filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineVirtualColumnList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineVirtualColumn> getOnlineVirtualColumnListWithRelation(OnlineVirtualColumn filter, String orderBy);

    /**
     * 根据数据表的集合，查询关联的虚拟字段数据列表。
     * @param tableIdSet 在线数据表Id集合。
     * @return 关联的虚拟字段数据列表。
     */
    List<OnlineVirtualColumn> getOnlineVirtualColumnListByTableIds(Set<Long> tableIdSet);
}
