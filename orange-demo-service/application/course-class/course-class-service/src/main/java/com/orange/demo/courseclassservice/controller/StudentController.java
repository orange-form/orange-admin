package com.orange.demo.courseclassservice.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.courseclassservice.service.*;
import com.orange.demo.courseclassinterface.dto.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 学生数据操作控制器类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController extends BaseController<Student, StudentDto, Long> {

    @Autowired
    private StudentService studentService;

    @Override
    protected BaseService<Student, StudentDto, Long> service() {
        return studentService;
    }

    /**
     * 新增学生数据数据。
     *
     * @param studentDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<JSONObject> add(@MyRequestBody("student") StudentDto studentDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        Student student = Student.INSTANCE.toModel(studentDto);
        // 验证关联Id的数据合法性
        CallResult callResult = studentService.verifyRelatedData(student, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        student = studentService.saveNew(student);
        JSONObject responseData = new JSONObject();
        responseData.put("studentId", student.getStudentId());
        return ResponseResult.success(responseData);
    }

    /**
     * 更新学生数据数据。
     *
     * @param studentDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody("student") StudentDto studentDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        Student student = Student.INSTANCE.toModel(studentDto);
        Student originalStudent = studentService.getById(student.getStudentId());
        if (originalStudent == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = studentService.verifyRelatedData(student, originalStudent);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        if (!studentService.update(student, originalStudent)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除学生数据数据。
     *
     * @param studentId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long studentId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(studentId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        Student originalStudent = studentService.getById(studentId);
        if (originalStudent == null) {
            //NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!studentService.remove(studentId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的学生数据列表。
     *
     * @param studentDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<JSONObject> list(
            @MyRequestBody("studentFilter") StudentDto studentDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        Student studentFilter = Student.INSTANCE.toModel(studentDtoFilter);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Student.class);
        List<Student> studentList =
                studentService.getStudentListWithRelation(studentFilter, orderBy);
        long totalCount = 0L;
        if (studentList instanceof Page) {
            totalCount = ((Page<Student>) studentList).getTotal();
        }
        // 分页连同对象数据转换copy工作，下面的方法一并完成。
        Tuple2<List<StudentDto>, Long> responseData =
                new Tuple2<>(Student.INSTANCE.fromModelList(studentList), totalCount);
        return ResponseResult.success(MyPageUtil.makeResponseData(responseData));
    }

    /**
     * 查看指定学生数据对象详情。
     *
     * @param studentId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<StudentDto> view(@RequestParam Long studentId) {
        if (MyCommonUtil.existBlankArgument(studentId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        Student student =
                studentService.getByIdWithRelation(studentId, MyRelationParam.full());
        if (student == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        StudentDto studentDto = Student.INSTANCE.fromModel(student);
        return ResponseResult.success(studentDto);
    }

    /**
     * 以字典形式返回全部学生数据数据集合。字典的键值为[studentId, studentName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listDictStudent")
    public ResponseResult<List<Map<String, Object>>> listDictStudent(Student filter) {
        List<Student> resultList = studentService.getListByFilter(filter, null);
        return ResponseResult.success(
                BeanQuery.select("studentId as id", "studentName as name").executeFrom(resultList));
    }

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param studentIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    @PostMapping("/listByIds")
    public ResponseResult<List<StudentDto>> listByIds(
            @RequestParam Set<Long> studentIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(studentIds, withDict, Student.INSTANCE);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param studentId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @PostMapping("/getById")
    public ResponseResult<StudentDto> getById(
            @RequestParam Long studentId, @RequestParam Boolean withDict) {
        return super.baseGetById(studentId, withDict, Student.INSTANCE);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param studentIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @PostMapping("/existIds")
    public ResponseResult<Boolean> existIds(@RequestParam Set<Long> studentIds) {
        return super.baseExistIds(studentIds);
    }

    /**
     * 判断参数列表中指定的主键Id是否存在。仅限于微服务间远程接口调用。
     *
     * @param studentId 主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long studentId) {
        return super.baseExistId(studentId);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/listBy")
    public ResponseResult<List<StudentDto>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, Student.INSTANCE);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/listMapBy")
    public ResponseResult<List<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListMapBy(queryParam, Student.INSTANCE);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/getBy")
    public ResponseResult<StudentDto> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, Student.INSTANCE);
    }

    /**
     * 获取远程主对象中符合查询条件的数据数量。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @PostMapping("/countBy")
    public ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam) {
        return super.baseCountBy(queryParam);
    }

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @PostMapping("/aggregateBy")
    public ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam) {
        return super.baseAggregateBy(aggregationParam);
    }
}
