package com.orangeforms.common.core.object;

import cn.hutool.core.util.ReflectUtil;
import com.orangeforms.common.core.constant.ApplicationConstant;
import com.orangeforms.common.core.exception.InvalidClassFieldException;
import com.orangeforms.common.core.exception.InvalidDataFieldException;
import com.orangeforms.common.core.exception.InvalidDataModelException;
import com.orangeforms.common.core.util.MyModelUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Controller参数中的排序请求对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class MyOrderParam extends ArrayList<MyOrderParam.OrderInfo> {

    private static final String DICT_MAP = "DictMap.";
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
        if (modelClazz == null) {
            throw new IllegalArgumentException(
                    "modelClazz Argument in MyOrderParam.buildOrderBy can't be NULL");
        }
        int i = 0;
        StringBuilder orderBy = new StringBuilder(128);
        for (OrderInfo orderInfo : orderParam) {
            if (StringUtils.isBlank(orderInfo.getFieldName())) {
                continue;
            }
            OrderBaseData orderBaseData = parseOrderBaseData(orderInfo, modelClazz);
            if (StringUtils.isBlank(orderBaseData.tableName)) {
                throw new InvalidDataModelException(orderBaseData.modelName);
            }
            if (StringUtils.isBlank(orderBaseData.columnName)) {
                throw new InvalidDataFieldException(orderBaseData.modelName, orderBaseData.fieldName);
            }
            processOrderInfo(orderInfo, orderBaseData, orderBy);
            if (++i < orderParam.size()) {
                orderBy.append(", ");
            }
        }
        return orderBy.toString();
    }

    private static void processOrderInfo(
            OrderInfo orderInfo, OrderBaseData orderBaseData, StringBuilder orderByBuilder) {
        if (StringUtils.isNotBlank(orderInfo.dateAggregateBy)) {
            orderByBuilder.append("DATE_FORMAT(")
                    .append(orderBaseData.tableName).append(".").append(orderBaseData.columnName);
            if (ApplicationConstant.DAY_AGGREGATION.equals(orderInfo.dateAggregateBy)) {
                orderByBuilder.append(", '%Y-%m-%d')");
            } else if (ApplicationConstant.MONTH_AGGREGATION.equals(orderInfo.dateAggregateBy)) {
                orderByBuilder.append(", '%Y-%m-01')");
            } else if (ApplicationConstant.YEAR_AGGREGATION.equals(orderInfo.dateAggregateBy)) {
                orderByBuilder.append(", '%Y-01-01')");
            } else {
                throw new IllegalArgumentException("Illegal DATE_FORMAT for GROUP ID list.");
            }
        } else {
            orderByBuilder.append(orderBaseData.tableName).append(".").append(orderBaseData.columnName);
        }
        if (orderInfo.asc != null && !orderInfo.asc) {
            orderByBuilder.append(" DESC");
        }
    }

    private static OrderBaseData parseOrderBaseData(OrderInfo orderInfo, Class<?> modelClazz) {
        OrderBaseData orderBaseData = new OrderBaseData();
        orderBaseData.fieldName = StringUtils.substringBefore(orderInfo.fieldName, DICT_MAP);
        String[] stringArray = StringUtils.split(orderBaseData.fieldName, '.');
        if (stringArray.length == 1) {
            orderBaseData.modelName = modelClazz.getSimpleName();
            orderBaseData.tableName = MyModelUtil.mapToTableName(modelClazz);
            orderBaseData.columnName = MyModelUtil.mapToColumnName(orderBaseData.fieldName, modelClazz);
        } else {
            Field field = ReflectUtil.getField(modelClazz, stringArray[0]);
            if (field == null) {
                throw new InvalidClassFieldException(modelClazz.getSimpleName(), stringArray[0]);
            }
            Class<?> fieldClazz = field.getType();
            orderBaseData.modelName = fieldClazz.getSimpleName();
            orderBaseData.fieldName = stringArray[1];
            orderBaseData.tableName = MyModelUtil.mapToTableName(fieldClazz);
            orderBaseData.columnName = MyModelUtil.mapToColumnName(orderBaseData.fieldName, fieldClazz);
        }
        return orderBaseData;
    }

    /**
     * 在排序列表中，可能存在基于指定表字段的排序，该函数将获取指定表的所有排序字段。
     * 返回的字符串，可直接用于SQL中的ORDER BY从句。
     *
     * @param orderParam        排序参数对象。
     * @param modelClazz        查询主表对应的主对象的Class。
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
            throw new IllegalArgumentException(
                    "modelClazz Argument in MyOrderParam.getOrderClauseByModelName can't be NULL");
        }
        List<String> fieldNameList = new LinkedList<>();
        String prefix = null;
        if (StringUtils.isNotBlank(relationModelName)) {
            prefix = relationModelName + ".";
        }
        for (OrderInfo orderInfo : orderParam) {
            OrderBaseData baseData = parseOrderBaseData(orderInfo, modelClazz, prefix, relationModelName);
            if (baseData != null) {
                fieldNameList.add(makeOrderBy(baseData, orderInfo.asc));
            }
        }
        return StringUtils.join(fieldNameList, ", ");
    }

    private static OrderBaseData parseOrderBaseData(
            OrderInfo orderInfo, Class<?> modelClazz, String prefix, String relationModelName) {
        OrderBaseData baseData = null;
        String fieldName = StringUtils.substringBefore(orderInfo.fieldName, DICT_MAP);
        if (prefix != null) {
            if (fieldName.startsWith(prefix)) {
                baseData = new OrderBaseData();
                Field field = ReflectUtil.getField(modelClazz, relationModelName);
                if (field == null) {
                    throw new InvalidClassFieldException(modelClazz.getSimpleName(), relationModelName);
                }
                Class<?> fieldClazz = field.getType();
                baseData.modelName = fieldClazz.getSimpleName();
                baseData.fieldName = StringUtils.removeStart(fieldName, prefix);
                baseData.tableName = MyModelUtil.mapToTableName(fieldClazz);
                baseData.columnName = MyModelUtil.mapToColumnName(fieldName, fieldClazz);
            }
        } else {
            String dotLimitor = ".";
            if (!fieldName.contains(dotLimitor)) {
                baseData = new OrderBaseData();
                baseData.modelName = modelClazz.getSimpleName();
                baseData.tableName = MyModelUtil.mapToTableName(modelClazz);
                baseData.columnName = MyModelUtil.mapToColumnName(fieldName, modelClazz);
            }
        }
        return baseData;
    }

    private static String makeOrderBy(OrderBaseData baseData, Boolean asc) {
        if (StringUtils.isBlank(baseData.tableName)) {
            throw new InvalidDataModelException(baseData.modelName);
        }
        if (StringUtils.isBlank(baseData.columnName)) {
            throw new InvalidDataFieldException(baseData.modelName, baseData.fieldName);
        }
        StringBuilder orderBy = new StringBuilder(128);
        orderBy.append(baseData.tableName).append(".").append(baseData.columnName);
        if (asc != null && !asc) {
            orderBy.append(" DESC");
        }
        return orderBy.toString();
    }

    /**
     * 在排序列表中，可能存在基于指定表字段的排序，该函数将删除指定表的所有排序字段。
     *
     * @param orderParam        排序参数对象。
     * @param modelClazz        查询主表对应的主对象的Class。
     * @param relationModelName 与关联表对应的Model的名称，如my_course_paper表应对的Java对象CoursePaper。
     *                          如果该值为null或空字符串，则获取所有主表的排序字段。
     */
    public static void removeOrderClauseByModelName(
            MyOrderParam orderParam, Class<?> modelClazz, String relationModelName) {
        if (orderParam == null) {
            return;
        }
        if (modelClazz == null) {
            throw new IllegalArgumentException(
                    "modelClazz Argument in MyOrderParam.removeOrderClauseByModelName can't be NULL");
        }
        List<Integer> fieldIndexList = new LinkedList<>();
        String prefix = null;
        if (StringUtils.isNotBlank(relationModelName)) {
            prefix = relationModelName + ".";
        }
        int i = 0;
        for (OrderInfo orderInfo : orderParam) {
            String fieldName = StringUtils.substringBefore(orderInfo.fieldName, DICT_MAP);
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

    /**
     * 排序信息对象。
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class OrderInfo {
        /**
         * Java对象的字段名。如果fieldName为空，则忽略跳过。目前主要包含三种格式：
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

    private static class OrderBaseData {
        private String modelName;
        private String fieldName;
        private String tableName;
        private String columnName;
    }
}
