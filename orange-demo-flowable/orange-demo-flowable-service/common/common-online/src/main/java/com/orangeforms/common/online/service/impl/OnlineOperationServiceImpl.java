package com.orangeforms.common.online.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.orangeforms.common.core.annotation.MyDataSourceResolver;
import com.orangeforms.common.core.constant.AggregationType;
import com.orangeforms.common.core.exception.NoDataPermException;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.object.MyWhereCriteria;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.core.object.Tuple2;
import com.orangeforms.common.core.util.RedisKeyUtil;
import com.orangeforms.common.datafilter.constant.DataPermRuleType;
import com.orangeforms.common.datafilter.config.DataFilterProperties;
import com.orangeforms.common.online.model.constant.*;
import com.orangeforms.common.online.service.OnlineVirtualColumnService;
import com.orangeforms.common.online.util.OnlineOperationHelper;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.online.util.OnlineDataSourceResolver;
import com.orangeforms.common.online.util.OnlineConstant;
import com.orangeforms.common.online.dao.OnlineOperationMapper;
import com.orangeforms.common.online.dto.OnlineFilterDto;
import com.orangeforms.common.online.model.*;
import com.orangeforms.common.online.object.ColumnData;
import com.orangeforms.common.online.object.JoinTableInfo;
import com.orangeforms.common.online.service.OnlineDictService;
import com.orangeforms.common.online.service.OnlineOperationService;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线表单运行时数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@MyDataSourceResolver(resolver = OnlineDataSourceResolver.class)
@Slf4j
@Service("onlineOperationService")
public class OnlineOperationServiceImpl implements OnlineOperationService {

    @Autowired
    private OnlineOperationMapper onlineOperationMapper;
    @Autowired
    private OnlineDictService onlineDictService;
    @Autowired
    private OnlineVirtualColumnService onlineVirtualColumnService;
    @Autowired
    private OnlineOperationHelper onlineOperationHelper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private DataFilterProperties dataFilterProperties;

    /**
     * 聚合返回数据中，聚合键的常量字段名。
     * 如select groupColumn groupedKey, max(aggregationColumn) aggregatedValue。
     */
    private static final String KEY_NAME = "groupedKey";
    /**
     * 聚合返回数据中，聚合值的常量字段名。
     * 如select groupColumn groupedKey, max(aggregationColumn) aggregatedValue。
     */
    private static final String VALUE_NAME = "aggregatedValue";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object saveNew(OnlineTable table, List<ColumnData> columnDataList) {
        String tableName = table.getTableName();
        String columnNames = this.makeColumnNames(columnDataList);
        List<Object> columnValueList = new LinkedList<>();
        Object id = null;
        // 这里逐个处理每一行数据，特别是非自增主键、createUserId、createTime、逻辑删除等特殊属性的字段。
        for (ColumnData columnData : columnDataList) {
            if (!columnData.getColumn().getAutoIncrement()) {
                this.makeupColumnValue(columnData);
                columnValueList.add(columnData.getColumnValue());
                if (columnData.getColumn().getPrimaryKey()) {
                    id = columnData.getColumnValue();
                }
            }
        }
        onlineOperationMapper.insert(tableName, columnNames, columnValueList);
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object saveNewAndSlaveRelation(
            OnlineTable masterTable,
            List<ColumnData> columnDataList,
            Map<OnlineDatasourceRelation, List<List<ColumnData>>> slaveDataListMap) {
        Object id = this.saveNew(masterTable, columnDataList);
        // 迭代多个一对多关联。
        for (Map.Entry<OnlineDatasourceRelation, List<List<ColumnData>>> entry : slaveDataListMap.entrySet()) {
            Long masterColumnId = entry.getKey().getMasterColumnId();
            ColumnData masterColumnData = null;
            for (ColumnData columnData : columnDataList) {
                if (columnData.getColumn().getColumnId().equals(masterColumnId)) {
                    masterColumnData = columnData;
                    break;
                }
            }
            Long slaveColumnId = entry.getKey().getSlaveColumnId();
            // 迭代一对多关联中的数据集合
            for (List<ColumnData> slaveColumnDataList : entry.getValue()) {
                // 迭代一对多关联记录的字段列表。
                for (ColumnData slaveColumnData : slaveColumnDataList) {
                    if (slaveColumnData.getColumn().getColumnId().equals(slaveColumnId)) {
                        slaveColumnData.setColumnValue(masterColumnData.getColumnValue());
                        break;
                    }
                }
                this.saveNew(entry.getKey().getSlaveTable(), slaveColumnDataList);
            }
        }
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(OnlineTable table, List<ColumnData> columnDataList) {
        String tableName = table.getTableName();
        List<ColumnData> updateColumnList = new LinkedList<>();
        List<ColumnData> whereColumnList = new LinkedList<>();
        for (ColumnData columnData : columnDataList) {
            this.makeupColumnValue(columnData);
            // 对于以下几种类型的字段，忽略更新。
            if (columnData.getColumn().getPrimaryKey()
                    || ObjectUtil.equal(columnData.getColumn().getFieldKind(), FieldKind.LOGIC_DELETE)) {
                whereColumnList.add(columnData);
                continue;
            }
            if (ObjectUtil.notEqual(columnData.getColumn().getFieldKind(), FieldKind.CREATE_TIME)
                    && ObjectUtil.notEqual(columnData.getColumn().getFieldKind(), FieldKind.CREATE_USER_ID)) {
                updateColumnList.add(columnData);
            }
        }
        if (CollUtil.isEmpty(updateColumnList)) {
            return true;
        }
        String dataPermFilter = this.buildDataPermFilter(table);
        return onlineOperationMapper.update(tableName, updateColumnList, whereColumnList, dataPermFilter) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public <T> boolean updateColumn(OnlineTable table, String dataId, OnlineColumn column, T dataValue) {
        List<ColumnData> whereColumnList = new LinkedList<>();
        if (table.getLogicDeleteColumn() != null) {
            ColumnData logicDeleteColumnData = new ColumnData();
            logicDeleteColumnData.setColumn(table.getLogicDeleteColumn());
            logicDeleteColumnData.setColumnValue(GlobalDeletedFlag.NORMAL);
            whereColumnList.add(logicDeleteColumnData);
        }
        ColumnData primaryKeyColumnData = new ColumnData();
        primaryKeyColumnData.setColumn(table.getPrimaryKeyColumn());
        primaryKeyColumnData.setColumnValue(
                onlineOperationHelper.convertToTypeValue(table.getPrimaryKeyColumn(), dataId));
        whereColumnList.add(primaryKeyColumnData);
        List<ColumnData> updateColumnList = new LinkedList<>();
        ColumnData updateColumnData = new ColumnData();
        updateColumnData.setColumn(column);
        updateColumnData.setColumnValue(dataValue);
        updateColumnList.add(updateColumnData);
        List<OnlineFilterDto> filterList =
                this.makeDefaultFilter(table, table.getPrimaryKeyColumn(), dataId);
        String dataPermFilter = this.buildDataPermFilter(table);
        return onlineOperationMapper.update(
                table.getTableName(), updateColumnList, whereColumnList, dataPermFilter) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(OnlineTable table, List<OnlineDatasourceRelation> relationList, String dataId) {
        Map<String, Object> masterData = null;
        if (CollUtil.isNotEmpty(relationList)) {
            for (OnlineDatasourceRelation relation : relationList) {
                if (relation.getCascadeDelete()
                        && !relation.getMasterColumnId().equals(table.getPrimaryKeyColumn().getColumnId())) {
                    masterData = getMasterData(table, null, null, dataId);
                    break;
                }
            }
        }
        List<OnlineFilterDto> filterList =
                this.makeDefaultFilter(table, table.getPrimaryKeyColumn(), dataId);
        String dataPermFilter = this.buildDataPermFilter(table);
        if (table.getLogicDeleteColumn() == null) {
            if (onlineOperationMapper.delete(table.getTableName(), filterList, dataPermFilter) != 1) {
                return false;
            }
        } else {
            if (!this.doLogicDelete(table, table.getPrimaryKeyColumn(), dataId, dataPermFilter)) {
                return false;
            }
        }
        if (CollUtil.isEmpty(relationList)) {
            return true;
        }
        for (OnlineDatasourceRelation relation : relationList) {
            if (!relation.getCascadeDelete()) {
                continue;
            }
            OnlineTable slaveTable = relation.getSlaveTable();
            OnlineColumn slaveColumn =
                    relation.getSlaveTable().getColumnMap().get(relation.getSlaveColumnId());
            String columnValue = dataId;
            if (!relation.getMasterColumnId().equals(table.getPrimaryKeyColumn().getColumnId())) {
                OnlineColumn relationMasterColumn = table.getColumnMap().get(relation.getMasterColumnId());
                columnValue = masterData.get(relationMasterColumn.getColumnName()).toString();
            }
            if (slaveTable.getLogicDeleteColumn() == null) {
                List<OnlineFilterDto> slaveFilterList =
                        this.makeDefaultFilter(relation.getSlaveTable(), slaveColumn, columnValue);
                onlineOperationMapper.delete(slaveTable.getTableName(), slaveFilterList, null);
            } else {
                this.doLogicDelete(slaveTable, slaveColumn, columnValue, null);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void forceDelete(OnlineTable table, OnlineColumn column, String columnValue) {
        List<OnlineFilterDto> filterList = this.makeDefaultFilter(table, column, columnValue);
        onlineOperationMapper.delete(table.getTableName(), filterList, null);
    }

    @Override
    public Map<String, Object> getMasterData(
            OnlineTable table,
            List<OnlineDatasourceRelation> oneToOneRelationList,
            List<OnlineDatasourceRelation> allRelationList,
            String dataId) {
        List<OnlineFilterDto> filterList =
                this.makeDefaultFilter(table, table.getPrimaryKeyColumn(), dataId);
        // 组件表关联数据。
        List<JoinTableInfo> joinInfoList = this.makeJoinInfoList(table, oneToOneRelationList);
        // 拼接关联表的select fields字段。
        String selectFields = this.makeSelectFields(table, oneToOneRelationList);
        String dataPermFilter = this.buildDataPermFilter(table);
        List<Map<String, Object>> resultList = onlineOperationMapper.getList(
                table.getTableName(), joinInfoList, selectFields, filterList, dataPermFilter, null);
        this.buildDataListWithDict(resultList, table, oneToOneRelationList);
        if (CollUtil.isEmpty(resultList)) {
            return null;
        }
        if (CollUtil.isNotEmpty(allRelationList)) {
            // 针对一对多和多对多关联，计算虚拟聚合字段。
            List<OnlineDatasourceRelation> toManyRelationList = allRelationList.stream()
                    .filter(r -> !r.getRelationType().equals(RelationType.ONE_TO_ONE)).collect(Collectors.toList());
            this.buildVirtualColumn(resultList, table, toManyRelationList);
        }
        return resultList.get(0);
    }

    @Override
    public Map<String, Object> getSlaveData(OnlineDatasourceRelation relation, String dataId) {
        OnlineTable slaveTable = relation.getSlaveTable();
        List<OnlineFilterDto> filterList =
                this.makeDefaultFilter(slaveTable, slaveTable.getPrimaryKeyColumn(), dataId);
        // 拼接关联表的select fields字段。
        String selectFields = this.makeSelectFields(slaveTable, relation.getVariableName());
        String dataPermFilter = this.buildDataPermFilter(slaveTable);
        List<Map<String, Object>> resultList = onlineOperationMapper.getList(
                slaveTable.getTableName(), null, selectFields, filterList, dataPermFilter, null);
        this.buildDataListWithDict(resultList, slaveTable, relation.getVariableName());
        return CollUtil.isEmpty(resultList) ? null : resultList.get(0);
    }

    @Override
    public List<Map<String, Object>> getMasterDataList(
            OnlineTable table,
            List<OnlineDatasourceRelation> oneToOneRelationList,
            List<OnlineDatasourceRelation> allRelationList,
            List<OnlineFilterDto> filterList,
            String orderBy) {
        // 如果主表中包含逻辑删除，需要在这里补充到过滤字段中。
        if (table.getLogicDeleteColumn() != null) {
            if (filterList == null) {
                filterList = new LinkedList<>();
            }
            OnlineFilterDto filter = new OnlineFilterDto();
            filter.setTableName(table.getTableName());
            filter.setColumnName(table.getLogicDeleteColumn().getColumnName());
            filter.setColumnValue(GlobalDeletedFlag.NORMAL);
            filterList.add(filter);
        }
        // 组件表关联数据。
        List<JoinTableInfo> joinInfoList = this.makeJoinInfoList(table, oneToOneRelationList);
        // 拼接关联表的select fields字段。
        String selectFields = this.makeSelectFields(table, oneToOneRelationList);
        String dataPermFilter = this.buildDataPermFilter(table);
        List<Map<String, Object>> resultList = onlineOperationMapper.getList(
                table.getTableName(), joinInfoList, selectFields, filterList, dataPermFilter, orderBy);
        this.buildDataListWithDict(resultList, table, oneToOneRelationList);
        // 针对一对多和多对多关联，计算虚拟聚合字段。
        if (CollUtil.isNotEmpty(allRelationList)) {
            List<OnlineDatasourceRelation> toManyRelationList = allRelationList.stream()
                    .filter(r -> !r.getRelationType().equals(RelationType.ONE_TO_ONE)).collect(Collectors.toList());
            this.buildVirtualColumn(resultList, table, toManyRelationList);
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getSlaveDataList(
            OnlineDatasourceRelation relation, List<OnlineFilterDto> filterList, String orderBy) {
        OnlineTable slaveTable = relation.getSlaveTable();
        // 如果主表中包含逻辑删除，需要在这里补充到过滤字段中。
        if (slaveTable.getLogicDeleteColumn() != null) {
            if (filterList == null) {
                filterList = new LinkedList<>();
            }
            OnlineFilterDto filter = new OnlineFilterDto();
            filter.setTableName(slaveTable.getTableName());
            filter.setColumnName(slaveTable.getLogicDeleteColumn().getColumnName());
            filter.setColumnValue(GlobalDeletedFlag.NORMAL);
            filterList.add(filter);
        }
        // 拼接关联表的select fields字段。
        String selectFields = this.makeSelectFields(slaveTable, relation.getVariableName());
        String dataPermFilter = this.buildDataPermFilter(slaveTable);
        List<Map<String, Object>> resultList = onlineOperationMapper.getList(
                slaveTable.getTableName(), null, selectFields, filterList, dataPermFilter, orderBy);
        this.buildDataListWithDict(resultList, slaveTable, relation.getVariableName());
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getDictDataList(OnlineDict dict, List<OnlineFilterDto> filterList) {
        if (StrUtil.isNotEmpty(dict.getDeletedColumnName())) {
            if (filterList == null) {
                filterList = new LinkedList<>();
            }
            OnlineFilterDto filter = new OnlineFilterDto();
            filter.setColumnName(dict.getDeletedColumnName());
            filter.setColumnValue(GlobalDeletedFlag.NORMAL);
            filterList.add(filter);
        }
        String selectFields = this.makeDictSelectFields(dict, false);
        String dataPermFilter = this.buildDataPermFilter(
                dict.getTableName(), dict.getDeptFilterColumnName(), dict.getUserFilterColumnName());
        return onlineOperationMapper.getDictList(dict.getTableName(), selectFields, filterList, dataPermFilter);
    }

    private void buildVirtualColumn(
            List<Map<String, Object>> resultList, OnlineTable table, List<OnlineDatasourceRelation> relationList) {
        if (CollUtil.isEmpty(resultList) || CollUtil.isEmpty(relationList)) {
            return;
        }
        OnlineVirtualColumn virtualColumnFilter = new OnlineVirtualColumn();
        virtualColumnFilter.setTableId(table.getTableId());
        virtualColumnFilter.setVirtualType(VirtualType.AGGREGATION);
        List<OnlineVirtualColumn> virtualColumnList =
                onlineVirtualColumnService.getOnlineVirtualColumnList(virtualColumnFilter, null);
        if (CollUtil.isEmpty(virtualColumnList)) {
            return;
        }
        Map<Long, OnlineDatasourceRelation> relationMap =
                relationList.stream().collect(Collectors.toMap(OnlineDatasourceRelation::getRelationId, r -> r));
        for (OnlineVirtualColumn virtualColumn : virtualColumnList) {
            OnlineDatasourceRelation relation = relationMap.get(virtualColumn.getRelationId());
            if (relation.getRelationType().equals(RelationType.ONE_TO_MANY)) {
                this.doBuildVirtualColumnForOneToMany(table, resultList, virtualColumn, relation);
            }
        }
    }

    private void doBuildVirtualColumnForOneToMany(
            OnlineTable masterTable,
            List<Map<String, Object>> resultList,
            OnlineVirtualColumn virtualColumn,
            OnlineDatasourceRelation relation) {
        String slaveTableName = relation.getSlaveTable().getTableName();
        OnlineColumn slaveColumn =
                relation.getSlaveTable().getColumnMap().get(relation.getSlaveColumnId());
        String slaveColumnName = slaveColumn.getColumnName();
        OnlineColumn aggregationColumn =
                relation.getSlaveTable().getColumnMap().get(virtualColumn.getAggregationColumnId());
        String aggregationColumnName = aggregationColumn.getColumnName();
        Tuple2<String, String> selectAndGroupByTuple = makeSelectListAndGroupByClause(
                slaveTableName, slaveColumnName, slaveTableName, aggregationColumnName, virtualColumn.getAggregationType());
        String selectList = selectAndGroupByTuple.getFirst();
        String groupBy = selectAndGroupByTuple.getSecond();
        // 开始组装过滤从句。
        List<MyWhereCriteria> criteriaList = new LinkedList<>();
        // 1. 组装主表数据对从表的过滤条件。
        MyWhereCriteria inlistFilter = new MyWhereCriteria();
        OnlineColumn masterColumn = masterTable.getColumnMap().get(relation.getMasterColumnId());
        String masterColumnName = masterColumn.getColumnName();
        Set<Object> masterIdSet = resultList.stream()
                .map(r -> r.get(masterColumnName)).filter(Objects::nonNull).collect(Collectors.toSet());
        inlistFilter.setCriteria(
                slaveTableName, slaveColumnName, slaveColumn.getObjectFieldType(), MyWhereCriteria.OPERATOR_IN, masterIdSet);
        criteriaList.add(inlistFilter);
        // 2. 从表逻辑删除字段过滤。
        if (relation.getSlaveTable().getLogicDeleteColumn() != null) {
            MyWhereCriteria deleteFilter = new MyWhereCriteria();
            deleteFilter.setCriteria(
                    slaveTableName,
                    relation.getSlaveTable().getLogicDeleteColumn().getColumnName(),
                    relation.getSlaveTable().getLogicDeleteColumn().getObjectFieldType(),
                    MyWhereCriteria.OPERATOR_EQUAL,
                    GlobalDeletedFlag.NORMAL);
            criteriaList.add(deleteFilter);
        }
        if (StrUtil.isNotBlank(virtualColumn.getWhereClauseJson())) {
            List<VirtualColumnWhereClause> whereClauseList =
                    JSONArray.parseArray(virtualColumn.getWhereClauseJson(), VirtualColumnWhereClause.class);
            if (CollUtil.isNotEmpty(whereClauseList)) {
                for (VirtualColumnWhereClause whereClause : whereClauseList) {
                    MyWhereCriteria whereClauseFilter = new MyWhereCriteria();
                    OnlineColumn c = relation.getSlaveTable().getColumnMap().get(whereClause.getColumnId());
                    whereClauseFilter.setCriteria(
                            slaveTableName,
                            c.getColumnName(),
                            c.getObjectFieldType(),
                            whereClause.getOperatorType(),
                            whereClause.getValue());
                    criteriaList.add(whereClauseFilter);
                }
            }
        }
        String criteriaString = MyWhereCriteria.makeCriteriaString(criteriaList);
        List<Map<String, Object>> aggregationMapList =
                onlineOperationMapper.getGroupedListByCondition(slaveTableName, selectList, criteriaString, groupBy);
        this.doMakeAggregationData(resultList, aggregationMapList, masterColumnName, virtualColumn.getObjectFieldName());
    }

    private void doMakeAggregationData(
            List<Map<String, Object>> resultList,
            List<Map<String, Object>> aggregationMapList,
            String masterColumnName,
            String virtualColumnName) {
        // 根据获取的分组聚合结果集，绑定到主表总的关联字段。
        if (CollectionUtils.isEmpty(aggregationMapList)) {
            return;
        }
        Map<Object, Object> relatedMap = new HashMap<>(aggregationMapList.size());
        for (Map<String, Object> map : aggregationMapList) {
            relatedMap.put(map.get(KEY_NAME), map.get(VALUE_NAME));
        }
        for (Map<String, Object> dataObject : resultList) {
            Object masterIdValue = dataObject.get(masterColumnName);
            if (masterIdValue != null) {
                Object value = relatedMap.get(masterIdValue);
                if (value != null) {
                    dataObject.put(virtualColumnName, value);
                }
            }
        }
    }

    private Tuple2<String, String> makeSelectListAndGroupByClause(
            String groupTableName,
            String groupColumnName,
            String aggregationTableName,
            String aggregationColumnName,
            Integer aggregationType) {
        String aggregationFunc = AggregationType.getAggregationFunction(aggregationType);
        // 构建Select List
        // 如：r_table.master_id groupedKey, SUM(r_table.aggr_column) aggregated_value
        StringBuilder groupedSelectList = new StringBuilder(128);
        groupedSelectList.append(groupTableName)
                .append(".")
                .append(groupColumnName)
                .append(" ")
                .append(KEY_NAME)
                .append(", ")
                .append(aggregationFunc)
                .append("(")
                .append(aggregationTableName)
                .append(".")
                .append(aggregationColumnName)
                .append(") ")
                .append(VALUE_NAME)
                .append(" ");
        StringBuilder groupBy = new StringBuilder(64);
        groupBy.append(groupTableName).append(".").append(groupColumnName);
        return new Tuple2<>(groupedSelectList.toString(), groupBy.toString());
    }

    private void buildDataListWithDict(
            List<Map<String, Object>> resultList, OnlineTable slaveTable, String relationVariableName) {
        if (CollUtil.isEmpty(resultList)) {
            return;
        }
        Set<Long> dictIdSet = new HashSet<>();
        // 先找主表字段对字典的依赖。
        Multimap<Long, String> dictColumnMap = LinkedHashMultimap.create();
        for (OnlineColumn column : slaveTable.getColumnMap().values()) {
            if (column.getDictId() != null) {
                dictIdSet.add(column.getDictId());
                dictColumnMap.put(column.getDictId(),
                        relationVariableName + OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR + column.getColumnName());
            }
        }
        this.doBuildDataListWithDict(resultList, dictIdSet, dictColumnMap);
    }

    private void buildDataListWithDict(
            List<Map<String, Object>> resultList, OnlineTable masterTable, List<OnlineDatasourceRelation> relationList) {
        if (CollUtil.isEmpty(resultList)) {
            return;
        }
        Set<Long> dictIdSet = new HashSet<>();
        // 先找主表字段对字典的依赖。
        Multimap<Long, String> dictColumnMap = LinkedHashMultimap.create();
        for (OnlineColumn column : masterTable.getColumnMap().values()) {
            if (column.getDictId() != null) {
                dictIdSet.add(column.getDictId());
                dictColumnMap.put(column.getDictId(), column.getColumnName());
            }
        }
        // 再找关联表字段对字典的依赖。
        if (CollUtil.isNotEmpty(relationList)) {
            for (OnlineDatasourceRelation relation : relationList) {
                for (OnlineColumn column : relation.getSlaveTable().getColumnMap().values()) {
                    if (column.getDictId() != null) {
                        dictIdSet.add(column.getDictId());
                        dictColumnMap.put(column.getDictId(),
                                relation.getVariableName() + OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR + column.getColumnName());
                    }
                }
            }
        }
        this.doBuildDataListWithDict(resultList, dictIdSet, dictColumnMap);
    }

    private void doBuildDataListWithDict(
            List<Map<String, Object>> resultList, Set<Long> dictIdSet, Multimap<Long, String> dictColumnMap) {
        if (CollUtil.isEmpty(dictIdSet)) {
            return;
        }
        List<OnlineDict> dictList = onlineDictService.getOnlineDictList(dictIdSet)
                .stream().filter(d -> d.getDictType() == DictType.TABLE).collect(Collectors.toList());
        for (OnlineDict dict : dictList) {
            Collection<String> columnNameList = dictColumnMap.get(dict.getDictId());
            for (String columnName : columnNameList) {
                Set<Object> dictIdDataSet = new HashSet<>();
                for (Map<String, Object> result : resultList) {
                    Object dictIdData = result.get(columnName);
                    if (ObjectUtil.isNotEmpty(dictIdData)) {
                        dictIdDataSet.add(dictIdData);
                    }
                }
                if (CollUtil.isEmpty(dictIdDataSet)) {
                    continue;
                }
                String selectFields = this.makeDictSelectFields(dict, true);
                List<OnlineFilterDto> filterList = new LinkedList<>();
                if (StrUtil.isNotBlank(dict.getDeletedColumnName())) {
                    OnlineFilterDto filter = new OnlineFilterDto();
                    filter.setColumnName(dict.getDeletedColumnName());
                    filter.setColumnValue(GlobalDeletedFlag.NORMAL);
                    filterList.add(filter);
                }
                OnlineFilterDto inlistFilter = new OnlineFilterDto();
                inlistFilter.setColumnName(dict.getKeyColumnName());
                inlistFilter.setColumnValueList(dictIdDataSet);
                inlistFilter.setFilterType(FieldFilterType.IN_LIST_FILTER);
                filterList.add(inlistFilter);
                List<Map<String, Object>> dictResultList =
                        onlineOperationMapper.getDictList(dict.getTableName(), selectFields, filterList, null);
                if (CollUtil.isEmpty(dictResultList)) {
                    continue;
                }
                Map<Object, Object> dictResultMap = new HashMap<>(dictResultList.size());
                for (Map<String, Object> dictResult : dictResultList) {
                    dictResultMap.put(dictResult.get("id"), dictResult.get("name"));
                }
                String dictKeyName = columnName + "__DictMap";
                for (Map<String, Object> result : resultList) {
                    Object dictIdData = result.get(columnName);
                    Object dictNameData = dictResultMap.get(dictIdData);
                    Map<String, Object> dictMap = new HashMap<>(2);
                    dictMap.put("id", dictIdData);
                    dictMap.put("name", dictNameData);
                    result.put(dictKeyName, dictMap);
                }
            }
        }
    }

    private List<JoinTableInfo> makeJoinInfoList(
            OnlineTable masterTable, List<OnlineDatasourceRelation> relationList) {
        if (CollUtil.isEmpty(relationList)) {
            return null;
        }
        Map<Long, OnlineColumn> masterTableColumnMap = masterTable.getColumnMap();
        List<JoinTableInfo> joinInfoList = new LinkedList<>();
        for (OnlineDatasourceRelation relation : relationList) {
            JoinTableInfo joinInfo = new JoinTableInfo();
            joinInfo.setLeftJoin(relation.getLeftJoin());
            joinInfo.setJoinTableName(relation.getSlaveTable().getTableName());
            // 根据配置动态拼接JOIN的关联条件，同时要考虑从表的逻辑删除过滤。
            OnlineColumn masterColumn = masterTableColumnMap.get(relation.getMasterColumnId());
            OnlineColumn slaveColumn = relation.getSlaveTable().getColumnMap().get(relation.getSlaveColumnId());
            StringBuilder conditionBuilder = new StringBuilder(64);
            conditionBuilder
                    .append(masterTable.getTableName())
                    .append(".")
                    .append(masterColumn.getColumnName())
                    .append(" = ")
                    .append(relation.getSlaveTable().getTableName())
                    .append(".")
                    .append(slaveColumn.getColumnName());
            if (relation.getSlaveTable().getLogicDeleteColumn() != null) {
                conditionBuilder
                        .append(" AND ")
                        .append(relation.getSlaveTable().getTableName())
                        .append(".")
                        .append(relation.getSlaveTable().getLogicDeleteColumn().getColumnName())
                        .append(" = ")
                        .append(GlobalDeletedFlag.NORMAL);
            }
            joinInfo.setJoinCondition(conditionBuilder.toString());
            joinInfoList.add(joinInfo);
        }
        return joinInfoList;
    }

    private String makeSelectFields(OnlineTable slaveTable, String relationVariableName) {
        StringBuilder selectFieldBuider = new StringBuilder(512);
        // 拼装主表的select fields字段。
        for (OnlineColumn column : slaveTable.getColumnMap().values()) {
            OnlineColumn deletedColumn = slaveTable.getLogicDeleteColumn();
            if (deletedColumn != null && StrUtil.equals(column.getColumnName(), deletedColumn.getColumnName())) {
                continue;
            }
            if (this.castToInteger(column)) {
                selectFieldBuider
                        .append("CAST(")
                        .append(slaveTable.getTableName())
                        .append(".")
                        .append(column.getColumnName())
                        .append(" AS SIGNED) ")
                        .append(relationVariableName)
                        .append(OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR)
                        .append(column.getColumnName())
                        .append(",");
            } else {
                selectFieldBuider
                        .append(slaveTable.getTableName())
                        .append(".")
                        .append(column.getColumnName())
                        .append(" ")
                        .append(relationVariableName)
                        .append(OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR)
                        .append(column.getColumnName())
                        .append(",");
            }
        }
        return selectFieldBuider.substring(0, selectFieldBuider.length() - 1);
    }

    private String makeSelectFields(OnlineTable masterTable, List<OnlineDatasourceRelation> relationList) {
        StringBuilder selectFieldBuider = new StringBuilder(512);
        if (CollUtil.isNotEmpty(relationList)) {
            for (OnlineDatasourceRelation relation : relationList) {
                OnlineTable slaveTable = relation.getSlaveTable();
                Collection<OnlineColumn> columnList = slaveTable.getColumnMap().values();
                for (OnlineColumn column : columnList) {
                    OnlineColumn deletedColumn = slaveTable.getLogicDeleteColumn();
                    if (deletedColumn != null && StrUtil.equals(column.getColumnName(), deletedColumn.getColumnName())) {
                        continue;
                    }
                    if (this.castToInteger(column)) {
                        selectFieldBuider
                                .append("CAST(")
                                .append(slaveTable.getTableName())
                                .append(".")
                                .append(column.getColumnName())
                                .append(" AS SIGNED) ")
                                .append(relation.getVariableName())
                                .append(OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR)
                                .append(column.getColumnName())
                                .append(",");
                    } else {
                        selectFieldBuider
                                .append(slaveTable.getTableName())
                                .append(".")
                                .append(column.getColumnName())
                                .append(" ")
                                .append(relation.getVariableName())
                                .append(OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR)
                                .append(column.getColumnName())
                                .append(",");
                    }
                }
            }
        }
        // 拼装主表的select fields字段。
        for (OnlineColumn column : masterTable.getColumnMap().values()) {
            OnlineColumn deletedColumn = masterTable.getLogicDeleteColumn();
            if (deletedColumn != null && StrUtil.equals(column.getColumnName(), deletedColumn.getColumnName())) {
                continue;
            }
            if (this.castToInteger(column)) {
                selectFieldBuider
                        .append("CAST(")
                        .append(masterTable.getTableName())
                        .append(".")
                        .append(column.getColumnName())
                        .append(" AS SIGNED) ")
                        .append(column.getColumnName())
                        .append(",");
            } else {
                selectFieldBuider
                        .append(masterTable.getTableName())
                        .append(".")
                        .append(column.getColumnName())
                        .append(",");
            }
        }
        return selectFieldBuider.substring(0, selectFieldBuider.length() - 1);
    }

    private String makeDictSelectFields(OnlineDict onlineDict, boolean ignoreParentId) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(onlineDict.getKeyColumnName()).append(" id, ");
        sb.append(onlineDict.getValueColumnName()).append(" name");
        if (!ignoreParentId && onlineDict.getTreeFlag()) {
            sb.append(", ").append(onlineDict.getParentKeyColumnName()).append(" parentId");
        }
        return sb.toString();
    }

    private boolean castToInteger(OnlineColumn column) {
        return "tinyint(1)".equals(column.getFullColumnType());
    }

    private String makeColumnNames(List<ColumnData> columnDataList) {
        StringBuilder sb = new StringBuilder(512);
        for (ColumnData columnData : columnDataList) {
            if (columnData.getColumn().getAutoIncrement()) {
                continue;
            }
            sb.append(columnData.getColumn().getColumnName()).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private void makeupColumnValue(ColumnData columnData) {
        if (columnData.getColumn().getAutoIncrement()) {
            return;
        }
        if (columnData.getColumn().getPrimaryKey()) {
            if (columnData.getColumnValue() == null) {
                if ("Long".equals(columnData.getColumn().getObjectFieldType())) {
                    columnData.setColumnValue(idGenerator.nextLongId());
                } else {
                    columnData.setColumnValue(idGenerator.nextStringId());
                }
            }
        } else if (columnData.getColumn().getFieldKind() != null) {
            switch (columnData.getColumn().getFieldKind()) {
                case FieldKind.CREATE_TIME:
                case FieldKind.UPDATE_TIME:
                    columnData.setColumnValue(new Date());
                    break;
                case FieldKind.CREATE_USER_ID:
                case FieldKind.UPDATE_USER_ID:
                    columnData.setColumnValue(TokenData.takeFromRequest().getUserId());
                    break;
                case FieldKind.LOGIC_DELETE:
                    columnData.setColumnValue(GlobalDeletedFlag.NORMAL);
                    break;
                default:
                    break;
            }
        }
    }

    private List<OnlineFilterDto> makeDefaultFilter(OnlineTable table, OnlineColumn column, String columnValue) {
        List<OnlineFilterDto> filterList = new LinkedList<>();
        OnlineFilterDto dataIdFilter = new OnlineFilterDto();
        dataIdFilter.setTableName(table.getTableName());
        dataIdFilter.setColumnName(column.getColumnName());
        dataIdFilter.setColumnValue(onlineOperationHelper.convertToTypeValue(column, columnValue));
        filterList.add(dataIdFilter);
        if (table.getLogicDeleteColumn() != null) {
            OnlineFilterDto filter = new OnlineFilterDto();
            filter.setTableName(table.getTableName());
            filter.setColumnName(table.getLogicDeleteColumn().getColumnName());
            filter.setColumnValue(GlobalDeletedFlag.NORMAL);
            filterList.add(filter);
        }
        return filterList;
    }

    private boolean doLogicDelete(
            OnlineTable table, OnlineColumn filterColumn, String filterColumnValue, String dataPermFilter) {
        List<ColumnData> updateColumnList = new LinkedList<>();
        ColumnData logicDeleteColumnData = new ColumnData();
        logicDeleteColumnData.setColumn(table.getLogicDeleteColumn());
        logicDeleteColumnData.setColumnValue(GlobalDeletedFlag.DELETED);
        updateColumnList.add(logicDeleteColumnData);
        List<ColumnData> whereColumnList = new LinkedList<>();
        ColumnData relationSlaveColumnData = new ColumnData();
        relationSlaveColumnData.setColumn(filterColumn);
        relationSlaveColumnData.setColumnValue(
                onlineOperationHelper.convertToTypeValue(filterColumn, filterColumnValue));
        whereColumnList.add(relationSlaveColumnData);
        return onlineOperationMapper.update(
                table.getTableName(), updateColumnList, whereColumnList, dataPermFilter) != 0;
    }

    private String buildDataPermFilter(String tableName, String deptFilterColumnName, String userFilterColumnName) {
        if (!dataFilterProperties.getEnabledDataPermFilter()) {
            return null;
        }
        return processDataPerm(tableName, deptFilterColumnName, userFilterColumnName);
    }

    private String buildDataPermFilter(OnlineTable table) {
        if (!dataFilterProperties.getEnabledDataPermFilter()) {
            return null;
        }
        String deptFilterColumnName = null;
        String userFilterColumnName = null;
        for (OnlineColumn column : table.getColumnMap().values()) {
            if (column.getDeptFilter()) {
                deptFilterColumnName = column.getColumnName();
            }
            if (column.getUserFilter()) {
                userFilterColumnName = column.getColumnName();
            }
        }
        return processDataPerm(table.getTableName(), deptFilterColumnName, userFilterColumnName);
    }

    private String processDataPerm(String tableName, String deptFilterColumnName, String userFilterColumnName) {
        TokenData tokenData = TokenData.takeFromRequest();
        if (Boolean.TRUE.equals(tokenData.getIsAdmin())) {
            return null;
        }
        String dataPermSessionKey =
                RedisKeyUtil.makeSessionDataPermIdKey(tokenData.getSessionId());
        RBucket<String> bucket = redissonClient.getBucket(dataPermSessionKey);
        String dataPermData = bucket.get();
        if (StrUtil.isBlank(dataPermData)) {
            throw new NoDataPermException("No Related DataPerm found.");
        }
        Map<Integer, String> dataPermMap = new HashMap<>(8);
        for (Map.Entry<String, Object> entry : JSON.parseObject(dataPermData).entrySet()) {
            dataPermMap.put(Integer.valueOf(entry.getKey()), entry.getValue().toString());
        }
        if (MapUtil.isEmpty(dataPermMap)) {
            throw new NoDataPermException("No Related DataPerm found.");
        }
        if (dataPermMap.containsKey(DataPermRuleType.TYPE_ALL)) {
            return null;
        }
        return doProcessDataPerm(tableName, deptFilterColumnName, userFilterColumnName, dataPermMap);
    }

    private String doProcessDataPerm(
            String tableName, String deptFilterColumnName, String userFilterColumnName, Map<Integer, String> dataPermMap) {
        List<String> criteriaList = new LinkedList<>();
        for (Map.Entry<Integer, String> entry : dataPermMap.entrySet()) {
            String filterClause = processDataPermRule(
                    tableName, deptFilterColumnName, userFilterColumnName, entry.getKey(), entry.getValue());
            if (StrUtil.isNotBlank(filterClause)) {
                criteriaList.add(filterClause);
            }
        }
        if (CollUtil.isEmpty(criteriaList)) {
            return null;
        }
        StringBuilder filterBuilder = new StringBuilder(128);
        filterBuilder.append("(");
        filterBuilder.append(CollUtil.join(criteriaList, " OR "));
        filterBuilder.append(")");
        return filterBuilder.toString();
    }

    private String processDataPermRule(
            String tableName, String deptFilterColumnName, String userFilterColumnName, Integer ruleType, String deptIds) {
        TokenData tokenData = TokenData.takeFromRequest();
        StringBuilder filter = new StringBuilder(128);
        if (ruleType == DataPermRuleType.TYPE_USER_ONLY) {
            if (StrUtil.isNotBlank(userFilterColumnName)) {
                if (dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(tableName).append(".");
                }
                filter.append(userFilterColumnName)
                        .append(" = ")
                        .append(tokenData.getUserId());
            }
        } else {
            if (StrUtil.isNotBlank(deptFilterColumnName)) {
                if (ruleType == DataPermRuleType.TYPE_DEPT_ONLY) {
                    if (dataFilterProperties.getAddTableNamePrefix()) {
                        filter.append(tableName).append(".");
                    }
                    filter.append(deptFilterColumnName)
                            .append(" = ")
                            .append(tokenData.getDeptId());
                } else if (ruleType == DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT) {
                    filter.append(" EXISTS ")
                            .append("(SELECT 1 FROM ")
                            .append(dataFilterProperties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation WHERE ")
                            .append(dataFilterProperties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.parent_dept_id = ")
                            .append(tokenData.getDeptId())
                            .append(" AND ");
                    if (dataFilterProperties.getAddTableNamePrefix()) {
                        filter.append(tableName).append(".");
                    }
                    filter.append(deptFilterColumnName)
                            .append(" = ")
                            .append(dataFilterProperties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.dept_id) ");
                } else if (ruleType == DataPermRuleType.TYPE_MULTI_DEPT_AND_CHILD_DEPT) {
                    filter.append(" EXISTS ")
                            .append("(SELECT 1 FROM ")
                            .append(dataFilterProperties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation WHERE ")
                            .append(dataFilterProperties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.parent_dept_id IN (")
                            .append(deptIds)
                            .append(") AND ");
                    if (dataFilterProperties.getAddTableNamePrefix()) {
                        filter.append(tableName).append(".");
                    }
                    filter.append(deptFilterColumnName)
                            .append(" = ")
                            .append(dataFilterProperties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.dept_id) ");
                } else if (ruleType == DataPermRuleType.TYPE_CUSTOM_DEPT_LIST) {
                    if (dataFilterProperties.getAddTableNamePrefix()) {
                        filter.append(tableName).append(".");
                    }
                    filter.append(deptFilterColumnName)
                            .append(" IN (")
                            .append(deptIds)
                            .append(") ");
                }
            }
        }
        return filter.toString();
    }

    @Data
    private static class VirtualColumnWhereClause {
        private Long tableId;
        private Long columnId;
        private Integer operatorType;
        private Object value;
    }
}
