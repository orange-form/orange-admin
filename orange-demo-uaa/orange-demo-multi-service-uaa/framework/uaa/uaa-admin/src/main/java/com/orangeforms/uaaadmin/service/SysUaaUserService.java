package com.orangeforms.uaaadmin.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.uaaadmin.model.SysUaaUser;

import java.util.*;

/**
 * UAA用户数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysUaaUserService extends IBaseService<SysUaaUser, Long> {

    /**
     * 保存新增对象。
     *
     * @param sysUaaUser 新增对象。
     * @return 返回新增对象。
     */
    SysUaaUser saveNew(SysUaaUser sysUaaUser);

    /**
     * 更新数据对象。
     *
     * @param sysUaaUser         更新的对象。
     * @param originalSysUaaUser 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(SysUaaUser sysUaaUser, SysUaaUser originalSysUaaUser);

    /**
     * 修改密码。
     *
     * @param uaaUserId  用户Id。
     * @param newPass    新密码明文。
     * @return 成功返回true，否则false。
     */
    boolean changePassword(Long uaaUserId, String newPass);

    /**
     * 删除指定数据。
     *
     * @param userId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long userId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysUaaUserListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysUaaUser> getSysUaaUserList(SysUaaUser filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysUaaUserList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysUaaUser> getSysUaaUserListWithRelation(SysUaaUser filter, String orderBy);
}
