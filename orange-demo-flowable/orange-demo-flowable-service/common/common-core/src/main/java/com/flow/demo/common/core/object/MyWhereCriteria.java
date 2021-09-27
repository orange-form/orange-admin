package com.flow.demo.common.core.object;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.flow.demo.common.core.exception.InvalidDataFieldException;
import com.flow.demo.common.core.exception.InvalidDataModelException;
import com.flow.demo.common.core.util.MyModelUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Where中的条件语句。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Data
@NoArgsConstructor
public class MyWhereCriteria {

    /**
     * 等于
     */
    public static final int OPERATOR_EQUAL = 0;

    /**
     * 不等于
     */
    public static final int OPERATOR_NOT_EQUAL = 1;

    /**
     * 大于等于
     */
    public static final int OPERATOR_GE = 2;

    /**
     * 大于
     */
    public static final int OPERATOR_GT = 3;

    /**
     * 小于等于
     */
    public static final int OPERATOR_LE = 4;

    /**
     * 小于
     */
    public static final int OPERATOR_LT = 5;

    /**
     * LIKE
     */
    public static final int OPERATOR_LIKE = 6;

    /**
     * NOT NULL
     */
    public static final int OPERATOR_NOT_NULL = 7;

    /**
     * IS NULL
     */
    public static final int OPERATOR_IS_NULL = 8;

    /**
     * IN
     */
    public static final int OPERATOR_IN = 9;

    /**
     * 参与过滤的实体对象的Class。
     */
    @JSONField(serialize = false)
    private Class<?> modelClazz;

    /**
     * 数据库表名。
     */
    private String tableName;

    /**
     * Java属性名称。
     */
    private String fieldName;

    /**
     * 数据表字段名。
     */
    private String columnName;

    /**
     * 数据表字段类型。
     */
    private Integer columnType;

    /**
     * 操作符类型，取值范围见上面的常量值。
     */
    private Integer operatorType;

    /**
     * 条件数据值。
     */
    private Object value;

    public MyWhereCriteria(Class<?> modelClazz, String fieldName, Integer operatorType, Object value) {
        this.modelClazz = modelClazz;
        this.fieldName = fieldName;
        this.operatorType = operatorType;
        this.value = value;
    }

    /**
     * 设置条件值。
     *
     * @param fieldName    条件所属的实体对象的字段名。
     * @param operatorType 条件操作符。具体值可参考当前对象的静态变量。
     * @param value        条件过滤值。
     * @return 验证结果对象，如果有错误将会返回具体的错误信息。
     */
    public CallResult setCriteria(String fieldName, Integer operatorType, Object value) {
        this.operatorType = operatorType;
        this.fieldName = fieldName;
        this.value = value;
        return doVerify();
    }

    /**
     * 设置条件值。
     *
     * @param modelClazz   数据表对应实体对象的Class.
     * @param fieldName    条件所属的实体对象的字段名。
     * @param operatorType 条件操作符。具体值可参考当前对象的静态变量。
     * @param value        条件过滤值。
     * @return 验证结果对象，如果有错误将会返回具体的错误信息。
     */
    public CallResult setCriteria(Class<?> modelClazz, String fieldName, Integer operatorType, Object value) {
        this.modelClazz = modelClazz;
        this.operatorType = operatorType;
        this.fieldName = fieldName;
        this.value = value;
        return doVerify();
    }

    /**
     * 设置条件值，通过该构造方法设置时，通常是直接将表名、字段名、字段类型等赋值，无需在通过modelClazz进行推演。
     *
     * @param tableName    数据表名。
     * @param columnName   数据字段名。
     * @param columnType   数据字段类型。
     * @param operatorType 操作类型。具体值可参考当前对象的静态变量。
     * @param value        条件过滤值。
     */
    public void setCriteria(
            String tableName, String columnName, String columnType, Integer operatorType, Object value) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = MyModelUtil.NUMERIC_FIELD_TYPE;
        if (String.class.getSimpleName().equals(columnType)) {
            this.columnType = MyModelUtil.STRING_FIELD_TYPE;
        } else if (Date.class.getSimpleName().equals(columnType)) {
            this.columnType = MyModelUtil.DATE_FIELD_TYPE;
        }
        this.operatorType = operatorType;
        this.value = value;
    }

    /**
     * 在执行该函数之前，该对象的所有数据均已经赋值完毕。
     * 该函数主要验证操作符字段和条件值字段对应关系的合法性。
     *
     * @return 验证结果对象，如果有错误将会返回具体的错误信息。
     */
    public CallResult doVerify() {
        if (fieldName == null) {
            return CallResult.error("过滤字段名称 [fieldName] 不能为空！");
        }
        if (modelClazz != null && ReflectUtil.getField(modelClazz, fieldName) == null) {
            return CallResult.error(
                    "过滤字段 [" + fieldName + "] 在实体对象 [" + modelClazz.getSimpleName() + "] 中并不存在！");
        }
        if (!checkOperatorType()) {
            return CallResult.error("无效的操作符类型 [" + operatorType + "]!");
        }
        // 其他操作符必须包含value值
        if (operatorType != OPERATOR_IS_NULL && operatorType != OPERATOR_NOT_NULL && value == null) {
            String operatorString = this.getOperatorString();
            return CallResult.error("操作符 [" + operatorString + "] 的条件值不能为空！");
        }
        if (this.operatorType == OPERATOR_IN) {
            if (!(value instanceof Collection)) {
                return CallResult.error("操作符 [IN] 的条件值必须为集合对象！");
            }
            if (CollectionUtils.isEmpty((Collection<?>) value)) {
                return CallResult.error("操作符 [IN] 的条件值不能为空！");
            }
        }
        return CallResult.ok();
    }

    /**
     * 判断操作符类型是否合法。
     *
     * @return 合法返回true，否则false。
     */
    public boolean checkOperatorType() {
        return operatorType != null
                && (operatorType >= OPERATOR_EQUAL && operatorType <= OPERATOR_IN);
    }

    /**
     * 获取操作符的字符串形式。
     *
     * @return 操作符的字符串。
     */
    public String getOperatorString() {
        switch (operatorType) {
            case OPERATOR_EQUAL:
                return " = ";
            case OPERATOR_NOT_EQUAL:
                return " != ";
            case OPERATOR_GE:
                return " >= ";
            case OPERATOR_GT:
                return " > ";
            case OPERATOR_LE:
                return " <= ";
            case OPERATOR_LT:
                return " < ";
            case OPERATOR_LIKE:
                return " LIKE ";
            case OPERATOR_NOT_NULL:
                return " IS NOT NULL ";
            case OPERATOR_IS_NULL:
                return " IS NULL ";
            case OPERATOR_IN:
                return " IN ";
            default:
                return null;
        }
    }

    /**
     * 获取组装后的SQL Where从句，如 table_name.column_name = 'value'。
     * 与查询数据表对应的实体对象Class为当前对象的modelClazz字段。
     *
     * @exception InvalidDataFieldException selectFieldList中存在非法实体字段时，抛出该异常。
     * @return 组装后的SQL条件从句。
     */
    public String makeCriteriaString() {
        return makeCriteriaString(this.modelClazz);
    }

    /**
     * 获取组装后的SQL Where从句，如 table_name.column_name = 'value'。
     *
     * @param modelClazz 与查询数据表对应的实体对象的Class。
     * @exception InvalidDataFieldException selectFieldList中存在非法实体字段时，抛出该异常。
     * @exception InvalidDataModelException 参数modelClazz没有对应的table，抛出该异常。
     * @return 组装后的SQL条件从句。
     */
    public String makeCriteriaString(Class<?> modelClazz) {
        String tableName;
        String columnName;
        Integer columnType;
        if (modelClazz != null) {
            Tuple2<String, Integer> fieldInfo = MyModelUtil.mapToColumnInfo(fieldName, modelClazz);
            if (fieldInfo == null) {
                throw new InvalidDataFieldException(modelClazz.getSimpleName(), fieldName);
            }
            columnName = fieldInfo.getFirst();
            columnType = fieldInfo.getSecond();
            tableName = MyModelUtil.mapToTableName(modelClazz);
            if (tableName == null) {
                throw new InvalidDataModelException(modelClazz.getSimpleName());
            }
        } else {
            tableName = this.tableName;
            columnName = this.columnName;
            columnType = this.columnType;
        }
        return this.buildClauseString(tableName, columnName, columnType);
    }

    /**
     * 获取组装后的SQL Where从句。如 table_name.column_name = 'value'。
     *
     * @param criteriaList 条件列表，所有条件直接目前仅支持 AND 的关系。
     * @exception InvalidDataFieldException selectFieldList中存在非法实体字段时，抛出该异常。
     * @return 组装后的SQL条件从句。
     */
    public static String makeCriteriaString(List<MyWhereCriteria> criteriaList) {
        return makeCriteriaString(criteriaList, null);
    }

    /**
     * 获取组装后的SQL Where从句。如 table_name.column_name = 'value'。
     *
     * @param criteriaList 条件列表，所有条件直接目前仅支持 AND 的关系。
     * @param modelClazz   与数据表对应的实体对象的Class。
     *                     如果不为NULL实体对象Class使用该值，否则使用每个MyWhereCriteria自身的modelClazz。
     * @exception InvalidDataFieldException selectFieldList中存在非法实体字段时，抛出该异常。
     * @return 组装后的SQL条件从句。
     */
    public static String makeCriteriaString(List<MyWhereCriteria> criteriaList, Class<?> modelClazz) {
        if (CollectionUtils.isEmpty(criteriaList)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(256);
        int i = 0;
        for (MyWhereCriteria whereCriteria : criteriaList) {
            Class<?> clazz = modelClazz;
            if (clazz == null) {
                clazz = whereCriteria.modelClazz;
            }
            if (i++ != 0) {
                sb.append(" AND ");
            }
            String criteriaString = whereCriteria.makeCriteriaString(clazz);
            sb.append(criteriaString);
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private String buildClauseString(String tableName, String columnName, Integer columnType) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(tableName).append(".").append(columnName).append(getOperatorString());
        if (operatorType == OPERATOR_IN) {
            Collection<?> filterValues = (Collection<?>) value;
            sb.append("(");
            int i = 0;
            for (Object filterValue : filterValues) {
                if (columnType.equals(MyModelUtil.NUMERIC_FIELD_TYPE)) {
                    sb.append(filterValue);
                } else {
                    sb.append("'").append(filterValue).append("'");
                }
                if (i++ != filterValues.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
        if (value != null) {
            if (columnType.equals(MyModelUtil.NUMERIC_FIELD_TYPE)) {
                sb.append(value);
            } else {
                sb.append("'").append(value).append("'");
            }
        }
        return sb.toString();
    }
}
