package com.orangeforms.common.online.service;

import com.orangeforms.common.online.dto.OnlineFilterDto;
import com.orangeforms.common.online.model.OnlineColumn;
import com.orangeforms.common.online.model.OnlineDatasourceRelation;
import com.orangeforms.common.online.model.OnlineDict;
import com.orangeforms.common.online.model.OnlineTable;
import com.orangeforms.common.online.object.ColumnData;

import java.util.List;
import java.util.Map;

/**
 * 在线表单运行时操作的数据服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineOperationService {

    /**
     * 待插入的所有表数据。
     *
     * @param table          在线表对象。
     * @param columnDataList 数据字段列表。
     * @return 主键值。由于自增主键不能获取插入后的主键值，因此返回NULL。
     */
    Object saveNew(OnlineTable table, List<ColumnData> columnDataList);

    /**
     * 待插入的主表数据和多个从表数据。
     *
     * @param masterTable      主表在线表对象。
     * @param columnDataList   主表数据字段数据。
     * @param slaveDataListMap 多个从表的数据字段数据。
     * @return 主表的主键值。由于自增主键不能获取插入后的主键值，因此返回NULL。
     */
    Object saveNewAndSlaveRelation(
            OnlineTable masterTable,
            List<ColumnData> columnDataList,
            Map<OnlineDatasourceRelation, List<List<ColumnData>>> slaveDataListMap);

    /**
     * 更新表数据。
     *
     * @param table          在线表对象。
     * @param columnDataList 单条表数据的字段数据列表。
     * @return true 更新成功，否则false。
     */
    boolean update(OnlineTable table, List<ColumnData> columnDataList);

    /**
     * 更新流程字段的状态。
     *
     * @param table     数据表。
     * @param dataId    主键Id。
     * @param column    更新字段。
     * @param dataValue 新的数据值。
     * @return true 更新成功，否则false。
     */
    <T> boolean updateColumn(OnlineTable table, String dataId, OnlineColumn column, T dataValue);

    /**
     * 删除主表数据，及其需要级联删除的一对多关联从表数据。
     *
     * @param table        表对象。
     * @param relationList 一对多关联对象列表。
     * @param dataId       主表主键Id值。
     * @return true 删除成功，否则false。
     */
    boolean delete(OnlineTable table, List<OnlineDatasourceRelation> relationList, String dataId);

    /**
     * 强制删除数据，不会指定逻辑删除，只会物理删除。
     *
     * @param table       在线表对象。
     * @param column      指定的字段。
     * @param columnValue 指定字段的值。
     */
    void forceDelete(OnlineTable table, OnlineColumn column, String columnValue);

    /**
     * 从数据源和一对一数据源关联中，动态获取数据。
     *
     * @param table                主表对象。
     * @param oneToOneRelationList 数据源一对一关联列表。
     * @param allRelationList      数据源全部关联列表。
     * @param dataId               主表主键Id值。
     * @return 查询结果。
     */
    Map<String, Object> getMasterData(
            OnlineTable table,
            List<OnlineDatasourceRelation> oneToOneRelationList,
            List<OnlineDatasourceRelation> allRelationList,
            String dataId);

    /**
     * 从一对多数据源关联中，动态获取数据。
     *
     * @param relation 一对多数据源关联对象。
     * @param dataId   一对多关联数据主键Id值。
     * @return 查询结果。
     */
    Map<String, Object> getSlaveData(OnlineDatasourceRelation relation, String dataId);

    /**
     * 从数据源和一对一数据源关联中，动态获取数据列表。
     *
     * @param table                主表对象。
     * @param oneToOneRelationList 数据源一对一关联列表。
     * @param allRelationList      数据源全部关联列表。
     * @param filterList           过滤参数列表。
     * @param orderBy              排序字符串。
     * @return 查询结果集。
     */
    List<Map<String, Object>> getMasterDataList(
            OnlineTable table,
            List<OnlineDatasourceRelation> oneToOneRelationList,
            List<OnlineDatasourceRelation> allRelationList,
            List<OnlineFilterDto> filterList,
            String orderBy);

    /**
     * 从一对多数据源关联中，动态获取数据列表。
     *
     * @param relation   一对多数据源关联对象。
     * @param filterList 过滤参数列表。
     * @param orderBy    排序字符串。
     * @return 查询结果集。
     */
    List<Map<String, Object>> getSlaveDataList(
            OnlineDatasourceRelation relation, List<OnlineFilterDto> filterList, String orderBy);

    /**
     * 从字典对象指向的数据表中查询数据，并根据参数进行数据过滤。
     *
     * @param dict       字典对象。
     * @param filterList 过滤参数列表。
     * @return 查询结果集。
     */
    List<Map<String, Object>> getDictDataList(OnlineDict dict, List<OnlineFilterDto> filterList);
}
