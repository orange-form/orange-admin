package com.orangeforms.common.online.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单数据表字段规则和字段多对多关联VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineColumnVo {

    /**
     * 主键Id。
     */
    private Long columnId;

    /**
     * 字段名。
     */
    private String columnName;

    /**
     * 数据表Id。
     */
    private Long tableId;

    /**
     * 数据表中的字段类型。
     */
    private String columnType;

    /**
     * 数据表中的完整字段类型(包括了精度和刻度)。
     */
    private String fullColumnType;

    /**
     * 是否为主键。
     */
    private Boolean primaryKey;

    /**
     * 是否是自增主键(0: 不是 1: 是)。
     */
    private Boolean autoIncrement;

    /**
     * 是否可以为空 (0: 不可以为空 1: 可以为空)。
     */
    private Boolean nullable;

    /**
     * 缺省值。
     */
    private String columnDefault;

    /**
     * 字段在数据表中的显示位置。
     */
    private Integer columnShowOrder;

    /**
     * 数据表中的字段注释。
     */
    private String columnComment;

    /**
     * 对象映射字段名称。
     */
    private String objectFieldName;

    /**
     * 对象映射字段类型。
     */
    private String objectFieldType;

    /**
     * 过滤类型。
     */
    private Integer filterType;

    /**
     * 是否是主键的父Id。
     */
    private Boolean parentKey;

    /**
     * 是否部门过滤字段。
     */
    private Boolean deptFilter;

    /**
     * 是否用户过滤字段。
     */
    private Boolean userFilter;

    /**
     * 字段类别。
     */
    private Integer fieldKind;

    /**
     * 包含的文件文件数量，0表示无限制。
     */
    private Integer maxFileCount;

    /**
     * 字典Id。
     */
    private Long dictId;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * fieldKind 常量字典关联数据。
     */
    private Map<String, Object> fieldKindDictMap;

    /**
     * dictId 的一对一关联。
     */
    private Map<String, Object> dictInfo;
}
