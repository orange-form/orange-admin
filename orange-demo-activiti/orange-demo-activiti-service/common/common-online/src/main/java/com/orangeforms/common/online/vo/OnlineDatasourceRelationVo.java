package com.orangeforms.common.online.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单的数据源关联VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineDatasourceRelationVo {

    /**
     * 主键Id。
     */
    private Long relationId;

    /**
     * 关联名称。
     */
    private String relationName;

    /**
     * 变量名。
     */
    private String variableName;

    /**
     * 主数据源Id。
     */
    private Long datasourceId;

    /**
     * 关联类型。
     */
    private Integer relationType;

    /**
     * 主表关联字段Id。
     */
    private Long masterColumnId;

    /**
     * 从表Id。
     */
    private Long slaveTableId;

    /**
     * 从表关联字段Id。
     */
    private Long slaveColumnId;

    /**
     * 删除主表的时候是否级联删除一对一和一对多的从表数据，多对多只是删除关联，不受到这个标记的影响。。
     */
    private Boolean cascadeDelete;

    /**
     * 是否左连接。
     */
    private Boolean leftJoin;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * masterColumnId 的一对一关联数据对象，数据对应类型为OnlineColumnVo。
     */
    private Map<String, Object> masterColumn;

    /**
     * slaveTableId 的一对一关联数据对象，数据对应类型为OnlineTableVo。
     */
    private Map<String, Object> slaveTable;

    /**
     * slaveColumnId 的一对一关联数据对象，数据对应类型为OnlineColumnVo。
     */
    private Map<String, Object> slaveColumn;

    /**
     * masterColumnId 字典关联数据。
     */
    private Map<String, Object> masterColumnIdDictMap;

    /**
     * slaveTableId 字典关联数据。
     */
    private Map<String, Object> slaveTableIdDictMap;

    /**
     * slaveColumnId 字典关联数据。
     */
    private Map<String, Object> slaveColumnIdDictMap;

    /**
     * relationType 常量字典关联数据。
     */
    private Map<String, Object> relationTypeDictMap;
}
