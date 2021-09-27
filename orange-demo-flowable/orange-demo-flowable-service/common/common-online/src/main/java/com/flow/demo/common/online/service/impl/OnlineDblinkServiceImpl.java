package com.flow.demo.common.online.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.core.base.service.BaseService;
import com.flow.demo.common.core.config.DataSourceContextHolder;
import com.flow.demo.common.core.object.MyRelationParam;
import com.flow.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.flow.demo.common.online.config.OnlineProperties;
import com.flow.demo.common.online.dao.OnlineDblinkMapper;
import com.flow.demo.common.online.model.OnlineDblink;
import com.flow.demo.common.online.object.SqlTable;
import com.flow.demo.common.online.object.SqlTableColumn;
import com.flow.demo.common.online.service.OnlineDblinkService;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库链接数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("onlineDblinkService")
public class OnlineDblinkServiceImpl extends BaseService<OnlineDblink, Long> implements OnlineDblinkService {

    @Autowired
    private OnlineDblinkMapper onlineDblinkMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private OnlineProperties onlineProperties;

    private Map<Serializable, OnlineDblink> dblinkMap;

    @PostConstruct
    public void loadAllDblink() {
        List<OnlineDblink> dblinkList = super.getAllList();
        this.dblinkMap = dblinkList.stream().collect(Collectors.toMap(OnlineDblink::getDblinkId, c -> c));
    }

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<OnlineDblink> mapper() {
        return onlineDblinkMapper;
    }

    /**
     * 根据主键Id，从本地缓存中读取数据库链接信息。
     * 这里之所以不考虑缓存补偿，是因为如果出现新的用于在线表单的数据库链接，我们也需要修改当前服务的多数据源配置才能正常工作，
     * 否则新OnlineDblink的ConstantType，没法保证正常的数据源切换。
     *
     * @param dblinkId 数据库链接Id。
     * @return 查询到的OnlineDblink对象。
     */
    @Override
    public OnlineDblink getById(Serializable dblinkId) {
        return dblinkMap.get(dblinkId);
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineDblinkListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineDblink> getOnlineDblinkList(OnlineDblink filter, String orderBy) {
        return onlineDblinkMapper.getOnlineDblinkList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineDblinkList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineDblink> getOnlineDblinkListWithRelation(OnlineDblink filter, String orderBy) {
        List<OnlineDblink> resultList = onlineDblinkMapper.getOnlineDblinkList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 获取指定DBLink下面的全部数据表。
     *
     * @param dblink 数据库链接对象。
     * @return 全部数据表列表。
     */
    @Override
    public List<SqlTable> getDblinkTableList(OnlineDblink dblink) {
        Integer originalType = DataSourceContextHolder.setDataSourceType(dblink.getDblinkConfigConstant());
        try {
            List<Map<String, Object>> resultList =
                    onlineDblinkMapper.getTableListWithPrefix(onlineProperties.getTablePrefix());
            List<SqlTable> tableList = new LinkedList<>();
            resultList.forEach(r -> {
                SqlTable sqlTable = BeanUtil.mapToBean(r, SqlTable.class, false, null);
                sqlTable.setDblinkId(dblink.getDblinkId());
                tableList.add(sqlTable);
            });
            return tableList;
        } finally {
            DataSourceContextHolder.unset(originalType);
        }
    }

    /**
     * X
     * 获取指定DBLink下，指定表名的数据表对象，及其关联字段列表。
     *
     * @param dblink    数据库链接对象。
     * @param tableName 数据库中的数据表名。
     * @return 数据表对象。
     */
    @Override
    public SqlTable getDblinkTable(OnlineDblink dblink, String tableName) {
        Integer originalType = DataSourceContextHolder.setDataSourceType(dblink.getDblinkConfigConstant());
        try {
            Map<String, Object> result = onlineDblinkMapper.getTableByName(tableName);
            if (result == null) {
                return null;
            }
            SqlTable sqlTable = BeanUtil.mapToBean(result, SqlTable.class, false, null);
            sqlTable.setDblinkId(dblink.getDblinkId());
            sqlTable.setColumnList(getDblinkTableColumnList(dblink, tableName));
            return sqlTable;
        } finally {
            DataSourceContextHolder.unset(originalType);
        }
    }

    /**
     * 获取指定DBLink下，指定表名的字段列表。
     *
     * @param dblink    数据库链接对象。
     * @param tableName 表名。
     * @return 表的字段列表。
     */
    @Override
    public List<SqlTableColumn> getDblinkTableColumnList(OnlineDblink dblink, String tableName) {
        Integer originalType = DataSourceContextHolder.setDataSourceType(dblink.getDblinkConfigConstant());
        try {
            List<Map<String, Object>> resultList = onlineDblinkMapper.getTableColumnList(tableName);
            List<SqlTableColumn> columnList = new LinkedList<>();
            resultList.forEach(r -> {
                SqlTableColumn sqlTableColumn =
                        BeanUtil.mapToBean(r, SqlTableColumn.class, false, null);
                sqlTableColumn.setAutoIncrement("auto_increment".equals(sqlTableColumn.getExtra()));
                columnList.add(sqlTableColumn);
            });
            return columnList;
        } finally {
            DataSourceContextHolder.unset(originalType);
        }
    }

    /**
     * 获取指定DBLink下，指定表的字段对象。
     *
     * @param dblink     数据库链接对象。
     * @param tableName  数据库中的数据表名。
     * @param columnName 数据库中的数据表的字段名。
     * @return 表的字段对象。
     */
    @Override
    public SqlTableColumn getDblinkTableColumn(OnlineDblink dblink, String tableName, String columnName) {
        Integer originalType = DataSourceContextHolder.setDataSourceType(dblink.getDblinkConfigConstant());
        try {
            Map<String, Object> result = onlineDblinkMapper.getTableColumnByName(tableName, columnName);
            if (result == null) {
                return null;
            }
            SqlTableColumn sqlTableColumn =
                    BeanUtil.mapToBean(result, SqlTableColumn.class, false, null);
            sqlTableColumn.setAutoIncrement("auto_increment".equals(sqlTableColumn.getExtra()));
            return sqlTableColumn;
        } finally {
            DataSourceContextHolder.unset(originalType);
        }
    }
}
