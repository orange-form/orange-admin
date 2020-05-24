package com.orange.admin.app.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.page.PageMethod;
import com.orange.admin.app.model.*;
import com.orange.admin.app.service.*;
import com.orange.admin.upms.model.*;
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
 * 老师数据源操作控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Slf4j
@RestController
@RequestMapping("/admin/app/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 新增老师数据源数据。
     *
     * @param teacher 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<JSONObject> add(@MyRequestBody Teacher teacher) {
        String errorMessage = MyCommonUtil.getModelValidationError(teacher);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = teacherService.verifyRelatedData(teacher, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        teacher = teacherService.saveNew(teacher);
        JSONObject responseData = new JSONObject();
        responseData.put("teacherId", teacher.getTeacherId());
        return ResponseResult.success(responseData);
    }

    /**
     * 更新老师数据源数据。
     *
     * @param teacher 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody Teacher teacher) {
        String errorMessage = MyCommonUtil.getModelValidationError(teacher, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        Teacher originalTeacher = teacherService.getById(teacher.getTeacherId());
        if (originalTeacher == null) {
            //NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = teacherService.verifyRelatedData(teacher, originalTeacher);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        if (!teacherService.update(teacher, originalTeacher)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除老师数据源数据。
     *
     * @param teacherId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long teacherId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(teacherId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        Teacher originalTeacher = teacherService.getById(teacherId);
        if (originalTeacher == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!teacherService.remove(teacherId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的老师数据源列表。
     *
     * @param teacherFilter 过滤对象。
     * @param sysDeptFilter 一对一从表过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<JSONObject> list(
            @MyRequestBody Teacher teacherFilter,
            @MyRequestBody SysDept sysDeptFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Teacher.class);
        List<Teacher> resultList =
                teacherService.getTeacherListWithRelation(teacherFilter, sysDeptFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定老师数据源对象详情。
     *
     * @param teacherId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<Teacher> view(@RequestParam Long teacherId) {
        if (MyCommonUtil.existBlankArgument(teacherId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        Teacher teacher = teacherService.getByIdWithRelation(teacherId, MyRelationParam.full());
        if (teacher == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(teacher);
    }

    /**
     * 以字典形式返回全部老师数据源数据集合。字典的键值为[teacherId, teacherName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDictTeacher")
    public ResponseResult<List<Map<String, Object>>> listDictTeacher(Teacher filter) {
        List<Teacher> resultList = teacherService.getListByFilter(filter, null);
        return ResponseResult.success(BeanQuery.select(
                "teacherId as id", "teacherName as name").executeFrom(resultList));
    }
}
