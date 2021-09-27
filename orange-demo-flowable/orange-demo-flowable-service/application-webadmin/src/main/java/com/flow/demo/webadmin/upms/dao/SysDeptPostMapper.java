package com.flow.demo.webadmin.upms.dao;

import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.webadmin.upms.model.SysDeptPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 部门岗位数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysDeptPostMapper extends BaseDaoMapper<SysDeptPost> {

    /**
     * 获取指定部门Id的部门岗位多对多关联数据列表，以及关联的部门和岗位数据。
     *
     * @param deptId 部门Id。如果参数为空则返回全部数据。
     * @return 部门岗位多对多数量列表。
     */
    List<Map<String, Object>> getSysDeptPostListWithRelationByDeptId(@Param("deptId") Long deptId);

    /**
     * 获取指定部门Id的领导部门岗位列表。
     *
     * @param deptId 部门Id。
     * @return 指定部门Id的领导部门岗位列表
     */
    List<SysDeptPost> getLeaderDeptPostList(@Param("deptId") Long deptId);
}
