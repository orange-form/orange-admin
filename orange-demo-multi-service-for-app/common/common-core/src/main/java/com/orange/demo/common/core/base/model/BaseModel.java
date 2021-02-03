package com.orange.demo.common.core.base.model;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * 实体对象的公共基类，所有子类均必须包含基类定义的数据表字段和实体对象字段。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class BaseModel {

    /**
     * 创建者Id。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新者Id。
     */
    @Column(name = "update_user_id")
    private Long updateUserId;

    /**
     * 更新时间。
     */
    @Column(name = "update_time")
    private Date updateTime;
}
