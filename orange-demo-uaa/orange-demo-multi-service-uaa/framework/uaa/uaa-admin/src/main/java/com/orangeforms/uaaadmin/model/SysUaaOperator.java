package com.orangeforms.uaaadmin.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.base.model.BaseModel;
import com.orangeforms.common.core.annotation.RelationConstDict;
import com.orangeforms.common.core.annotation.UploadFlagColumn;
import com.orangeforms.common.core.upload.UploadStoreTypeEnum;
import com.orangeforms.common.core.validator.AddGroup;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.uaaadmin.model.constant.OperatorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.*;

/**
 * SysUaaOperator实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "zz_sys_uaa_operator")
public class SysUaaOperator extends BaseModel {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @TableId(value = "operator_id")
    private Long operatorId;

    /**
     * 用户登录名称。
     */
    @NotBlank(message = "数据验证失败，登录名称不能为空！")
    @TableField(value = "login_name")
    private String loginName;

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
     * 操作员类型(0: 管理员 1: 普通操作员)。
     */
    @NotNull(message = "数据验证失败，操作员类型不能为空！")
    @ConstDictRef(constDictClass = OperatorType.class, message = "数据验证失败，操作员类型为无效值！")
    @TableField(value = "operator_type")
    private Integer operatorType;

    /**
     * 用户头像的Url。
     */
    @UploadFlagColumn(storeType = UploadStoreTypeEnum.LOCAL_SYSTEM)
    @TableField(value = "head_image_url")
    private String headImageUrl;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * login_name / show_name LIKE搜索字符串。
     */
    @TableField(exist = false)
    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = MyCommonUtil.replaceSqlWildcard(searchString);
    }

    @RelationConstDict(
            masterIdField = "operatorType",
            constantDictClass = OperatorType.class)
    @TableField(exist = false)
    private Map<String, Object> operatorTypeDictMap;
}
