package com.orangeforms.common.online.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.util.RedisKeyUtil;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.online.dao.OnlineTableMapper;
import com.orangeforms.common.online.model.OnlineColumn;
import com.orangeforms.common.online.model.OnlineTable;
import com.orangeforms.common.online.model.constant.FieldKind;
import com.orangeforms.common.online.object.SqlTable;
import com.orangeforms.common.online.service.OnlineColumnService;
import com.orangeforms.common.online.service.OnlineTableService;
import com.github.pagehelper.Page;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据表数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("onlineTableService")
public class OnlineTableServiceImpl extends BaseService<OnlineTable, Long> implements OnlineTableService {

    @Autowired
    private OnlineTableMapper onlineTableMapper;
    @Autowired
    private OnlineColumnService onlineColumnService;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 在线对象表的缺省缓存时间(小时)。
     */
    private static final int DEFAULT_CACHED_TABLE_HOURS = 168;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<OnlineTable> mapper() {
        return onlineTableMapper;
    }

    /**
     * 基于数据库表保存新增对象。
     *
     * @param sqlTable 数据库表对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OnlineTable saveNewFromSqlTable(SqlTable sqlTable) {
        OnlineTable onlineTable = new OnlineTable();
        onlineTable.setDblinkId(sqlTable.getDblinkId());
        onlineTable.setTableId(idGenerator.nextLongId());
        onlineTable.setTableName(sqlTable.getTableName());
        String modelName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, sqlTable.getTableName());
        onlineTable.setModelName(modelName);
        Date now = new Date();
        onlineTable.setUpdateTime(now);
        onlineTable.setCreateTime(now);
        onlineTableMapper.insert(onlineTable);
        List<OnlineColumn> columnList = onlineColumnService.saveNewList(sqlTable.getColumnList(), onlineTable.getTableId());
        onlineTable.setColumnList(columnList);
        return onlineTable;
    }

    /**
     * 更新数据对象。
     *
     * @param onlineTable         更新的对象。
     * @param originalOnlineTable 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(OnlineTable onlineTable, OnlineTable originalOnlineTable) {
        this.evictTableCache(onlineTable.getTableId());
        onlineTable.setUpdateTime(new Date());
        onlineTable.setCreateTime(originalOnlineTable.getCreateTime());
        UpdateWrapper<OnlineTable> uw = this.createUpdateQueryForNullValue(onlineTable, onlineTable.getTableId());
        return onlineTableMapper.update(onlineTable, uw) == 1;
    }

    /**
     * 删除指定表及其关联的字段数据。
     *
     * @param tableId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long tableId) {
        if (onlineTableMapper.deleteById(tableId) == 0) {
            return false;
        }
        this.evictTableCache(tableId);
        onlineColumnService.removeByTableId(tableId);
        return true;
    }

    /**
     * 删除指定数据表Id集合中的表，及其关联字段。
     *
     * @param tableIdSet 待删除的数据表Id集合。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByTableIdSet(Set<Long> tableIdSet) {
        tableIdSet.forEach(this::evictTableCache);
        onlineTableMapper.delete(
                new QueryWrapper<OnlineTable>().lambda().in(OnlineTable::getTableId, tableIdSet));
        onlineColumnService.removeByTableIdSet(tableIdSet);
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineTableListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineTable> getOnlineTableList(OnlineTable filter, String orderBy) {
        return onlineTableMapper.getOnlineTableList(filter, orderBy);
    }

    /**
     * 获取指定在线表Id集合的对象列表。
     *
     * @param tableIdSet 主键Id集合。
     * @return 指定的数据表对象列表。
     */
    @Override
    public List<OnlineTable> getOnlineTableList(Set<Long> tableIdSet) {
        return onlineTableMapper.selectBatchIds(tableIdSet);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineTableList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineTable> getOnlineTableListWithRelation(OnlineTable filter, String orderBy) {
        List<OnlineTable> resultList = onlineTableMapper.getOnlineTableList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 根据数据源Id，获取该数据源及其关联所引用的数据表列表。
     *
     * @param datasourceId 指定的数据源Id。
     * @return 该数据源及其关联所引用的数据表列表。
     */
    @Override
    public List<OnlineTable> getOnlineTableListByDatasourceId(Long datasourceId) {
        return onlineTableMapper.getOnlineTableListByDatasourceId(datasourceId);
    }

    /**
     * 从缓存中获取指定的表数据及其关联字段列表。优先从缓存中读取，如果不存在则从数据库中读取，并同步到缓存。
     * 该接口方法仅仅用户在线表单的动态数据操作接口，而非在线表单的配置接口。
     *
     * @param tableId 表主键Id。
     * @return 查询后的在线表对象。
     */
    @Override
    public OnlineTable getOnlineTableFromCache(Long tableId) {
        String redisKey = RedisKeyUtil.makeOnlineTableKey(tableId);
        RBucket<String> tableBucket = redissonClient.getBucket(redisKey);
        if (tableBucket.isExists()) {
            String tableInfo = tableBucket.get();
            return JSON.parseObject(tableInfo, OnlineTable.class);
        }
        OnlineTable table = this.getByIdWithRelation(tableId, MyRelationParam.full());
        if (table == null) {
            return null;
        }
        for (OnlineColumn column : table.getColumnList()) {
            if (column.getPrimaryKey()) {
                table.setPrimaryKeyColumn(column);
                continue;
            }
            if (ObjectUtil.equal(column.getFieldKind(), FieldKind.LOGIC_DELETE)) {
                table.setLogicDeleteColumn(column);
            }
        }
        Map<Long, OnlineColumn> columnMap =
                table.getColumnList().stream().collect(Collectors.toMap(OnlineColumn::getColumnId, c -> c));
        table.setColumnMap(columnMap);
        table.setColumnList(null);
        tableBucket.set(JSON.toJSONString(table));
        tableBucket.expire(DEFAULT_CACHED_TABLE_HOURS, TimeUnit.HOURS);
        return table;
    }

    private void evictTableCache(Long tableId) {
        String tableIdKey = RedisKeyUtil.makeOnlineTableKey(tableId);
        redissonClient.getBucket(tableIdKey).delete();
    }
}
