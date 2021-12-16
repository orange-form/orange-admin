package com.orangeforms.webadmin.upms.controller;

import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import com.orangeforms.webadmin.upms.dto.SysPermCodeDto;
import com.orangeforms.webadmin.upms.vo.SysPermCodeVo;
import com.orangeforms.webadmin.upms.model.SysPermCode;
import com.orangeforms.webadmin.upms.service.SysPermCodeService;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 权限字管理接口控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysPermCode")
public class SysPermCodeController {

    @Autowired
    private SysPermCodeService sysPermCodeService;

    /**
     * 新增权限字操作。
     *
     * @param sysPermCodeDto   新增权限字对象。
     * @param permIdListString 与当前权限Id绑定的权限资源Id列表，多个权限资源之间逗号分隔。
     * @return 应答结果对象，包含新增权限字的主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody SysPermCodeDto sysPermCodeDto, @MyRequestBody String permIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermCodeDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED);
        }
        SysPermCode sysPermCode = MyModelUtil.copyTo(sysPermCodeDto, SysPermCode.class);
        CallResult result = sysPermCodeService.verifyRelatedData(sysPermCode, null, permIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> permIdSet = null;
        if (result.getData() != null) {
            permIdSet = result.getData().getObject("permIdSet", new TypeReference<Set<Long>>(){});
        }
        sysPermCode = sysPermCodeService.saveNew(sysPermCode, permIdSet);
        return ResponseResult.success(sysPermCode.getPermCodeId());
    }

    /**
     * 更新权限字操作。
     *
     * @param sysPermCodeDto   更新权限字对象。
     * @param permIdListString 与当前权限Id绑定的权限资源Id列表，多个权限资源之间逗号分隔。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody SysPermCodeDto sysPermCodeDto, @MyRequestBody String permIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermCodeDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysPermCode originalSysPermCode = sysPermCodeService.getById(sysPermCodeDto.getPermCodeId());
        if (originalSysPermCode == null) {
            errorMessage = "数据验证失败，当前权限字并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        SysPermCode sysPermCode = MyModelUtil.copyTo(sysPermCodeDto, SysPermCode.class);
        CallResult result = sysPermCodeService.verifyRelatedData(sysPermCode, originalSysPermCode, permIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> permIdSet = null;
        if (result.getData() != null) {
            permIdSet = result.getData().getObject("permIdSet", new TypeReference<Set<Long>>(){});
        }
        try {
            if (!sysPermCodeService.update(sysPermCode, originalSysPermCode, permIdSet)) {
                errorMessage = "数据验证失败，当前权限字并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
            }
        } catch (DuplicateKeyException e) {
            errorMessage = "数据操作失败，权限字编码已经存在！";
            return ResponseResult.error(ErrorCodeEnum.DUPLICATED_UNIQUE_KEY, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 删除指定权限字操作。
     *
     * @param permCodeId 指定的权限字主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long permCodeId) {
        if (MyCommonUtil.existBlankArgument(permCodeId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        String errorMessage;
        if (sysPermCodeService.hasChildren(permCodeId)) {
            errorMessage = "数据验证失败，当前权限字存在下级权限字！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        if (!sysPermCodeService.remove(permCodeId)) {
            errorMessage = "数据操作失败，权限字不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 查看权限字列表。
     *
     * @return 应答结果对象，包含权限字列表。
     */
    @PostMapping("/list")
    public ResponseResult<List<SysPermCodeVo>> list() {
        List<SysPermCode> sysPermCodeList =
                sysPermCodeService.getAllListByOrder("permCodeType", "showOrder");
        return ResponseResult.success(MyModelUtil.copyCollectionTo(sysPermCodeList, SysPermCodeVo.class));
    }

    /**
     * 查看权限字对象详情。
     *
     * @param permCodeId 指定权限字主键Id。
     * @return 应答结果对象，包含权限字对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysPermCodeVo> view(@RequestParam Long permCodeId) {
        if (MyCommonUtil.existBlankArgument(permCodeId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysPermCode sysPermCode =
                sysPermCodeService.getByIdWithRelation(permCodeId, MyRelationParam.full());
        if (sysPermCode == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysPermCodeVo sysPermCodeVo = MyModelUtil.copyTo(sysPermCode, SysPermCodeVo.class);
        return ResponseResult.success(sysPermCodeVo);
    }

    /**
     * 查询权限字的用户列表。同时返回详细的分配路径。
     *
     * @param permCodeId 权限字Id。
     * @param loginName  登录名。
     * @return 应答对象。包含从权限字到用户的完整权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysUserWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysUserWithDetail(Long permCodeId, String loginName) {
        if (MyCommonUtil.isBlankOrNull(permCodeId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysPermCodeService.getSysUserListWithDetail(permCodeId, loginName));
    }

    /**
     * 查询权限字的角色列表。同时返回详细的分配路径。
     *
     * @param permCodeId 权限字Id。
     * @param roleName   角色名。
     * @return 应答对象。包含从权限字到角色的权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysRoleWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysRoleWithDetail(Long permCodeId, String roleName) {
        if (MyCommonUtil.isBlankOrNull(permCodeId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysPermCodeService.getSysRoleListWithDetail(permCodeId, roleName));
    }
}
