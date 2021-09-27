package com.flow.demo.webadmin.upms.service;

import com.flow.demo.webadmin.upms.model.*;
import com.flow.demo.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 部门管理数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
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
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getSysDeptList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysDept> getSysDeptListWithRelation(SysDept filter, String orderBy);

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

    /**
     * 批量添加多对多关联关系。
     *
     * @param sysDeptPostList 多对多关联表对象集合。
     * @param deptId 主表Id。
     */
    void addSysDeptPostList(List<SysDeptPost> sysDeptPostList, Long deptId);

    /**
     * 更新中间表数据。
     *
     * @param sysDeptPost 中间表对象。
     * @return 更新成功与否。
     */
    boolean updateSysDeptPost(SysDeptPost sysDeptPost);

    /**
     * 移除单条多对多关系。
     *
     * @param deptId 主表Id。
     * @param postId 从表Id。
     * @return 成功返回true，否则false。
     */
    boolean removeSysDeptPost(Long deptId, Long postId);

    /**
     * 获取中间表数据。
     *
     * @param deptId 主表Id。
     * @param postId 从表Id。
     * @return 中间表对象。
     */
    SysDeptPost getSysDeptPost(Long deptId, Long postId);

    /**
     * 根据部门岗位Id获取部门岗位关联对象。
     *
     * @param deptPostId 部门岗位Id。
     * @return 部门岗位对象。
     */
    SysDeptPost getSysDeptPost(Long deptPostId);

    /**
     * 获取指定部门Id的部门岗位多对多关联数据列表，以及关联的部门和岗位数据。
     *
     * @param deptId 部门Id。如果参数为空则返回全部数据。
     * @return 部门岗位多对多数量列表。
     */
    List<Map<String, Object>> getSysDeptPostListWithRelationByDeptId(Long deptId);

    /**
     * 根据部门Id获取该部门领导岗位的部门岗位Id集合。
     *
     * @param deptId 部门Id。
     * @return 部门领导岗位的部门岗位Id集合。
     */
    List<Long> getLeaderDeptPostIdList(Long deptId);

    /**
     * 根据部门Id获取上级部门领导岗位的部门岗位Id集合。
     *
     * @param deptId 部门Id。
     * @return 上级部门领导岗位的部门岗位Id集合。
     */
    List<Long> getUpLeaderDeptPostIdList(Long deptId);
}
