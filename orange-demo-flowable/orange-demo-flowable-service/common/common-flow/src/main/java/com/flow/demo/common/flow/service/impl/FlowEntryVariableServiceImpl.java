package com.flow.demo.common.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flow.demo.common.flow.service.*;
import com.flow.demo.common.flow.dao.*;
import com.flow.demo.common.flow.model.*;
import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.core.object.MyRelationParam;
import com.flow.demo.common.core.base.service.BaseService;
import com.flow.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 流程变量数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowEntryVariableService")
public class FlowEntryVariableServiceImpl extends BaseService<FlowEntryVariable, Long> implements FlowEntryVariableService {

    @Autowired
    private FlowEntryVariableMapper flowEntryVariableMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowEntryVariable> mapper() {
        return flowEntryVariableMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param flowEntryVariable 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowEntryVariable saveNew(FlowEntryVariable flowEntryVariable) {
        flowEntryVariable.setVariableId(idGenerator.nextLongId());
        flowEntryVariable.setCreateTime(new Date());
        flowEntryVariableMapper.insert(flowEntryVariable);
        return flowEntryVariable;
    }

    /**
     * 更新数据对象。
     *
     * @param flowEntryVariable         更新的对象。
     * @param originalFlowEntryVariable 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(FlowEntryVariable flowEntryVariable, FlowEntryVariable originalFlowEntryVariable) {
        flowEntryVariable.setCreateTime(originalFlowEntryVariable.getCreateTime());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<FlowEntryVariable> uw = this.createUpdateQueryForNullValue(flowEntryVariable, flowEntryVariable.getVariableId());
        return flowEntryVariableMapper.update(flowEntryVariable, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param variableId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long variableId) {
        return flowEntryVariableMapper.deleteById(variableId) == 1;
    }

    /**
     * 删除指定流程Id的所有变量。
     *
     * @param entryId 流程Id。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByEntryId(Long entryId) {
        flowEntryVariableMapper.delete(
                new LambdaQueryWrapper<FlowEntryVariable>().eq(FlowEntryVariable::getEntryId, entryId));
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getFlowEntryVariableListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<FlowEntryVariable> getFlowEntryVariableList(FlowEntryVariable filter, String orderBy) {
        return flowEntryVariableMapper.getFlowEntryVariableList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getFlowEntryVariableList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<FlowEntryVariable> getFlowEntryVariableListWithRelation(FlowEntryVariable filter, String orderBy) {
        List<FlowEntryVariable> resultList = flowEntryVariableMapper.getFlowEntryVariableList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }
}
