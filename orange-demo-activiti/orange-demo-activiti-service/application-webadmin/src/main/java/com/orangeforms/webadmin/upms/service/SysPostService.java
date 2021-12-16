package com.orangeforms.webadmin.upms.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.webadmin.upms.model.SysPost;
import com.orangeforms.webadmin.upms.model.SysUserPost;

import java.util.*;

/**
 * 岗位管理数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysPostService extends IBaseService<SysPost, Long> {

    /**
     * 保存新增对象。
     *
     * @param sysPost 新增对象。
     * @return 返回新增对象。
     */
    SysPost saveNew(SysPost sysPost);

    /**
     * 更新数据对象。
     *
     * @param sysPost         更新的对象。
     * @param originalSysPost 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(SysPost sysPost, SysPost originalSysPost);

    /**
     * 删除指定数据。
     *
     * @param postId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long postId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysPostListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysPost> getSysPostList(SysPost filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getSysPostList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysPost> getSysPostListWithRelation(SysPost filter, String orderBy);

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回不与指定主表主键Id存在对多对关系的列表。
     *
     * @param deptId  主表主键Id。
     * @param filter  从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysPost> getNotInSysPostListByDeptId(Long deptId, SysPost filter, String orderBy);

    /**
     * 获取指定部门的岗位列表。
     *
     * @param deptId  部门Id。
     * @param filter  从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysPost> getSysPostListByDeptId(Long deptId, SysPost filter, String orderBy);

    /**
     * 获取指定用户的用户岗位多对多关联数据列表。
     *
     * @param userId 用户Id。
     * @return 用户岗位多对多关联数据列表。
     */
    List<SysUserPost> getSysUserPostListByUserId(Long userId);

    /**
     * 判断指定的部门岗位Id集合是否都属于指定的部门Id。
     *
     * @param deptPostIdSet 部门岗位Id集合。
     * @param deptId        部门Id。
     * @return 全部是返回true，否则false。
     */
    boolean existAllPrimaryKeys(Set<Long> deptPostIdSet, Long deptId);
}
