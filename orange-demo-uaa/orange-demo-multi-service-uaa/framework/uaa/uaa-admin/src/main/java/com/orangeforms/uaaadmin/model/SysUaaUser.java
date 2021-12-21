package com.orangeforms.uaaadmin.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.base.model.BaseModel;
import com.orangeforms.common.core.annotation.RelationConstDict;
import com.orangeforms.common.core.validator.AddGroup;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.uaaadmin.model.constant.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.*;

/**
 * SysUaaUser实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "zz_sys_uaa_user")
public class SysUaaUser extends BaseModel {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 用户登录名称。
     */
    @NotBlank(message = "数据验证失败，登录名称不能为空！")
    private String username;

    /**
     * 密码。
     */
    @JSONField(serialize = false)
    @NotBlank(message = "数据验证失败，密码不能为空！", groups = {AddGroup.class})
    private String password;

    /**
     * 用户显示名称。
     */
    @NotBlank(message = "数据验证失败，用户昵称不能为空！")
    @TableField(value = "show_name")
    private String showName;

    /**
     * 状态(0: 正常 1: 锁定)。
     */
    @NotNull(message = "数据验证失败，用户状态不能为空！")
    @ConstDictRef(constDictClass = UserStatus.class, message = "数据验证失败，用户状态为无效值！")
    private Integer locked;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * [创建时间] 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String createTimeStart;

    /**
     * [创建时间] 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String createTimeEnd;

    /**
     * username / show_name LIKE搜索字符串。
     */
    @TableField(exist = false)
    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = MyCommonUtil.replaceSqlWildcard(searchString);
    }

    @RelationConstDict(
            masterIdField = "locked",
            constantDictClass = UserStatus.class)
    @TableField(exist = false)
    private Map<String, Object> lockedDictMap;
}
