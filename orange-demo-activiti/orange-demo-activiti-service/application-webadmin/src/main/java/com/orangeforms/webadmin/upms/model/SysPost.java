package com.orangeforms.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.webadmin.upms.vo.SysPostVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;

/**
 * 岗位实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_post")
public class SysPost {

    /**
     * 岗位Id。
     */
    @TableId(value = "post_id")
    private Long postId;

    /**
     * 岗位名称。
     */
    @TableField(value = "post_name")
    private String postName;

    /**
     * 岗位层级，数值越小级别越高。
     */
    private Integer level;

    /**
     * 是否领导岗位。
     */
    @TableField(value = "leader_post")
    private Boolean leaderPost;

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新者Id。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * postId 的多对多关联表数据对象。
     */
    @TableField(exist = false)
    private SysDeptPost sysDeptPost;

    @Mapper
    public interface SysPostModelMapper extends BaseModelMapper<SysPostVo, SysPost> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param sysPostVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysDeptPost", expression = "java(mapToBean(sysPostVo.getSysDeptPost(), com.orangeforms.webadmin.upms.model.SysDeptPost.class))")
        @Override
        SysPost toModel(SysPostVo sysPostVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param sysPost 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysDeptPost", expression = "java(beanToMap(sysPost.getSysDeptPost(), false))")
        @Override
        SysPostVo fromModel(SysPost sysPost);
    }
    public static final SysPostModelMapper INSTANCE = Mappers.getMapper(SysPostModelMapper.class);
}
