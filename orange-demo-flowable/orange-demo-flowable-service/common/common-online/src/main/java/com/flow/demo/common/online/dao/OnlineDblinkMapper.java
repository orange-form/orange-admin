package com.flow.demo.common.online.dao;

import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.online.model.OnlineDblink;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据库链接数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineDblinkMapper extends BaseDaoMapper<OnlineDblink> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineDblinkFilter 主表过滤对象。
     * @param orderBy            排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineDblink> getOnlineDblinkList(
            @Param("onlineDblinkFilter") OnlineDblink onlineDblinkFilter, @Param("orderBy") String orderBy);

    /**
     * 获取当前数据库链接下的所有用于动态表单的表。
     *
     * @param prefix 动态表单所使用的表的前缀。如果为空，则返回所有数据表。
     * @return 所有用于动态表单的表。
     */
    @Select("<script>"
        + "SELECT "
        + "    table_name tableName, "
        + "    table_comment tableComment, "
        + "    create_time createTime "
        + "FROM "
        + "    information_schema.tables "
        + "WHERE "
        + "    table_schema = (SELECT database()) "
        + "    <if test=\"prefix != null and prefix != ''\">"
        + "        AND table_name like '${prefix}%'"
        + "    </if>"
        + "</script>")
    List<Map<String, Object>> getTableListWithPrefix(@Param("prefix") String prefix);

    /**
     * 获取当前数据库链接下指定数据库表的数据。
     *
     * @param tableName 数据库表名。
     * @return 表数据。
     */
    @Select("SELECT \n" +
            "  table_name tableName, \n" +
            "  table_comment tableComment, \n" +
            "  create_time createTime \n" +
            "FROM \n" +
            "  information_schema.tables \n" +
            "WHERE table_schema = (SELECT database()) AND table_name = #{tableName}")
    Map<String, Object> getTableByName(@Param("tableName") String tableName);

    /**
     * 获取指定表的字段列表。
     *
     * @param tableName 指定的表名。
     * @return 指定表的字段列表。
     */
    @Select("SELECT \n" +
            "  column_name columnName, \n" +
            "  data_type columnType, \n" +
            "  column_type fullColumnType, \n" +
            "  column_comment columnComment, \n" +
            "  CASE WHEN column_key = 'PRI' THEN 1 ELSE 0 END AS primaryKey, \n" +
            "  is_nullable nullable, \n" +
            "  ordinal_position columnShowOrder, \n" +
            "  extra, \n" +
            "  CHARACTER_MAXIMUM_LENGTH stringPrecision, \n" +
            "  numeric_precision numericPrecision, \n" +
            "  COLUMN_DEFAULT columnDefault \n" +
            "FROM information_schema.columns \n" +
            "WHERE table_name = #{tableName} \n" +
            "  AND table_schema = (SELECT database()) ORDER BY ordinal_position")
    List<Map<String, Object>> getTableColumnList(@Param("tableName") String tableName);

    /**
     * 获取指定表的字段对象。
     *
     * @param tableName  指定的表名。
     * @param columnName 指定的字段名。
     * @return 指定表的字段。
     */
    @Select("SELECT \n" +
            "  column_name columnName, \n" +
            "  data_type columnType, \n" +
            "  column_type fullColumnType, \n" +
            "  column_comment columnComment, \n" +
            "  CASE WHEN column_key = 'PRI' THEN 1 ELSE 0 END AS primaryKey, \n" +
            "  is_nullable nullable, \n" +
            "  ordinal_position columnShowOrder, \n" +
            "  extra, \n" +
            "  CHARACTER_MAXIMUM_LENGTH stringPrecision, \n" +
            "  numeric_precision numericPrecision, \n" +
            "  COLUMN_DEFAULT columnDefault \n" +
            "FROM information_schema.columns \n" +
            "WHERE table_name = #{tableName} \n" +
            "  AND column_name = #{columnName} \n" +
            "  AND table_schema = (SELECT database()) ORDER BY ordinal_position")
    Map<String, Object> getTableColumnByName(
            @Param("tableName") String tableName, @Param("columnName") String columnName);
}
