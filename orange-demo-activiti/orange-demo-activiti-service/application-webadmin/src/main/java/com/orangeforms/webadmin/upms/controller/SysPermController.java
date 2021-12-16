package com.orangeforms.webadmin.upms.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import com.orangeforms.webadmin.upms.dto.SysPermDto;
import com.orangeforms.webadmin.upms.vo.SysPermVo;
import com.orangeforms.webadmin.upms.model.SysPerm;
import com.orangeforms.webadmin.upms.service.SysPermService;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Map;

/**
 * 权限资源管理接口控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysPerm")
public class SysPermController {

    @Autowired
    private SysPermService sysPermService;

    /**
     * 新增权限资源操作。
     *
     * @param sysPermDto 新增权限资源对象。
     * @return 应答结果对象，包含新增权限资源的主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysPermDto sysPermDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysPerm sysPerm = MyModelUtil.copyTo(sysPermDto, SysPerm.class);
        CallResult result = sysPermService.verifyRelatedData(sysPerm, null);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        sysPerm = sysPermService.saveNew(sysPerm);
        return ResponseResult.success(sysPerm.getPermId());
    }

    /**
     * 更新权限资源操作。
     *
     * @param sysPermDto 更新权限资源对象。
     * @return 应答结果对象，包含更新权限资源的主键Id。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SysPermDto sysPermDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysPerm originalPerm = sysPermService.getById(sysPermDto.getPermId());
        if (originalPerm == null) {
            errorMessage = "数据验证失败，当前权限资源并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        SysPerm sysPerm = MyModelUtil.copyTo(sysPermDto, SysPerm.class);
        CallResult result = sysPermService.verifyRelatedData(sysPerm, originalPerm);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        sysPermService.update(sysPerm, originalPerm);
        return ResponseResult.success();
    }

    /**
     * 删除指定权限资源操作。
     *
     * @param permId 指定的权限资源主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long permId) {
        if (MyCommonUtil.existBlankArgument(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysPermService.remove(permId)) {
            String errorMessage = "数据操作失败，权限不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 查看权限资源对象详情。
     *
     * @param permId 指定权限资源主键Id。
     * @return 应答结果对象，包含权限资源对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysPermVo> view(@RequestParam Long permId) {
        if (MyCommonUtil.existBlankArgument(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysPerm perm = sysPermService.getById(permId);
        if (perm == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysPermVo permVo = MyModelUtil.copyTo(perm, SysPermVo.class);
        return ResponseResult.success(permVo);
    }

    /**
     * 查看权限资源列表。
     *
     * @param sysPermDtoFilter 过滤对象。
     * @param pageParam         分页参数。
     * @return 应答结果对象，包含权限资源列表。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysPermVo>> list(
            @MyRequestBody SysPermDto sysPermDtoFilter, @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysPerm filter = MyModelUtil.copyTo(sysPermDtoFilter, SysPerm.class);
        List<SysPerm> permList = sysPermService.getPermListWithRelation(filter);
        List<SysPermVo> permVoList = MyModelUtil.copyCollectionTo(permList, SysPermVo.class);
        long totalCount = 0L;
        if (permList instanceof Page) {
            totalCount = ((Page<SysPerm>) permList).getTotal();
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(permVoList, totalCount));
    }

    /**
     * 查询权限资源地址的用户列表。同时返回详细的分配路径。
     *
     * @param permId    权限资源Id。
     * @param loginName 登录名。
     * @return 应答对象。包含从权限资源到用户的完整权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysUserWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysUserWithDetail(Long permId, String loginName) {
        if (MyCommonUtil.isBlankOrNull(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysPermService.getSysUserListWithDetail(permId, loginName));
    }

    /**
     * 查询权限资源地址的角色列表。同时返回详细的分配路径。
     *
     * @param permId   权限资源Id。
     * @param roleName 角色名。
     * @return 应答对象。包含从权限资源到角色的权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysRoleWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysRoleWithDetail(Long permId, String roleName) {
        if (MyCommonUtil.isBlankOrNull(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysPermService.getSysRoleListWithDetail(permId, roleName));
    }

    /**
     * 查询权限资源地址的菜单列表。同时返回详细的分配路径。
     *
     * @param permId   权限资源Id。
     * @param menuName 菜单名。
     * @return 应答对象。包含从权限资源到菜单的权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysMenuWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysMenuWithDetail(Long permId, String menuName) {
        if (MyCommonUtil.isBlankOrNull(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysPermService.getSysMenuListWithDetail(permId, menuName));
    }
}
