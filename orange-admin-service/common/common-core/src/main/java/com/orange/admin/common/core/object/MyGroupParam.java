package com.orange.admin.common.core.object;

import cn.hutool.core.util.ReflectUtil;
import com.orange.admin.common.core.util.MyModelUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 查询分组参数请求对象。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class MyGroupParam extends ArrayList<MyGroupParam.GroupInfo> {

    /**
     * SQL语句的SELECT LIST中，分组字段的返回字段名称列表。
     */
    private List<String> selectGroupFieldList;

    /**
     * 分组参数解析后构建的SQL语句中所需的分组数据，如GROUP BY的字段列表和SELECT LIST中的分组字段显示列表。
     */
    private MyGroupCriteria groupCriteria;
    /**
     * 基于分组参数对象中的数据，构建SQL中select list和group by从句可以直接使用的分组对象。
     *
     * @param groupParam 分组参数对象。
     * @param modelClazz 查询表对应的主对象的Class。
     * @return SQL中所需的GROUP对象。详见MyGroupCriteria类定义。
     */
    public static MyGroupParam buildGroupBy(MyGroupParam groupParam, Class<?> modelClazz) {
        if (groupParam == null) {
            return null;
        }
        if (modelClazz == null) {
            throw new IllegalArgumentException("modelClazz Argument can't be NULL");
        }
        groupParam.selectGroupFieldList = new LinkedList<>();
        StringBuilder groupByBuilder = new StringBuilder(128);
        StringBuilder groupSelectBuilder = new StringBuilder(128);
        int i = 0;
        for (GroupInfo groupInfo : groupParam) {
            String modelName, fieldName, tableName, columnName;
            String[] stringArray = StringUtils.split(groupInfo.fieldName,'.');
            if (stringArray.length == 1) {
                modelName = modelClazz.getSimpleName();
                fieldName = groupInfo.fieldName;
                tableName = MyModelUtil.mapToTableName(modelClazz);
                columnName = MyModelUtil.mapToColumnName(fieldName, modelClazz);
            } else {
                Field field = ReflectUtil.getField(modelClazz, stringArray[0]);
                if (field == null) {
                    log.error("Relation Field [{}] doesn't exist in Class [{}]!",
                            stringArray[0], modelClazz.getSimpleName());
                    return null;
                }
                Class<?> fieldClazz = field.getType();
                modelName = fieldClazz.getSimpleName();
                fieldName = stringArray[1];
                tableName = MyModelUtil.mapToTableName(fieldClazz);
                columnName = MyModelUtil.mapToColumnName(fieldName, fieldClazz);
            }
            if (StringUtils.isBlank(tableName)) {
                log.error("ModelName [{}] can't be mapped to Table!", modelName);
                return null;
            }
            if (StringUtils.isBlank(columnName)) {
                log.error("FieldName [{}] in Class [{}`] can't be mapped to Column!", fieldName, modelName);
                return null;
            }
            if (StringUtils.isNotBlank(groupInfo.dateAggregateBy)) {
                groupByBuilder.append("DATE_FORMAT(").append(tableName).append(".").append(columnName);
                groupSelectBuilder.append("DATE_FORMAT(").append(tableName).append(".").append(columnName);
                if ("day".equals(groupInfo.dateAggregateBy)) {
                    groupByBuilder.append(", '%Y-%m-%d')");
                    groupSelectBuilder.append(", '%Y-%m-%d')");
                } else if ("month".equals(groupInfo.dateAggregateBy)) {
                    groupByBuilder.append(", '%Y-%m-01')");
                    groupSelectBuilder.append(", '%Y-%m-01')");
                } else if ("year".equals(groupInfo.dateAggregateBy)) {
                    groupByBuilder.append(", '%Y-01-01')");
                    groupSelectBuilder.append(", '%Y-01-01')");
                } else {
                    throw new IllegalArgumentException("Illegal DATE_FORMAT for GROUP ID list.");
                }
                if (StringUtils.isNotBlank(groupInfo.aliasName)) {
                    groupSelectBuilder.append(" ").append(groupInfo.aliasName);
                } else {
                    groupSelectBuilder.append(" ").append(columnName);
                }
            } else {
                groupByBuilder.append(tableName).append(".").append(columnName);
                groupSelectBuilder.append(tableName).append(".").append(columnName);
                if (StringUtils.isNotBlank(groupInfo.aliasName)) {
                    groupSelectBuilder.append(" ").append(groupInfo.aliasName);
                }
            }
            String aliasName = StringUtils.isBlank(groupInfo.aliasName) ? fieldName : groupInfo.aliasName;
            // selectGroupFieldList中的元素，目前只是被export操作使用。会根据集合中的元素名称匹配导出表头。
            groupParam.selectGroupFieldList.add(aliasName);
            if (++i < groupParam.size()) {
                groupByBuilder.append(", ");
                groupSelectBuilder.append(", ");
            }
        }
        groupParam.groupCriteria = new MyGroupCriteria(groupByBuilder.toString(), groupSelectBuilder.toString());
        return groupParam;
    }

    @Data
    public static class GroupInfo {
        /**
         * Java对象的字段名。目前主要包含三种格式：
         * 1. 简单的属性名称，如userId，将会直接映射到与其关联的数据库字段。表名为当前ModelClazz所对应的表名。
         *    映射结果或为 my_main_table.user_id
         * 2. 一对一关联表属性，如user.userId，这里将先获取user属性的对象类型并映射到对应的表名，后面的userId为
         *    user所在实体的属性。映射结果或为：my_sys_user.user_id
         */
        private String fieldName;
        /**
         * SQL语句的Select List中，分组字段的别名。如果别名为NULL，直接取fieldName。
         */
        private String aliasName;
        /**
         * 如果该值不为NULL，则会对分组字段进行DATE_FORMAT函数的计算，并根据具体的值，将日期数据截取到指定的位。
         * day:   表示按照天聚合，将会截取到天。DATE_FORMAT(columnName, '%Y-%m-%d')
         * month: 表示按照月聚合，将会截取到月。DATE_FORMAT(columnName, '%Y-%m-01')
         * year:  表示按照年聚合，将会截取到年。DATE_FORMAT(columnName, '%Y-01-01')
         */
        private String dateAggregateBy;
    }
}
