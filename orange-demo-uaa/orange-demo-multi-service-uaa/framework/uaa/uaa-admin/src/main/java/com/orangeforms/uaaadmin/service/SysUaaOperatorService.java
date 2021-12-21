package com.orangeforms.uaaadmin.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.uaaadmin.model.SysUaaOperator;

import java.util.*;

/**
 * 操作员数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysUaaOperatorService extends IBaseService<SysUaaOperator, Long> {

    /**
     * 根据登录名获取UAA操作员对象。
     *
     * @param loginName UAA操作员登录名。
     * @return UAA操作员对象。
     */
    SysUaaOperator getUaaOperatorByLoginName(String loginName);

    /**
     * 保存新增对象。
     *
     * @param sysUaaOperator 新增对象。
     * @return 返回新增对象。
     */
    SysUaaOperator saveNew(SysUaaOperator sysUaaOperator);

    /**
     * 更新数据对象。
     *
     * @param sysUaaOperator         更新的对象。
     * @param originalSysUaaOperator 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(SysUaaOperator sysUaaOperator, SysUaaOperator originalSysUaaOperator);

    /**
     * 修改密码。
     *
     * @param uaaOperatorId 操作员Id。
     * @param newPass       新密码明文。
     * @return 成功返回true，否则false。
     */
    boolean changePassword(Long uaaOperatorId, String newPass);

    /**
     * 删除指定数据。
     *
     * @param operatorId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long operatorId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysUaaOperatorListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysUaaOperator> getSysUaaOperatorList(SysUaaOperator filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysUaaOperatorList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysUaaOperator> getSysUaaOperatorListWithRelation(SysUaaOperator filter, String orderBy);
}
