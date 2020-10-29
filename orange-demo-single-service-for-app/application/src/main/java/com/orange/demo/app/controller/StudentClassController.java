package com.orange.demo.app.controller;

import com.github.pagehelper.page.PageMethod;
import com.orange.demo.app.model.*;
import com.orange.demo.app.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;
import java.util.stream.Collectors;

/**
 * 班级数据操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Api(tags = "班级数据管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/studentClass")
public class StudentClassController {

    @Autowired
    private StudentClassService studentClassService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;

    /**
     * 新增班级数据数据。
     *
     * @param studentClass 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"studentClass.userId"})
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody StudentClass studentClass) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentClass);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = studentClassService.verifyRelatedData(studentClass, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        studentClass = studentClassService.saveNew(studentClass);
        return ResponseResult.success(studentClass.getClassId());
    }

    /**
     * 更新班级数据数据。
     *
     * @param studentClass 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody StudentClass studentClass) {
        String errorMessage = MyCommonUtil.getModelValidationError(studentClass, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        StudentClass originalStudentClass = studentClassService.getById(studentClass.getClassId());
        if (originalStudentClass == null) {
            //NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = studentClassService.verifyRelatedData(studentClass, originalStudentClass);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
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
     * @param studentClassFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<StudentClass>> list(
            @MyRequestBody StudentClass studentClassFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, StudentClass.class);
        List<StudentClass> resultList = studentClassService.getStudentClassListWithRelation(studentClassFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定班级数据对象详情。
     *
     * @param classId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<StudentClass> view(@RequestParam Long classId) {
        if (MyCommonUtil.existBlankArgument(classId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        StudentClass studentClass = studentClassService.getByIdWithRelation(classId, MyRelationParam.full());
        if (studentClass == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(studentClass);
    }

    /**
     * 列出不与指定班级数据存在多对多关系的 [课程数据] 列表数据。通常用于查看添加新 [课程数据] 对象的候选列表。
     *
     * @param classId 主表关联字段。
     * @param courseFilter [课程数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listNotInClassCourse")
    public ResponseResult<MyPageData<Course>> listNotInClassCourse(
            @MyRequestBody Long classId,
            @MyRequestBody Course courseFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doClassCourseVerify(classId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Course.class);
        List<Course> resultList =
                courseService.getNotInCourseListByClassId(classId, courseFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 列出与指定班级数据存在多对多关系的 [课程数据] 列表数据。
     *
     * @param classId 主表关联字段。
     * @param courseFilter [课程数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listClassCourse")
    public ResponseResult<MyPageData<Course>> listClassCourse(
            @MyRequestBody Long classId,
            @MyRequestBody Course courseFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doClassCourseVerify(classId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Course.class);
        List<Course> resultList =
                courseService.getCourseListByClassId(classId, courseFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    private ResponseResult<Void> doClassCourseVerify(Long classId) {
        if (MyCommonUtil.existBlankArgument(classId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!studentClassService.existId(classId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        return ResponseResult.success();
    }

    /**
     * 批量添加班级数据和 [课程数据] 对象的多对多关联关系数据。
     *
     * @param classId 主表主键Id。
     * @param classCourseList 关联对象列表。
     * @return 应答结果对象。
     */
    @PostMapping("/addClassCourse")
    public ResponseResult<Void> addClassCourse(
            @MyRequestBody Long classId,
            @MyRequestBody(elementType = ClassCourse.class) List<ClassCourse> classCourseList) {
        if (MyCommonUtil.existBlankArgument(classId, classCourseList)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        for (ClassCourse classCourse : classCourseList) {
            String errorMessage = MyCommonUtil.getModelValidationError(classCourse);
            if (errorMessage != null) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
            }
        }
        Set<Long> courseIdSet =
                classCourseList.stream().map(ClassCourse::getCourseId).collect(Collectors.toSet());
        if (!studentClassService.existId(classId)
                || !courseService.existUniqueKeyList("courseId", courseIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        studentClassService.addClassCourseList(classCourseList, classId);
        return ResponseResult.success();
    }

    /**
     * 更新指定班级数据和指定 [课程数据] 的多对多关联数据。
     *
     * @param classCourse 对多对中间表对象。
     * @return 应答结果对象。
     */
    @PostMapping("/updateClassCourse")
    public ResponseResult<Void> updateClassCourse(@MyRequestBody ClassCourse classCourse) {
        String errorMessage = MyCommonUtil.getModelValidationError(classCourse);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
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
    public ResponseResult<ClassCourse> viewClassCourse(
            @RequestParam Long classId, @RequestParam Long courseId) {
        if (MyCommonUtil.existBlankArgument(classId, courseId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ClassCourse classCourse = studentClassService.getClassCourse(classId, courseId);
        if (classCourse == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(classCourse);
    }

    /**
     * 移除指定班级数据和指定 [课程数据] 的多对多关联关系。
     *
     * @param classId 主表主键Id。
     * @param courseId 从表主键Id。
     * @return 应答结果对象。
     */
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
     * 列出不与指定班级数据存在多对多关系的 [学生数据] 列表数据。通常用于查看添加新 [学生数据] 对象的候选列表。
     *
     * @param classId 主表关联字段。
     * @param studentFilter [学生数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listNotInClassStudent")
    public ResponseResult<MyPageData<Student>> listNotInClassStudent(
            @MyRequestBody Long classId,
            @MyRequestBody Student studentFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doClassStudentVerify(classId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Student.class);
        List<Student> resultList =
                studentService.getNotInStudentListByClassId(classId, studentFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 列出与指定班级数据存在多对多关系的 [学生数据] 列表数据。
     *
     * @param classId 主表关联字段。
     * @param studentFilter [学生数据] 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，返回符合条件的数据列表。
     */
    @PostMapping("/listClassStudent")
    public ResponseResult<MyPageData<Student>> listClassStudent(
            @MyRequestBody Long classId,
            @MyRequestBody Student studentFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doClassStudentVerify(classId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Student.class);
        List<Student> resultList =
                studentService.getStudentListByClassId(classId, studentFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    private ResponseResult<Void> doClassStudentVerify(Long classId) {
        if (MyCommonUtil.existBlankArgument(classId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!studentClassService.existId(classId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        return ResponseResult.success();
    }

    /**
     * 批量添加班级数据和 [学生数据] 对象的多对多关联关系数据。
     *
     * @param classId 主表主键Id。
     * @param classStudentList 关联对象列表。
     * @return 应答结果对象。
     */
    @PostMapping("/addClassStudent")
    public ResponseResult<Void> addClassStudent(
            @MyRequestBody Long classId,
            @MyRequestBody(elementType = ClassStudent.class) List<ClassStudent> classStudentList) {
        if (MyCommonUtil.existBlankArgument(classId, classStudentList)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        for (ClassStudent classStudent : classStudentList) {
            String errorMessage = MyCommonUtil.getModelValidationError(classStudent);
            if (errorMessage != null) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
            }
        }
        Set<Long> studentIdSet =
                classStudentList.stream().map(ClassStudent::getStudentId).collect(Collectors.toSet());
        if (!studentClassService.existId(classId)
                || !studentService.existUniqueKeyList("studentId", studentIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        studentClassService.addClassStudentList(classStudentList, classId);
        return ResponseResult.success();
    }

    /**
     * 移除指定班级数据和指定 [学生数据] 的多对多关联关系。
     *
     * @param classId 主表主键Id。
     * @param studentId 从表主键Id。
     * @return 应答结果对象。
     */
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
}
