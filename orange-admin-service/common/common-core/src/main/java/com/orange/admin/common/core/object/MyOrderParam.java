package com.orange.admin.common.core.object;

import cn.hutool.core.util.ReflectUtil;
import com.orange.admin.common.core.util.MyModelUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Controller参数中的排序请求对象。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class MyOrderParam extends ArrayList<MyOrderParam.OrderInfo> {

    /**
     * 基于排序对象中的JSON数据，构建SQL中order by从句可以直接使用的排序字符串。
     *
     * @param orderParam 排序参数对象。
     * @param modelClazz 查询主表对应的主对象的Class。
     * @return SQL中order by从句可以直接使用的排序字符串。
     */
    public static String buildOrderBy(MyOrderParam orderParam, Class<?> modelClazz) {
        if (orderParam == null) {
            return null;
        }
        String exceptionMessage;
        if (modelClazz == null) {
            throw new IllegalArgumentException("modelClazz Argument can't be NULL");
        }
        int i = 0;
        StringBuilder orderBy = new StringBuilder(128);
        for (OrderInfo orderInfo : orderParam) {
            String modelName, fieldName = orderInfo.fieldName, tableName, columnName;
            int pos = fieldName.indexOf("DictMap.");
            if (pos != -1) {
                fieldName = fieldName.substring(0, pos);
            }
            String[] stringArray = StringUtils.split(fieldName, '.');
            if (stringArray.length == 1) {
                modelName = modelClazz.getSimpleName();
                tableName = MyModelUtil.mapToTableName(modelClazz);
                columnName = MyModelUtil.mapToColumnName(fieldName, modelClazz);
            } else {
                Field field = ReflectUtil.getField(modelClazz, stringArray[0]);
                if (field == null) {
                    exceptionMessage = String.format(
                            "Relation Field [%s] doesn't exist in Class [%s]!", stringArray[0], modelClazz.getSimpleName());
                    log.error(exceptionMessage);
                    throw new IllegalArgumentException(exceptionMessage);
                }
                Class<?> fieldClazz = field.getType();
                modelName = fieldClazz.getSimpleName();
                fieldName = stringArray[1];
                tableName = MyModelUtil.mapToTableName(fieldClazz);
                columnName = MyModelUtil.mapToColumnName(fieldName, fieldClazz);
            }
            if (StringUtils.isBlank(tableName)) {
                exceptionMessage = String.format("ModelName [%s] can't be mapped to Table!", modelName);
                log.error(exceptionMessage);
                throw new IllegalArgumentException(exceptionMessage);
            }
            if (StringUtils.isBlank(columnName)) {
                exceptionMessage = String.format(
                        "FieldName [%s] in Class [%s] can't be mapped to Column!", fieldName, modelName);
                log.error(exceptionMessage);
                throw new IllegalArgumentException(exceptionMessage);
            }
            if (StringUtils.isNotBlank(orderInfo.dateAggregateBy)) {
                orderBy.append("DATE_FORMAT(").append(tableName).append(".").append(columnName);
                if ("day".equals(orderInfo.dateAggregateBy)) {
                    orderBy.append(", '%Y-%m-%d')");
                } else if ("month".equals(orderInfo.dateAggregateBy)) {
                    orderBy.append(", '%Y-%m-01')");
                } else if ("year".equals(orderInfo.dateAggregateBy)) {
                    orderBy.append(", '%Y-01-01')");
                } else {
                    throw new IllegalArgumentException("Illegal DATE_FORMAT for GROUP ID list.");
                }
            } else {
                orderBy.append(tableName).append(".").append(columnName);
            }
            if (orderInfo.asc != null && !orderInfo.asc) {
                orderBy.append(" DESC");
            }
            if (++i < orderParam.size()) {
                orderBy.append(", ");
            }
        }
        return orderBy.toString();
    }

    /**
     * 在排序列表中，可能存在基于指定表字段的排序，该函数将获取指定表的所有排序字段。
     * 返回有的字符串，可直接用于SQL中的ORDER BY从句。
     *
     * @param orderParam 排序参数对象。
     * @param modelClazz 查询主表对应的主对象的Class。
     * @param relationModelName 与关联表对应的Model的名称，如my_course_paper表应对的Java对象CoursePaper。
     *                          如果该值为null或空字符串，则获取所有主表的排序字段。
     * @return 返回的是表字段，而非Java对象的属性，多个字段之间逗号分隔。
     */
    public static String getOrderClauseByModelName(
            MyOrderParam orderParam, Class<?> modelClazz, String relationModelName) {
        if (orderParam == null) {
            return null;
        }
        if (modelClazz == null) {
            throw new IllegalArgumentException("modelClazz Argument can't be NULL");
        }
        String exceptionMessage;
        List<String> fieldNameList = new LinkedList<>();
        String prefix = null;
        if (StringUtils.isNotBlank(relationModelName)) {
            prefix = relationModelName + ".";
        }
        for (OrderInfo orderInfo : orderParam) {
            boolean found = false;
            String modelName = null, fieldName = orderInfo.fieldName, tableName = null, columnName = null;
            int pos = fieldName.indexOf("DictMap.");
            if (pos != -1) {
                fieldName = fieldName.substring(0, pos);
            }
            if (prefix != null) {
                if (fieldName.startsWith(prefix)) {
                    Field field = ReflectUtil.getField(modelClazz, relationModelName);
                    if (field == null) {
                        exceptionMessage = String.format(
                                "Relation Field [%s] doesn't exist in Class [%s]!", relationModelName, modelClazz.getSimpleName());
                        log.error(exceptionMessage);
                        throw new IllegalArgumentException(exceptionMessage);
                    }
                    Class<?> fieldClazz = field.getType();
                    modelName = fieldClazz.getSimpleName();
                    fieldName = StringUtils.removeStart(fieldName, prefix);
                    tableName = MyModelUtil.mapToTableName(fieldClazz);
                    columnName = MyModelUtil.mapToColumnName(fieldName, fieldClazz);
                    found = true;
                }
            } else {
                if (!fieldName.contains(".")) {
                    modelName = modelClazz.getSimpleName();
                    tableName = MyModelUtil.mapToTableName(modelClazz);
                    columnName = MyModelUtil.mapToColumnName(fieldName, modelClazz);
                    found = true;
                }
            }
            if (found) {
                if (StringUtils.isBlank(tableName)) {
                    exceptionMessage = String.format("ModelName [%s] can't be mapped to Table!", modelName);
                    log.error(exceptionMessage);
                    throw new IllegalArgumentException(exceptionMessage);
                }
                if (StringUtils.isBlank(columnName)) {
                    exceptionMessage = String.format(
                            "FieldName [%s] in Class [%s] can't be mapped to Column!", fieldName, modelName);
                    log.error(exceptionMessage);
                    throw new IllegalArgumentException(exceptionMessage);
                }
                StringBuilder orderBy = new StringBuilder(128);
                orderBy.append(tableName).append(".").append(columnName);
                if (orderInfo.asc != null && !orderInfo.asc) {
                    orderBy.append(" DESC");
                }
                fieldNameList.add(orderBy.toString());
            }
        }
        return StringUtils.join(fieldNameList, ", ");
    }

    /**
     * 在排序列表中，可能存在基于指定表字段的排序，该函数将删除指定表的所有排序字段。
     *
     * @param orderParam 排序参数对象。
     * @param modelClazz 查询主表对应的主对象的Class。
     * @param relationModelName 与关联表对应的Model的名称，如my_course_paper表应对的Java对象CoursePaper。
     *                          如果该值为null或空字符串，则获取所有主表的排序字段。
     */
    public static void removeOrderClauseByModelName(
            MyOrderParam orderParam, Class<?> modelClazz, String relationModelName) {
        if (orderParam == null) {
            return;
        }
        if (modelClazz == null) {
            throw new IllegalArgumentException("modelClazz Argument can't be NULL");
        }
        List<Integer> fieldIndexList = new LinkedList<>();
        String prefix = null;
        if (StringUtils.isNotBlank(relationModelName)) {
            prefix = relationModelName + ".";
        }
        int i = 0;
        for (OrderInfo orderInfo : orderParam) {
            String fieldName = orderInfo.fieldName;
            int pos = fieldName.indexOf("DictMap.");
            if (pos != -1) {
                fieldName = fieldName.substring(0, pos);
            }
            if (prefix != null) {
                if (fieldName.startsWith(prefix)) {
                    fieldIndexList.add(i);
                }
            } else {
                if (!fieldName.contains(".")) {
                    fieldIndexList.add(i);
                }
            }
            ++i;
        }
        for (int index : fieldIndexList) {
            orderParam.remove(index);
        }
    }

    @Data
    public static class OrderInfo {
        /**
         * Java对象的字段名。目前主要包含三种格式：
         * 1. 简单的属性名称，如userId，将会直接映射到与其关联的数据库字段。表名为当前ModelClazz所对应的表名。
         *    映射结果或为 my_main_table.user_id
         * 2. 字典属性名称，如userIdDictMap.id，由于仅仅支持字典中Id数据的排序，所以直接截取DictMap之前的字符串userId作为排序属性。
         *    表名为当前ModelClazz所对应的表名。映射结果或为 my_main_table.user_id
         * 3. 一对一关联表属性，如user.userId，这里将先获取user属性的对象类型并映射到对应的表名，后面的userId为
         *    user所在实体的属性。映射结果或为：my_sys_user.user_id
         */
        private String fieldName;
        /**
         * 排序方向。true为升序，否则降序。
         */
        private Boolean asc = true;
        /**
         * 如果该值不为NULL，则会对日期型排序字段进行DATE_FORMAT函数的计算，并根据具体的值，将日期数据截取到指定的位。
         * day:   表示按照天聚合，将会截取到天。DATE_FORMAT(columnName, '%Y-%m-%d')
         * month: 表示按照月聚合，将会截取到月。DATE_FORMAT(columnName, '%Y-%m-01')
         * year:  表示按照年聚合，将会截取到年。DATE_FORMAT(columnName, '%Y-01-01')
         */
        private String dateAggregateBy;
    }
}
