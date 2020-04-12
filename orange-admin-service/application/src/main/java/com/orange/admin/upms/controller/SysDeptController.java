package com.orange.admin.upms.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.PageHelper;
import com.orange.admin.upms.model.*;
import com.orange.admin.upms.service.*;
import com.orange.admin.common.core.object.*;
import com.orange.admin.common.core.util.*;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.annotation.MyRequestBody;
import com.orange.admin.common.core.validator.UpdateGroup;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;

/**
 * 部门管理操作控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysDept")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    /**
     * 新增部门管理数据。
     *
     * @param sysDept 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<?> add(@MyRequestBody SysDept sysDept) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        JSONObject responseData = null;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysDept);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            sysDept = sysDeptService.saveNew(sysDept);
            responseData = new JSONObject();
            responseData.put("deptId", sysDept.getDeptId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新部门管理数据。
     *
     * @param sysDept 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<?> update(@MyRequestBody SysDept sysDept) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysDept, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            // 验证关联Id的数据合法性
            SysDept originalSysDept = sysDeptService.getById(sysDept.getDeptId());
            if (originalSysDept == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                //TODO 修改下面方括号中的话述
                errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
                break;
            }
            if (!sysDeptService.update(sysDept, originalSysDept)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除部门管理数据。
     *
     * @param deptId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long deptId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(deptId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            // 验证关联Id的数据合法性
            SysDept originalSysDept = sysDeptService.getById(deptId);
            if (originalSysDept == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                //TODO 修改下面方括号中的话述
                errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
                break;
            }
            if (!sysDeptService.remove(deptId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 列出符合过滤条件的部门管理列表。
     *
     * @param sysDeptFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<?> list(
            @MyRequestBody SysDept sysDeptFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysDept.class);
        List<SysDept> resultList = sysDeptService.getSysDeptListWithRelation(sysDeptFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定部门管理对象详情。
     *
     * @param deptId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysDept> view(@RequestParam Long deptId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        SysDept sysDept = null;
        do {
            if (MyCommonUtil.existBlankArgument(deptId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            sysDept = sysDeptService.getByIdWithRelation(deptId);
            if (sysDept == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, sysDept);
    }

    /**
     * 以字典形式返回全部部门管理数据集合。字典的键值为[deptId, deptName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDictSysDept")
    public ResponseResult<?> listDictSysDept(SysDept filter) {
        List<SysDept> resultList = sysDeptService.getListByFilter(filter);
        return ResponseResult.success(BeanQuery.select(
                "deptId as id", "deptName as name").executeFrom(resultList));
    }
}
