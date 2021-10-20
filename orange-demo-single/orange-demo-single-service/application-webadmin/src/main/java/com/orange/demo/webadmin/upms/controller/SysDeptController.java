package com.orange.demo.webadmin.upms.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.orange.demo.common.log.annotation.OperationLog;
import com.orange.demo.common.log.model.constant.SysOperationLogType;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.webadmin.upms.vo.*;
import com.orange.demo.webadmin.upms.dto.*;
import com.orange.demo.webadmin.upms.model.*;
import com.orange.demo.webadmin.upms.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 部门管理操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
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
     * @param sysDeptDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysDeptDto sysDeptDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysDeptDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysDept sysDept = MyModelUtil.copyTo(sysDeptDto, SysDept.class);
        // 验证父Id的数据合法性
        SysDept parentSysDept = null;
        if (MyCommonUtil.isNotBlankOrNull(sysDept.getParentId())) {
            parentSysDept = sysDeptService.getById(sysDept.getParentId());
            if (parentSysDept == null) {
                errorMessage = "数据验证失败，关联的父节点并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        sysDept = sysDeptService.saveNew(sysDept, parentSysDept);
        return ResponseResult.success(sysDept.getDeptId());
    }

    /**
     * 更新部门管理数据。
     *
     * @param sysDeptDto 更新对象。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SysDeptDto sysDeptDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysDeptDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysDept sysDept = MyModelUtil.copyTo(sysDeptDto, SysDept.class);
        SysDept originalSysDept = sysDeptService.getById(sysDept.getDeptId());
        if (originalSysDept == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证父Id的数据合法性
        if (MyCommonUtil.isNotBlankOrNull(sysDept.getParentId())
                && ObjectUtils.notEqual(sysDept.getParentId(), originalSysDept.getParentId())) {
            SysDept parentSysDept = sysDeptService.getById(sysDept.getParentId());
            if (parentSysDept == null) {
                // NOTE: 修改下面方括号中的话述
                errorMessage = "数据验证失败，关联的 [父节点] 并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        if (!sysDeptService.update(sysDept, originalSysDept)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除部门管理数据。
     *
     * @param deptId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long deptId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(deptId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        SysDept originalSysDept = sysDeptService.getById(deptId);
        if (originalSysDept == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (sysDeptService.hasChildren(deptId)) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象存在子对象] ，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        if (sysDeptService.hasChildrenUser(deptId)) {
            errorMessage = "数据验证失败，请先移除部门用户数据后，再删除当前部门！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        if (!sysDeptService.remove(deptId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的部门管理列表。
     *
     * @param sysDeptDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysDeptVo>> list(
            @MyRequestBody SysDeptDto sysDeptDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysDept sysDeptFilter = MyModelUtil.copyTo(sysDeptDtoFilter, SysDept.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysDept.class);
        List<SysDept> sysDeptList = sysDeptService.getSysDeptListWithRelation(sysDeptFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(sysDeptList, SysDept.INSTANCE));
    }

    /**
     * 查看指定部门管理对象详情。
     *
     * @param deptId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysDeptVo> view(@RequestParam Long deptId) {
        if (MyCommonUtil.existBlankArgument(deptId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysDept sysDept = sysDeptService.getByIdWithRelation(deptId, MyRelationParam.full());
        if (sysDept == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysDeptVo sysDeptVo = SysDept.INSTANCE.fromModel(sysDept);
        return ResponseResult.success(sysDeptVo);
    }

    /**
     * 以字典形式返回全部部门管理数据集合。字典的键值为[deptId, deptName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(SysDept filter) {
        List<SysDept> resultList = sysDeptService.getListByFilter(filter);
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "deptId as id", "deptName as name").executeFrom(resultList));
    }

    /**
     * 根据字典Id集合，获取查询后的字典数据。
     *
     * @param dictIds 字典Id集合。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @PostMapping("/listDictByIds")
    public ResponseResult<List<Map<String, Object>>> listDictByIds(
            @MyRequestBody(elementType = Long.class) List<Long> dictIds) {
        List<SysDept> resultList = sysDeptService.getInList(new HashSet<>(dictIds));
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "deptId as id", "deptName as name").executeFrom(resultList));
    }

    /**
     * 根据父主键Id，以字典的形式返回其下级数据列表。
     * 白名单接口，登录用户均可访问。
     *
     * @param parentId 父主键Id。
     * @return 按照字典的形式返回下级数据列表。
     */
    @GetMapping("/listDictByParentId")
    public ResponseResult<List<Map<String, Object>>> listDictByParentId(@RequestParam(required = false) Long parentId) {
        List<SysDept> resultList = sysDeptService.getListByParentId("parentId", parentId);
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "deptId as id", "deptName as name").executeFrom(resultList));
    }
}
