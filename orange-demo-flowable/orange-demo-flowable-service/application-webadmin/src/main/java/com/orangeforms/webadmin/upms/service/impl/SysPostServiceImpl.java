package com.orangeforms.webadmin.upms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.webadmin.upms.dao.SysDeptPostMapper;
import com.orangeforms.webadmin.upms.dao.SysPostMapper;
import com.orangeforms.webadmin.upms.dao.SysUserPostMapper;
import com.orangeforms.webadmin.upms.model.SysDeptPost;
import com.orangeforms.webadmin.upms.model.SysPost;
import com.orangeforms.webadmin.upms.model.SysUserPost;
import com.orangeforms.webadmin.upms.service.SysPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 岗位管理数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("sysPostService")
public class SysPostServiceImpl extends BaseService<SysPost, Long> implements SysPostService {

    @Autowired
    private SysPostMapper sysPostMapper;
    @Autowired
    private SysUserPostMapper sysUserPostMapper;
    @Autowired
    private SysDeptPostMapper sysDeptPostMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysPost> mapper() {
        return sysPostMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param sysPost 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysPost saveNew(SysPost sysPost) {
        sysPost.setPostId(idGenerator.nextLongId());
        sysPost.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        MyModelUtil.fillCommonsForInsert(sysPost);
        MyModelUtil.setDefaultValue(sysPost, "leaderPost", false);
        sysPostMapper.insert(sysPost);
        return sysPost;
    }

    /**
     * 更新数据对象。
     *
     * @param sysPost         更新的对象。
     * @param originalSysPost 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysPost sysPost, SysPost originalSysPost) {
        MyModelUtil.fillCommonsForUpdate(sysPost, originalSysPost);
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<SysPost> uw = this.createUpdateQueryForNullValue(sysPost, sysPost.getPostId());
        return sysPostMapper.update(sysPost, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param postId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long postId) {
        if (sysPostMapper.deleteById(postId) != 1) {
            return false;
        }
        // 开始删除多对多父表的关联
        SysUserPost sysUserPost = new SysUserPost();
        sysUserPost.setPostId(postId);
        sysUserPostMapper.delete(new QueryWrapper<>(sysUserPost));
        SysDeptPost sysDeptPost = new SysDeptPost();
        sysDeptPost.setPostId(postId);
        sysDeptPostMapper.delete(new QueryWrapper<>(sysDeptPost));
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysPostListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysPost> getSysPostList(SysPost filter, String orderBy) {
        return sysPostMapper.getSysPostList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getSysPostList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysPost> getSysPostListWithRelation(SysPost filter, String orderBy) {
        List<SysPost> resultList = sysPostMapper.getSysPostList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回不与指定主表主键Id存在对多对关系的列表。
     *
     * @param deptId  主表主键Id。
     * @param filter  从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysPost> getNotInSysPostListByDeptId(Long deptId, SysPost filter, String orderBy) {
        List<SysPost> resultList = sysPostMapper.getNotInSysPostListByDeptId(deptId, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    /**
     * 获取指定部门的岗位列表。
     *
     * @param deptId  部门Id。
     * @param filter  从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysPost> getSysPostListByDeptId(Long deptId, SysPost filter, String orderBy) {
        List<SysPost> resultList = sysPostMapper.getSysPostListByDeptId(deptId, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    @Override
    public List<SysUserPost> getSysUserPostListByUserId(Long userId) {
    SysUserPost filter = new SysUserPost();
        filter.setUserId(userId);
        return sysUserPostMapper.selectList(new QueryWrapper<>(filter));
    }

    @Override
    public boolean existAllPrimaryKeys(Set<Long> deptPostIdSet, Long deptId) {
        LambdaQueryWrapper<SysDeptPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptPost::getDeptId, deptId);
        queryWrapper.in(SysDeptPost::getDeptPostId, deptPostIdSet);
        return sysDeptPostMapper.selectCount(queryWrapper) == deptPostIdSet.size();
    }
}
