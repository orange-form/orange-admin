package com.flow.demo.common.online.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.core.object.CallResult;
import com.flow.demo.common.core.object.MyRelationParam;
import com.flow.demo.common.core.base.service.BaseService;
import com.flow.demo.common.online.dao.OnlineVirtualColumnMapper;
import com.flow.demo.common.online.model.OnlineDatasource;
import com.flow.demo.common.online.model.OnlineVirtualColumn;
import com.flow.demo.common.online.model.constant.VirtualType;
import com.flow.demo.common.online.service.OnlineColumnService;
import com.flow.demo.common.online.service.OnlineDatasourceRelationService;
import com.flow.demo.common.online.service.OnlineDatasourceService;
import com.flow.demo.common.online.service.OnlineVirtualColumnService;
import com.flow.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 虚拟字段数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("onlineVirtualColumnService")
public class OnlineVirtualColumnServiceImpl
        extends BaseService<OnlineVirtualColumn, Long> implements OnlineVirtualColumnService {

    @Autowired
    private OnlineVirtualColumnMapper onlineVirtualColumnMapper;
    @Autowired
    private OnlineDatasourceService onlineDatasourceService;
    @Autowired
    private OnlineDatasourceRelationService onlineDatasourceRelationService;
    @Autowired
    private OnlineColumnService onlineColumnService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<OnlineVirtualColumn> mapper() {
        return onlineVirtualColumnMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param virtualColumn 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OnlineVirtualColumn saveNew(OnlineVirtualColumn virtualColumn) {
        virtualColumn.setVirtualColumnId(idGenerator.nextLongId());
        if (virtualColumn.getVirtualType().equals(VirtualType.AGGREGATION)) {
            OnlineDatasource datasource = onlineDatasourceService.getById(virtualColumn.getDatasourceId());
            virtualColumn.setTableId(datasource.getMasterTableId());
        }
        onlineVirtualColumnMapper.insert(virtualColumn);
        return virtualColumn;
    }

    /**
     * 更新数据对象。
     *
     * @param virtualColumn         更新的对象。
     * @param originalVirtualColumn 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(OnlineVirtualColumn virtualColumn, OnlineVirtualColumn originalVirtualColumn) {
        if (virtualColumn.getVirtualType().equals(VirtualType.AGGREGATION)) {
            if (!virtualColumn.getDatasourceId().equals(originalVirtualColumn.getDatasourceId())) {
                OnlineDatasource datasource = onlineDatasourceService.getById(virtualColumn.getDatasourceId());
                virtualColumn.setTableId(datasource.getMasterTableId());
            }
        }
        UpdateWrapper<OnlineVirtualColumn> uw =
                this.createUpdateQueryForNullValue(virtualColumn, virtualColumn.getVirtualColumnId());
        return onlineVirtualColumnMapper.update(virtualColumn, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param virtualColumnId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long virtualColumnId) {
        return onlineVirtualColumnMapper.deleteById(virtualColumnId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineVirtualColumnListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineVirtualColumn> getOnlineVirtualColumnList(OnlineVirtualColumn filter, String orderBy) {
        return onlineVirtualColumnMapper.getOnlineVirtualColumnList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineVirtualColumnList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineVirtualColumn> getOnlineVirtualColumnListWithRelation(OnlineVirtualColumn filter, String orderBy) {
        List<OnlineVirtualColumn> resultList = onlineVirtualColumnMapper.getOnlineVirtualColumnList(filter, orderBy);
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 根据数据表的集合，查询关联的虚拟字段数据列表。
     * @param tableIdSet 在线数据表Id集合。
     * @return 关联的虚拟字段数据列表。
     */
    @Override
    public List<OnlineVirtualColumn> getOnlineVirtualColumnListByTableIds(Set<Long> tableIdSet) {
        LambdaQueryWrapper<OnlineVirtualColumn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OnlineVirtualColumn::getTableId, tableIdSet);
        return onlineVirtualColumnMapper.selectList(queryWrapper);
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param virtualColumn         最新数据对象。
     * @param originalVirtualColumn 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    @Override
    public CallResult verifyRelatedData(OnlineVirtualColumn virtualColumn, OnlineVirtualColumn originalVirtualColumn) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        if (this.needToVerify(virtualColumn, originalVirtualColumn, OnlineVirtualColumn::getDatasourceId)
                && !onlineDatasourceService.existId(virtualColumn.getDatasourceId())) {
            return CallResult.error(String.format(errorMessageFormat, "数据源Id"));
        }
        if (this.needToVerify(virtualColumn, originalVirtualColumn, OnlineVirtualColumn::getRelationId)
                && !onlineDatasourceRelationService.existId(virtualColumn.getRelationId())) {
            return CallResult.error(String.format(errorMessageFormat, "数据源关联Id"));
        }
        if (this.needToVerify(virtualColumn, originalVirtualColumn, OnlineVirtualColumn::getAggregationColumnId)
                && !onlineColumnService.existId(virtualColumn.getAggregationColumnId())) {
            return CallResult.error(String.format(errorMessageFormat, "聚合字段Id"));
        }
        return CallResult.ok();
    }
}
