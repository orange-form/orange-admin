package com.orange.demo.courseclassservice.controller;

import com.orange.demo.common.log.annotation.OperationLog;
import com.orange.demo.common.log.model.constant.SysOperationLogType;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.courseclassservice.service.*;
import com.orange.demo.courseclassapi.dto.*;
import com.orange.demo.courseclassapi.vo.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.IBaseService;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 班级数据操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "班级数据管理接口")
@Slf4j
@RestController
@RequestMapping("/studentClass")
public class StudentClassController extends BaseController<StudentClass, StudentClassVo, Long> {

    @Autowired
    private StudentClassService studentClassService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;

    @Override
    protected IBaseService<StudentClass, Long> service() {
        return studentClassService;
    }

    /**
     * 新增班级数据数据。
     *
     * @param studentClassDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"studentClassDto.classId"})
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody StudentClassDto studentClassDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentClassDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        StudentClass studentClass = MyModelUtil.copyTo(studentClassDto, StudentClass.class);
        // 验证关联Id的数据合法性
        CallResult callResult = studentClassService.verifyRelatedData(studentClass, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证远程服务关联Id的数据合法性
        CallResult remoteCallResult = studentClassService.verifyRemoteRelatedData(studentClass, null);
        if (!remoteCallResult.isSuccess()) {
            errorMessage = remoteCallResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        studentClass = studentClassService.saveNew(studentClass);
        return ResponseResult.success(studentClass.getClassId());
    }

    /**
     * 更新班级数据数据。
     *
     * @param studentClassDto 更新对象。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody StudentClassDto studentClassDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentClassDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        StudentClass studentClass = MyModelUtil.copyTo(studentClassDto, StudentClass.class);
        StudentClass originalStudentClass = studentClassService.getById(studentClass.getClassId());
        if (originalStudentClass == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = studentClassService.verifyRelatedData(studentClass, originalStudentClass);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证远程服务关联Id的数据合法性
        CallResult remoteCallResult = studentClassService.verifyRemoteRelatedData(studentClass, originalStudentClass);
        if (!remoteCallResult.isSuccess()) {
            errorMessage = remoteCallResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!studentClassService.update(studentClass, originalStudentClass)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除班级数据数据。
     *
     * @param classId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long classId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(classId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        StudentClass originalStudentClass = studentClassService.getById(classId);
        if (originalStudentClass == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!studentClassService.remove(classId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的班级数据列表。
     *
     * @param studentClassDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<StudentClassVo>> list(
            @MyRequestBody StudentClassDto studentClassDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        StudentClass studentClassFilter = MyModelUtil.copyTo(studentClassDtoFilter, StudentClass.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, StudentClass.class);
        List<StudentClass> studentClassList =
                studentClassService.getStudentClassListWithRelation(studentClassFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(studentClassList, StudentClass.INSTANCE));
    }

    /**
     * 查看指定班级数据对象详情。
     *
     * @param classId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<StudentClassVo> view(@RequestParam Long classId) {
        if (MyCommonUtil.existBlankArgument(classId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        StudentClass studentClass = studentClassService.getByIdWithRelation(classId, MyRelationParam.full());
        if (studentClass == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        StudentClassVo studentClassVo = StudentClass.INSTANCE.fromModel(studentClass);
        return ResponseResult.success(studentClassVo);
    }

    /**
     * 列出不与指定班级数据存在多对多关系的 [课程数据] 列表数据。
     * 通常用于查看添加新 [课程数据] 对象的候选列表。
     *
     * @param classId 主表主键Id。
     * @param courseDtoFilter [课程数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam  分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listNotInClassCourse")
    public ResponseResult<MyPageData<CourseVo>> listNotInClassCourse(
            @MyRequestBody Long classId,
            @MyRequestBody CourseDto courseDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (MyCommonUtil.isNotBlankOrNull(classId) && !studentClassService.existId(classId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        Course filter = MyModelUtil.copyTo(courseDtoFilter, Course.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Course.class);
        List<Course> courseList;
        if (MyCommonUtil.isNotBlankOrNull(classId)) {
            courseList = courseService.getNotInCourseListByClassId(classId, filter, orderBy);
        } else {
            courseList = courseService.getCourseList(filter, orderBy);
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(courseList, Course.INSTANCE));
    }

    /**
     * 列出与指定班级数据存在多对多关系的 [课程数据] 列表数据。
     *
     * @param classId 主表主键Id。
     * @param courseDtoFilter [课程数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam  分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listClassCourse")
    public ResponseResult<MyPageData<CourseVo>> listClassCourse(
            @MyRequestBody(required = true) Long classId,
            @MyRequestBody CourseDto courseDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (!studentClassService.existId(classId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        Course filter = MyModelUtil.copyTo(courseDtoFilter, Course.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Course.class);
        List<Course> courseList =
                courseService.getCourseListByClassId(classId, filter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(courseList, Course.INSTANCE));
    }

    /**
     * 批量添加班级数据和 [课程数据] 对象的多对多关联关系数据。
     *
     * @param classId 主表主键Id。
     * @param classCourseDtoList 关联对象列表。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.ADD_M2M)
    @PostMapping("/addClassCourse")
    public ResponseResult<Void> addClassCourse(
            @MyRequestBody Long classId,
            @MyRequestBody(elementType = ClassCourseDto.class) List<ClassCourseDto> classCourseDtoList) {
        if (MyCommonUtil.existBlankArgument(classId, classCourseDtoList)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        String errorMessage = MyCommonUtil.getModelValidationError(classCourseDtoList);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Set<Long> courseIdSet =
                classCourseDtoList.stream().map(ClassCourseDto::getCourseId).collect(Collectors.toSet());
        if (!studentClassService.existId(classId)
                || !courseService.existUniqueKeyList("courseId", courseIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        List<ClassCourse> classCourseList =
                MyModelUtil.copyCollectionTo(classCourseDtoList, ClassCourse.class);
        studentClassService.addClassCourseList(classCourseList, classId);
        return ResponseResult.success();
    }

    /**
     * 更新指定班级数据和指定 [课程数据] 的多对多关联数据。
     *
     * @param classCourseDto 对多对中间表对象。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/updateClassCourse")
    public ResponseResult<Void> updateClassCourse(
            @MyRequestBody ClassCourseDto classCourseDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(classCourseDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ClassCourse classCourse = MyModelUtil.copyTo(classCourseDto, ClassCourse.class);
        if (!studentClassService.updateClassCourse(classCourse)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 显示班级数据和指定 [课程数据] 的多对多关联详情数据。
     *
     * @param classId 主表主键Id。
     * @param courseId 从表主键Id。
     * @return 应答结果对象，包括中间表详情。
     */
    @GetMapping("/viewClassCourse")
    public ResponseResult<ClassCourseVo> viewClassCourse(
            @RequestParam Long classId, @RequestParam Long courseId) {
        if (MyCommonUtil.existBlankArgument(classId, courseId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ClassCourse classCourse = studentClassService.getClassCourse(classId, courseId);
        if (classCourse == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        ClassCourseVo classCourseVo = MyModelUtil.copyTo(classCourse, ClassCourseVo.class);
        return ResponseResult.success(classCourseVo);
    }

    /**
     * 移除指定班级数据和指定 [课程数据] 的多对多关联关系。
     *
     * @param classId 主表主键Id。
     * @param courseId 关联表主键Id。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.DELETE_M2M)
    @PostMapping("/deleteClassCourse")
    public ResponseResult<Void> deleteClassCourse(
            @MyRequestBody Long classId, @MyRequestBody Long courseId) {
        if (MyCommonUtil.existBlankArgument(classId, courseId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!studentClassService.removeClassCourse(classId, courseId)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 列出不与指定班级数据存在多对多关系的 [学生数据] 列表数据。
     * 通常用于查看添加新 [学生数据] 对象的候选列表。
     *
     * @param classId 主表主键Id。
     * @param studentDtoFilter [学生数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam  分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listNotInClassStudent")
    public ResponseResult<MyPageData<StudentVo>> listNotInClassStudent(
            @MyRequestBody Long classId,
            @MyRequestBody StudentDto studentDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (MyCommonUtil.isNotBlankOrNull(classId) && !studentClassService.existId(classId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        Student filter = MyModelUtil.copyTo(studentDtoFilter, Student.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Student.class);
        List<Student> studentList;
        if (MyCommonUtil.isNotBlankOrNull(classId)) {
            studentList = studentService.getNotInStudentListByClassId(classId, filter, orderBy);
        } else {
            studentList = studentService.getStudentList(filter, orderBy);
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(studentList, Student.INSTANCE));
    }

    /**
     * 列出与指定班级数据存在多对多关系的 [学生数据] 列表数据。
     *
     * @param classId 主表主键Id。
     * @param studentDtoFilter [学生数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam  分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listClassStudent")
    public ResponseResult<MyPageData<StudentVo>> listClassStudent(
            @MyRequestBody(required = true) Long classId,
            @MyRequestBody StudentDto studentDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (!studentClassService.existId(classId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        Student filter = MyModelUtil.copyTo(studentDtoFilter, Student.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Student.class);
        List<Student> studentList =
                studentService.getStudentListByClassId(classId, filter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(studentList, Student.INSTANCE));
    }

    /**
     * 批量添加班级数据和 [学生数据] 对象的多对多关联关系数据。
     *
     * @param classId 主表主键Id。
     * @param classStudentDtoList 关联对象列表。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.ADD_M2M)
    @PostMapping("/addClassStudent")
    public ResponseResult<Void> addClassStudent(
            @MyRequestBody Long classId,
            @MyRequestBody(elementType = ClassStudentDto.class) List<ClassStudentDto> classStudentDtoList) {
        if (MyCommonUtil.existBlankArgument(classId, classStudentDtoList)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        String errorMessage = MyCommonUtil.getModelValidationError(classStudentDtoList);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Set<Long> studentIdSet =
                classStudentDtoList.stream().map(ClassStudentDto::getStudentId).collect(Collectors.toSet());
        if (!studentClassService.existId(classId)
                || !studentService.existUniqueKeyList("studentId", studentIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        List<ClassStudent> classStudentList =
                MyModelUtil.copyCollectionTo(classStudentDtoList, ClassStudent.class);
        studentClassService.addClassStudentList(classStudentList, classId);
        return ResponseResult.success();
    }

    /**
     * 移除指定班级数据和指定 [学生数据] 的多对多关联关系。
     *
     * @param classId 主表主键Id。
     * @param studentId 关联表主键Id。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.DELETE_M2M)
    @PostMapping("/deleteClassStudent")
    public ResponseResult<Void> deleteClassStudent(
            @MyRequestBody Long classId, @MyRequestBody Long studentId) {
        if (MyCommonUtil.existBlankArgument(classId, studentId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!studentClassService.removeClassStudent(classId, studentId)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param classIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    @ApiOperation(hidden = true, value = "listByIds")
    @PostMapping("/listByIds")
    public ResponseResult<List<StudentClassVo>> listByIds(
            @RequestParam Set<Long> classIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(classIds, withDict, StudentClass.INSTANCE);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param classId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @ApiOperation(hidden = true, value = "getById")
    @PostMapping("/getById")
    public ResponseResult<StudentClassVo> getById(
            @RequestParam Long classId, @RequestParam Boolean withDict) {
        return super.baseGetById(classId, withDict, StudentClass.INSTANCE);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param classIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existIds")
    @PostMapping("/existIds")
    public ResponseResult<Boolean> existIds(@RequestParam Set<Long> classIds) {
        return super.baseExistIds(classIds);
    }

    /**
     * 判断参数列表中指定的主键Id是否存在。仅限于微服务间远程接口调用。
     *
     * @param classId 主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existId")
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long classId) {
        return super.baseExistId(classId);
    }

    /**
     * 根据主键Id删除数据。
     *
     * @param classId 主键Id。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteById")
    @PostMapping("/deleteById")
    public ResponseResult<Integer> deleteById(@RequestParam Long classId) throws Exception {
        StudentClass filter = new StudentClass();
        filter.setClassId(classId);
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
    public ResponseResult<Integer> deleteBy(@RequestBody StudentClassDto filter) throws Exception {
        return super.baseDeleteBy(MyModelUtil.copyTo(filter, StudentClass.class));
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listBy")
    @PostMapping("/listBy")
    public ResponseResult<MyPageData<StudentClassVo>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, StudentClass.INSTANCE);
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
        return super.baseListMapBy(queryParam, StudentClass.INSTANCE);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @ApiOperation(hidden = true, value = "getBy")
    @PostMapping("/getBy")
    public ResponseResult<StudentClassVo> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, StudentClass.INSTANCE);
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

    private String doBusinessDataVerify(
            StudentClassDto studentClassDto,
            boolean forUpdate,
            List<ClassCourseDto> classCourseDtoList,
            List<ClassStudentDto> classStudentDtoList) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentClassDto, forUpdate);
        if (errorMessage != null) {
            return errorMessage;
        }
        errorMessage = MyCommonUtil.getModelValidationError(classCourseDtoList);
        if (errorMessage != null) {
            return "参数 [classCourseDtoList] 数据验证失败" + errorMessage;
        }
        errorMessage = MyCommonUtil.getModelValidationError(classStudentDtoList);
        if (errorMessage != null) {
            return "参数 [classStudentDtoList] 数据验证失败" + errorMessage;
        }
        return null;
    }
}
