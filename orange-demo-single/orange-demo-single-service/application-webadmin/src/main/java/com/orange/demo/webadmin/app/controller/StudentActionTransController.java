package com.orange.demo.webadmin.app.controller;

import com.orange.demo.common.log.annotation.OperationLog;
import com.orange.demo.common.log.model.constant.SysOperationLogType;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.webadmin.app.vo.*;
import com.orange.demo.webadmin.app.dto.*;
import com.orange.demo.webadmin.app.model.*;
import com.orange.demo.webadmin.app.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学生行为流水操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
@RestController
@RequestMapping("/admin/app/studentActionTrans")
public class StudentActionTransController {

    @Autowired
    private StudentActionTransService studentActionTransService;

    /**
     * 新增学生行为流水数据。
     *
     * @param studentActionTransDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody StudentActionTransDto studentActionTransDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentActionTransDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        StudentActionTrans studentActionTrans = MyModelUtil.copyTo(studentActionTransDto, StudentActionTrans.class);
        // 验证关联Id的数据合法性
        CallResult callResult = studentActionTransService.verifyRelatedData(studentActionTrans, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        studentActionTrans = studentActionTransService.saveNew(studentActionTrans);
        return ResponseResult.success(studentActionTrans.getTransId());
    }

    /**
     * 更新学生行为流水数据。
     *
     * @param studentActionTransDto 更新对象。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody StudentActionTransDto studentActionTransDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentActionTransDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        StudentActionTrans studentActionTrans = MyModelUtil.copyTo(studentActionTransDto, StudentActionTrans.class);
        StudentActionTrans originalStudentActionTrans = studentActionTransService.getById(studentActionTrans.getTransId());
        if (originalStudentActionTrans == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = studentActionTransService.verifyRelatedData(studentActionTrans, originalStudentActionTrans);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!studentActionTransService.update(studentActionTrans, originalStudentActionTrans)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除学生行为流水数据。
     *
     * @param transId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long transId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(transId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        StudentActionTrans originalStudentActionTrans = studentActionTransService.getById(transId);
        if (originalStudentActionTrans == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!studentActionTransService.remove(transId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的学生行为流水列表。
     *
     * @param studentActionTransDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<StudentActionTransVo>> list(
            @MyRequestBody StudentActionTransDto studentActionTransDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        StudentActionTrans studentActionTransFilter = MyModelUtil.copyTo(studentActionTransDtoFilter, StudentActionTrans.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, StudentActionTrans.class);
        List<StudentActionTrans> studentActionTransList =
                studentActionTransService.getStudentActionTransListWithRelation(studentActionTransFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(studentActionTransList, StudentActionTrans.INSTANCE));
    }

    /**
     * 查看指定学生行为流水对象详情。
     *
     * @param transId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<StudentActionTransVo> view(@RequestParam Long transId) {
        if (MyCommonUtil.existBlankArgument(transId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        StudentActionTrans studentActionTrans = studentActionTransService.getByIdWithRelation(transId, MyRelationParam.full());
        if (studentActionTrans == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        StudentActionTransVo studentActionTransVo = StudentActionTrans.INSTANCE.fromModel(studentActionTrans);
        return ResponseResult.success(studentActionTransVo);
    }
}
