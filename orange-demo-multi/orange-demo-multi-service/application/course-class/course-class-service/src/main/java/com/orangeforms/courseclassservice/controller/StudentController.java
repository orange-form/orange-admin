package com.orangeforms.courseclassservice.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.courseclassservice.model.*;
import com.orangeforms.courseclassservice.service.*;
import com.orangeforms.courseclassapi.dto.*;
import com.orangeforms.courseclassapi.vo.*;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.*;
import com.orangeforms.common.core.base.controller.BaseController;
import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.log.annotation.OperationLog;
import com.orangeforms.common.log.model.constant.SysOperationLogType;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学生数据操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "学生数据管理接口")
@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController extends BaseController<Student, StudentVo, Long> {

    @Autowired
    private StudentService studentService;

    @Override
    protected IBaseService<Student, Long> service() {
        return studentService;
    }

    /**
     * 新增学生数据数据。
     *
     * @param studentDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {
            "studentDto.studentId",
            "studentDto.searchString",
            "studentDto.birthdayStart",
            "studentDto.birthdayEnd",
            "studentDto.registerTimeStart",
            "studentDto.registerTimeEnd"})
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody StudentDto studentDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Student student = MyModelUtil.copyTo(studentDto, Student.class);
        // 验证关联Id的数据合法性
        CallResult callResult = studentService.verifyAllRelatedData(student, null);
        if (!callResult.isSuccess()) {
            return ResponseResult.errorFrom(callResult);
        }
        student = studentService.saveNew(student);
        return ResponseResult.success(student.getStudentId());
    }

    /**
     * 更新学生数据数据。
     *
     * @param studentDto 更新对象。
     * @return 应答结果对象。
     */
    @ApiOperationSupport(ignoreParameters = {
            "StudentDto.searchString",
            "StudentDto.birthdayStart",
            "StudentDto.birthdayEnd",
            "StudentDto.registerTimeStart",
            "StudentDto.registerTimeEnd"})
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody StudentDto studentDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Student student = MyModelUtil.copyTo(studentDto, Student.class);
        Student originalStudent = studentService.getById(student.getStudentId());
        if (originalStudent == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = studentService.verifyAllRelatedData(student, originalStudent);
        if (!callResult.isSuccess()) {
            return ResponseResult.errorFrom(callResult);
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
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long studentId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(studentId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        Student originalStudent = studentService.getById(studentId);
        if (originalStudent == null) {
            // NOTE: 修改下面方括号中的话述
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
    public ResponseResult<MyPageData<StudentVo>> list(
            @MyRequestBody StudentDto studentDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        Student studentFilter = MyModelUtil.copyTo(studentDtoFilter, Student.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Student.class);
        List<Student> studentList = studentService.getStudentListWithRelation(studentFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(studentList, Student.INSTANCE));
    }

    /**
     * 查看指定学生数据对象详情。
     *
     * @param studentId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<StudentVo> view(@RequestParam Long studentId) {
        if (MyCommonUtil.existBlankArgument(studentId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        Student student = studentService.getByIdWithRelation(studentId, MyRelationParam.full());
        if (student == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        StudentVo studentVo = Student.INSTANCE.fromModel(student);
        return ResponseResult.success(studentVo);
    }

    /**
     * 以字典形式返回全部学生数据数据集合。字典的键值为[studentId, studentName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(Student filter) {
        List<Student> resultList = studentService.getListByFilter(filter);
        return ResponseResult.success(
                BeanQuery.select("studentId as id", "studentName as name").executeFrom(resultList));
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
        List<Student> resultList = studentService.getInList(new HashSet<>(dictIds));
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
    @ApiOperation(hidden = true, value = "listByIds")
    @PostMapping("/listByIds")
    public ResponseResult<List<StudentVo>> listByIds(
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
    @ApiOperation(hidden = true, value = "getById")
    @PostMapping("/getById")
    public ResponseResult<StudentVo> getById(
            @RequestParam Long studentId, @RequestParam Boolean withDict) {
        return super.baseGetById(studentId, withDict, Student.INSTANCE);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param studentIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existIds")
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
    @ApiOperation(hidden = true, value = "existId")
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long studentId) {
        return super.baseExistId(studentId);
    }

    /**
     * 保存或更新数据。
     *
     * @param data 主键Id为null时表示新增数据，否则更新数据。
     * @return 应答结果对象，主键Id。
     */
    @ApiOperation(hidden = true, value = "saveNewOrUpdate")
    @PostMapping("/saveNewOrUpdate")
    public ResponseResult<StudentVo> saveNewOrUpdate(@RequestBody StudentDto data) {
        Student student = MyModelUtil.copyTo(data, Student.class);
        service().saveNewOrUpdate(student, studentService::saveNew, studentService::update);
        return ResponseResult.success(Student.INSTANCE.fromModel(student));
    }

    /**
     * 批量新增或保存数据列表。
     *
     * @param dataList 数据列表。主键Id为null时表示新增数据，否则更新数据。
     * @return 应答结果对象。
     */
    @ApiOperation(hidden = true, value = "saveNewOrUpdateBatch")
    @PostMapping("/saveNewOrUpdateBatch")
    public ResponseResult<Void> saveNewOrUpdateBatch(@RequestBody List<StudentDto> dataList) {
        List<Student> studentList = MyModelUtil.copyCollectionTo(dataList, Student.class);
        service().saveNewOrUpdateBatch(studentList, studentService::saveNewBatch, studentService::update);
        return ResponseResult.success();
    }

    /**
     * 根据最新对象列表和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param data 数据对象。
     *             主键有值是视为更新操作的数据比对，因此仅当关联Id变化时才会验证。
     *             主键为空视为新增操作的数据比对，所有关联Id都会被验证。
     * @return 应答结果对象。
     */
    @ApiOperation(hidden = true, value = "verifyRelatedData")
    @PostMapping("/verifyRelatedData")
    public ResponseResult<Void> verifyRelatedData(@RequestBody StudentDto data) {
        Student student = MyModelUtil.copyTo(data, Student.class);
        return super.baseVerifyRelatedData(student, Student::getStudentId);
    }

    /**
     * 根据最新对象列表和原有对象列表的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param dataList 数据对象列表。
     * @return 应答结果对象。
     */
    @ApiOperation(hidden = true, value = "verifyRelatedDataList")
    @PostMapping("/verifyRelatedDataList")
    public ResponseResult<Void> verifyRelatedDataList(@RequestBody List<StudentDto> dataList) {
        List<Student> studentList = MyModelUtil.copyCollectionTo(dataList, Student.class);
        return super.baseVerifyRelatedDataList(studentList, Student::getStudentId);
    }

    /**
     * 根据主键Id删除数据。
     *
     * @param studentId 主键Id。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteById")
    @PostMapping("/deleteById")
    public ResponseResult<Integer> deleteById(@RequestParam Long studentId) throws Exception {
        Student filter = new Student();
        filter.setStudentId(studentId);
        return super.baseDeleteBy(filter);
    }

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteBy")
    @PostMapping("/deleteBy")
    public ResponseResult<Integer> deleteBy(@RequestBody StudentDto filter) throws Exception {
        return super.baseDeleteBy(MyModelUtil.copyTo(filter, Student.class));
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listBy")
    @PostMapping("/listBy")
    public ResponseResult<MyPageData<StudentVo>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, Student.INSTANCE);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listMapBy")
    @PostMapping("/listMapBy")
    public ResponseResult<MyPageData<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListMapBy(queryParam, Student.INSTANCE);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @ApiOperation(hidden = true, value = "getBy")
    @PostMapping("/getBy")
    public ResponseResult<StudentVo> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, Student.INSTANCE);
    }

    /**
     * 获取远程主对象中符合查询条件的数据数量。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @ApiOperation(hidden = true, value = "countBy")
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
    @ApiOperation(hidden = true, value = "aggregateBy")
    @PostMapping("/aggregateBy")
    public ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam) {
        return super.baseAggregateBy(aggregationParam);
    }
}
