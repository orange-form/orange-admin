package com.orangeforms.upmsservice.service;

import com.orangeforms.upmsservice.model.*;
import com.orangeforms.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 部门管理数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysDeptService extends IBaseService<SysDept, Long> {

    /**
     * 保存新增的部门对象。
     *
     * @param sysDept       新增的部门对象。
     * @param parentSysDept 上级部门对象。
     * @return 新增后的部门对象。
     */
    SysDept saveNew(SysDept sysDept, SysDept parentSysDept);

    /**
     * 更新部门对象。
     *
     * @param sysDept         更新的部门对象。
     * @param originalSysDept 原有的部门对象。
     * @return 更新成功返回true，否则false。
     */
    boolean update(SysDept sysDept, SysDept originalSysDept);

    /**
     * 删除指定数据。
     *
     * @param deptId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long deptId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysDeptListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysDept> getSysDeptList(SysDept filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysDeptListWithRelation)方法。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    <M> List<SysDept> getSysDeptList(String inFilterField, Set<M> inFilterValues, SysDept filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysDeptList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    List<SysDept> getSysDeptListWithRelation(SysDept filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 同时还包含主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysDeptList)，以便获取更好的查询性能。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    <M> List<SysDept> getSysDeptListWithRelation(
            String inFilterField, Set<M> inFilterValues, SysDept filter, String orderBy);

    /**
     * 判断指定对象是否包含下级对象。
     *
     * @param deptId 主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasChildren(Long deptId);

    /**
     * 判断指定部门Id是否包含用户对象。
     *
     * @param deptId 部门主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasChildrenUser(Long deptId);
}
