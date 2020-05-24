package com.orange.admin.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.admin.common.core.annotation.DeletedFlagColumn;
import com.orange.admin.common.core.annotation.RelationManyToMany;
import com.orange.admin.common.core.validator.ConstDictRef;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.upms.model.constant.SysMenuType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * 菜单实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_menu")
public class SysMenu {

    /**
     * 主键Id。
     */
    @NotNull(message = "菜单Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "menu_id")
    private Long menuId;

    /**
     * 父菜单Id，目录菜单的父菜单为null。
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    @NotBlank(message = "菜单显示名称不能为空！")
    @Column(name = "menu_name")
    private String menuName;

    /**
     * 菜单类型(0: 目录 1: 菜单 2: 按钮 3: UI片段)。
     */
    @NotNull(message = "菜单类型不能为空！")
    @ConstDictRef(constDictClass = SysMenuType.class, message = "数据验证失败，菜单类型为无效值！")
    @Column(name = "menu_type")
    private Integer menuType;

    /**
     * 前端表单路由名称，仅用于menu_type为1的菜单类型。
     */
    @Column(name = "form_router_name")
    private String formRouterName;

    /**
     * 菜单显示顺序 (值越小，排序越靠前)。
     */
    @NotNull(message = "菜单显示顺序不能为空！")
    @Column(name = "show_order")
    private Integer showOrder;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @RelationManyToMany(
            relationMapperName = "sysMenuPermCodeMapper",
            relationMasterIdField = "menuId",
            relationModelClass = SysMenuPermCode.class)
    @Transient
    private List<SysMenuPermCode> sysMenuPermCodeList;
}
