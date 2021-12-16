package com.orangeforms.common.online.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单关联的字典VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineDictVo {

    /**
     * 主键Id。
     */
    private Long dictId;

    /**
     * 字典名称。
     */
    private String dictName;

    /**
     * 字典类型。
     */
    private Integer dictType;

    /**
     * 数据库链接Id。
     */
    private Long dblinkId;

    /**
     * 字典表名称。
     */
    private String tableName;

    /**
     * 字典表键字段名称。
     */
    private String keyColumnName;

    /**
     * 字典表父键字段名称。
     */
    private String parentKeyColumnName;

    /**
     * 字典值字段名称。
     */
    private String valueColumnName;

    /**
     * 逻辑删除字段。
     */
    private String deletedColumnName;

    /**
     * 用户过滤滤字段名称。
     */
    private String userFilterColumnName;

    /**
     * 部门过滤字段名称。
     */
    private String deptFilterColumnName;

    /**
     * 租户过滤字段名称。
     */
    private String tenantFilterColumnName;

    /**
     * 是否树形标记。
     */
    private Boolean treeFlag;

    /**
     * 获取字典数据的url。
     */
    private String dictListUrl;

    /**
     * 根据主键id批量获取字典数据的url。
     */
    private String dictIdsUrl;

    /**
     * 字典的JSON数据。
     */
    private String dictDataJson;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * dictType 常量字典关联数据。
     */
    private Map<String, Object> dictTypeDictMap;

    /**
     * 数据库链接Id字典关联数据。
     */
    private Map<String, Object> dblinkIdDictMap;
}
