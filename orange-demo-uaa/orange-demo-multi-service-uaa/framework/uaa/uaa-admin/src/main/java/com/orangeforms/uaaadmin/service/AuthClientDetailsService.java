package com.orangeforms.uaaadmin.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.uaaadmin.model.AuthClientDetails;

import java.util.*;

/**
 * 应用客户端数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface AuthClientDetailsService extends IBaseService<AuthClientDetails, String> {

    /**
     * 保存新增对象。
     *
     * @param authClientDetails 新增对象。
     * @return 返回新增对象。
     */
    AuthClientDetails saveNew(AuthClientDetails authClientDetails);

    /**
     * 更新数据对象。
     *
     * @param authClientDetails         更新的对象。
     * @param originalAuthClientDetails 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(AuthClientDetails authClientDetails, AuthClientDetails originalAuthClientDetails);

    /**
     * 删除指定数据。
     *
     * @param clientId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(String clientId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getAuthClientDetailsListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<AuthClientDetails> getAuthClientDetailsList(AuthClientDetails filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getAuthClientDetailsList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<AuthClientDetails> getAuthClientDetailsListWithRelation(AuthClientDetails filter, String orderBy);
}
