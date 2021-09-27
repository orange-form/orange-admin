package com.flow.demo.common.online.object;

import lombok.Data;

/**
 * 数据库中的表字段对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SqlTableColumn {

    /**
     * 表字段名。
     */
    private String columnName;

    /**
     * 表字段类型。
     */
    private String columnType;

    /**
     * 表字段全类型。
     */
    private String fullColumnType;

    /**
     * 字段注释。
     */
    private String columnComment;

    /**
     * 是否为主键。
     */
    private Boolean primaryKey;

    /**
     * 是否自动增长。
     */
    private Boolean autoIncrement;

    /**
     * 是否可以为空值。
     */
    private Boolean nullable;

    /**
     * 字段顺序。
     */
    private Integer columnShowOrder;

    /**
     * 附件信息。
     */
    private String extra;

    /**
     * 字符型字段精度。
     */
    private Long stringPrecision;

    /**
     * 数值型字段精度。
     */
    private Integer numericPrecision;

    /**
     * 缺省值。
     */
    private Object columnDefault;
}
