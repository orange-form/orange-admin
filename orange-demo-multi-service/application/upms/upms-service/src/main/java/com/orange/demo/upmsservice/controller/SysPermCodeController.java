package com.orange.demo.upmsservice.controller;

import com.github.pagehelper.page.PageMethod;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.util.MyPageUtil;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.upmsinterface.dto.SysPermCodeDto;
import com.orange.demo.upmsservice.model.SysPermCode;
import com.orange.demo.upmsservice.service.SysPermCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 权限字管理接口控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "权限字管理接口")
@Slf4j
@RestController
@RequestMapping("/sysPermCode")
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
    @SuppressWarnings("unchecked")
    @ApiOperationSupport(ignoreParameters = {"sysPermCode.permCodeId"})
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody("sysPermCode") SysPermCodeDto sysPermCodeDto, @MyRequestBody String permIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermCodeDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED);
        }
        SysPermCode sysPermCode = MyModelUtil.copyTo(sysPermCodeDto, SysPermCode.class);
        CallResult result = sysPermCodeService.verifyRelatedData(sysPermCode, null, permIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        Set<Long> permIdSet = null;
        if (result.getData() != null) {
            permIdSet = (Set<Long>) result.getData().get("permIdSet");
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
    @SuppressWarnings("unchecked")
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody("sysPermCode") SysPermCodeDto sysPermCodeDto, @MyRequestBody String permIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermCodeDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysPermCode originalSysPermCode = sysPermCodeService.getById(sysPermCodeDto.getPermCodeId());
        if (originalSysPermCode == null) {
            errorMessage = "数据验证失败，当前权限字并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        SysPermCode sysPermCode = MyModelUtil.copyTo(sysPermCodeDto, SysPermCode.class);
        CallResult result = sysPermCodeService.verifyRelatedData(sysPermCode, originalSysPermCode, permIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        Set<Long> permIdSet = null;
        if (result.getData() != null) {
            permIdSet = (Set<Long>) result.getData().get("permIdSet");
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
    public ResponseResult<List<SysPermCodeDto>> list() {
        List<SysPermCode> sysPermCodeList =
                sysPermCodeService.getAllListByOrder("permCodeType", "showOrder");
        return ResponseResult.success(MyModelUtil.copyCollectionTo(sysPermCodeList, SysPermCodeDto.class));
    }

    /**
     * 查看权限字对象详情。
     *
     * @param permCodeId 指定权限字主键Id。
     * @return 应答结果对象，包含权限字对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysPermCodeDto> view(@RequestParam Long permCodeId) {
        if (MyCommonUtil.existBlankArgument(permCodeId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysPermCode sysPermCode =
                sysPermCodeService.getByIdWithRelation(permCodeId, MyRelationParam.full());
        if (sysPermCode == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysPermCodeDto sysPermCodeDto = MyModelUtil.copyTo(sysPermCode, SysPermCodeDto.class);
        return ResponseResult.success(sysPermCodeDto);
    }

    /**
     * 查看用户关联的权限字列表。
     *
     * @param loginName 精确匹配用户登录名。
     * @param permCode  模糊匹配的权限字名，LIKE %permCode%。
     * @param pageParam 分页对象。
     * @return 应答结果对象，包含该用户的全部权限资源列表。
     */
    @PostMapping("/listAllPermCodesByUserFilter")
    public ResponseResult<MyPageData<SysPermCodeDto>> listAllPermCodesByUserFilter(
            @MyRequestBody String loginName,
            @MyRequestBody String permCode,
            @MyRequestBody MyPageParam pageParam) {
        if (MyCommonUtil.existBlankArgument(loginName)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<SysPermCode> permCodeList =
                sysPermCodeService.getUserPermCodeListByFilter(loginName, permCode);
        MyPageData<SysPermCodeDto> pageData = MyPageUtil.makeResponseData(
                MyModelUtil.copyCollectionTo(permCodeList, SysPermCodeDto.class));
        return ResponseResult.success(pageData);
    }
}
