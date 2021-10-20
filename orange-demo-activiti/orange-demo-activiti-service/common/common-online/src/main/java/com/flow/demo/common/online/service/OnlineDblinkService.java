package com.flow.demo.common.online.service;

import com.flow.demo.common.core.base.service.IBaseService;
import com.flow.demo.common.online.model.OnlineDblink;
import com.flow.demo.common.online.object.SqlTable;
import com.flow.demo.common.online.object.SqlTableColumn;

import java.util.List;

/**
 * 数据库链接数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineDblinkService extends IBaseService<OnlineDblink, Long> {

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineDblinkListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineDblink> getOnlineDblinkList(OnlineDblink filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineDblinkList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<OnlineDblink> getOnlineDblinkListWithRelation(OnlineDblink filter, String orderBy);

    /**
     * 获取指定DBLink下面的全部数据表。
     *
     * @param dblink 数据库链接对象。
     * @return 全部数据表列表。
     */
    List<SqlTable> getDblinkTableList(OnlineDblink dblink);

    /**
     * 获取指定DBLink下，指定表名的数据表对象，及其关联字段列表。
     *
     * @param dblink    数据库链接对象。
     * @param tableName 数据库中的数据表名。
     * @return 数据表对象。
     */
    SqlTable getDblinkTable(OnlineDblink dblink, String tableName);

    /**
     * 获取指定DBLink下，指定表名的字段列表。
     *
     * @param dblink    数据库链接对象。
     * @param tableName 数据库中的数据表名。
     * @return 表的字段列表。
     */
    List<SqlTableColumn> getDblinkTableColumnList(OnlineDblink dblink, String tableName);

    /**
     * 获取指定DBLink下，指定表的字段对象。
     *
     * @param dblink     数据库链接对象。
     * @param tableName  数据库中的数据表名。
     * @param columnName 数据库中的数据表的字段名。
     * @return 表的字段对象。
     */
    SqlTableColumn getDblinkTableColumn(OnlineDblink dblink, String tableName, String columnName);
}
