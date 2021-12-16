package com.orangeforms.upmsservice.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.annotation.RelationManyToMany;
import com.orangeforms.common.core.base.model.BaseModel;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.upmsapi.vo.SysMenuVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;

/**
 * 菜单实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "zz_sys_menu")
public class SysMenu extends BaseModel {

    /**
     * 主键Id。
     */
    @TableId(value = "menu_id")
    private Long menuId;

    /**
     * 父菜单Id，目录菜单的父菜单为null。
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    @TableField(value = "menu_name")
    private String menuName;

    /**
     * 菜单类型(0: 目录 1: 菜单 2: 按钮 3: UI片段)。
     */
    @TableField(value = "menu_type")
    private Integer menuType;

    /**
     * 前端表单路由名称，仅用于menu_type为1的菜单类型。
     */
    @TableField(value = "form_router_name")
    private String formRouterName;

    /**
     * 在线表单主键Id，仅用于在线表单绑定的菜单。
     */
    @TableField(value = "online_form_id")
    private Long onlineFormId;

    /**
     * 在线表单菜单的权限控制类型，具体值可参考SysOnlineMenuPermType常量对象。
     */
    @TableField(value = "online_menu_perm_type")
    private Integer onlineMenuPermType;

    /**
     * 菜单显示顺序 (值越小，排序越靠前)。
     */
    @TableField(value = "show_order")
    private Integer showOrder;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    @RelationManyToMany(
            relationMapperName = "sysMenuPermCodeMapper",
            relationMasterIdField = "menuId",
            relationModelClass = SysMenuPermCode.class)
    @TableField(exist = false)
    private List<SysMenuPermCode> sysMenuPermCodeList;

    @Mapper
    public interface SysMenuModelMapper extends BaseModelMapper<SysMenuVo, SysMenu> {
        /**
         * 转换VO对象到实体对象。
         *
         * @param sysMenuVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysMenuPermCodeList", expression = "java(mapToBean(sysMenuVo.getSysMenuPermCodeList(), com.orangeforms.upmsservice.model.SysMenuPermCode.class))")
        @Override
        SysMenu toModel(SysMenuVo sysMenuVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param sysMenu 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysMenuPermCodeList", expression = "java(beanToMap(sysMenu.getSysMenuPermCodeList(), false))")
        @Override
        SysMenuVo fromModel(SysMenu sysMenu);
    }
    public static final SysMenuModelMapper INSTANCE = Mappers.getMapper(SysMenu.SysMenuModelMapper.class);
}
