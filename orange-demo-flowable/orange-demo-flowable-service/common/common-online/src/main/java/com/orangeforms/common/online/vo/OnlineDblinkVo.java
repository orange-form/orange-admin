package com.orangeforms.common.online.vo;

import lombok.Data;

import java.util.Date;

/**
 * 在线表单数据表所在数据库链接VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineDblinkVo {

    /**
     * 主键Id。
     */
    private Long dblinkId;

    /**
     * 链接中文名称。
     */
    private String dblinkName;

    /**
     * 链接英文名称。
     */
    private String variableName;

    /**
     * 链接描述。
     */
    private String dblinkDesc;

    /**
     * 数据源配置常量。
     */
    private Integer dblinkConfigConstant;

    /**
     * 创建时间。
     */
    private Date createTime;
}
