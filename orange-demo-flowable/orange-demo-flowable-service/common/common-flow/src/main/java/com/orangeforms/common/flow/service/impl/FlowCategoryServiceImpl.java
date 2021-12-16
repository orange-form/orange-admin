package com.orangeforms.common.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.flow.dao.*;
import com.orangeforms.common.flow.model.*;
import com.orangeforms.common.flow.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * FlowCategory数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowCategoryService")
public class FlowCategoryServiceImpl extends BaseService<FlowCategory, Long> implements FlowCategoryService {

    @Autowired
    private FlowCategoryMapper flowCategoryMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowCategory> mapper() {
        return flowCategoryMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param flowCategory 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowCategory saveNew(FlowCategory flowCategory) {
        flowCategory.setCategoryId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        flowCategory.setUpdateUserId(tokenData.getUserId());
        flowCategory.setCreateUserId(tokenData.getUserId());
        Date now = new Date();
        flowCategory.setUpdateTime(now);
        flowCategory.setCreateTime(now);
        flowCategoryMapper.insert(flowCategory);
        return flowCategory;
    }

    /**
     * 更新数据对象。
     *
     * @param flowCategory         更新的对象。
     * @param originalFlowCategory 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(FlowCategory flowCategory, FlowCategory originalFlowCategory) {
        flowCategory.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        flowCategory.setCreateUserId(originalFlowCategory.getCreateUserId());
        flowCategory.setUpdateTime(new Date());
        flowCategory.setCreateTime(originalFlowCategory.getCreateTime());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<FlowCategory> uw =
                this.createUpdateQueryForNullValue(flowCategory, flowCategory.getCategoryId());
        return flowCategoryMapper.update(flowCategory, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param categoryId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long categoryId) {
        return flowCategoryMapper.deleteById(categoryId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getFlowCategoryListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<FlowCategory> getFlowCategoryList(FlowCategory filter, String orderBy) {
        return flowCategoryMapper.getFlowCategoryList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getFlowCategoryList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<FlowCategory> getFlowCategoryListWithRelation(FlowCategory filter, String orderBy) {
        List<FlowCategory> resultList = flowCategoryMapper.getFlowCategoryList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }
}
