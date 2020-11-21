package com.orange.demo.app.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import cn.hutool.core.util.ReflectUtil;
import com.orange.demo.common.core.upload.BaseUpDownloader;
import com.orange.demo.common.core.upload.UpDownloaderFactory;
import com.orange.demo.common.core.upload.UploadResponseInfo;
import com.orange.demo.common.core.upload.UploadStoreInfo;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.app.model.*;
import com.orange.demo.app.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.cache.SessionCacheHelper;
import com.orange.demo.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import javax.validation.groups.Default;

/**
 * 课程数据操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
@RestController
@RequestMapping("/admin/app/course")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private ApplicationConfig appConfig;
    @Autowired
    private SessionCacheHelper cacheHelper;
    @Autowired
    private UpDownloaderFactory upDownloaderFactory;

    /**
     * 新增课程数据数据。
     *
     * @param course 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody Course course) {
        String errorMessage = MyCommonUtil.getModelValidationError(course);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = courseService.verifyRelatedData(course, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        course = courseService.saveNew(course);
        return ResponseResult.success(course.getCourseId());
    }

    /**
     * 更新课程数据数据。
     *
     * @param course 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody Course course) {
        String errorMessage = MyCommonUtil.getModelValidationError(course, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        Course originalCourse = courseService.getById(course.getCourseId());
        if (originalCourse == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = courseService.verifyRelatedData(course, originalCourse);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!courseService.update(course, originalCourse)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除课程数据数据。
     *
     * @param courseId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long courseId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(courseId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        Course originalCourse = courseService.getById(courseId);
        if (originalCourse == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!courseService.remove(courseId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的课程数据列表。
     *
     * @param courseFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<Course>> list(
            @MyRequestBody Course courseFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, Course.class);
        List<Course> resultList = courseService.getCourseListWithRelation(courseFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定课程数据对象详情。
     *
     * @param courseId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<Course> view(@RequestParam Long courseId) {
        if (MyCommonUtil.existBlankArgument(courseId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        Course course = courseService.getByIdWithRelation(courseId, MyRelationParam.full());
        if (course == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(course);
    }

    /**
     * 附件文件下载。
     * 这里将图片和其他类型的附件文件放到不同的父目录下，主要为了便于今后图片文件的迁移。
     *
     * @param courseId 附件所在记录的主键Id。
     * @param fieldName 附件所属的字段名。
     * @param filename  文件名。如果没有提供该参数，就从当前记录的指定字段中读取。
     * @param asImage   下载文件是否为图片。
     * @param response  Http 应答对象。
     */
    @GetMapping("/download")
    public void download(
            @RequestParam(required = false) Long courseId,
            @RequestParam String fieldName,
            @RequestParam String filename,
            @RequestParam Boolean asImage,
            HttpServletResponse response) {
        if (MyCommonUtil.existBlankArgument(fieldName, filename, asImage)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // 使用try来捕获异常，是为了保证一旦出现异常可以返回500的错误状态，便于调试。
        // 否则有可能给前端返回的是200的错误码。
        try {
            // 如果请求参数中没有包含主键Id，就判断该文件是否为当前session上传的。
            if (courseId == null) {
                if (!cacheHelper.existSessionUploadFile(filename)) {
                    ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                Course course = courseService.getById(courseId);
                if (course == null) {
                    ResponseResult.output(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                String fieldJsonData = (String) ReflectUtil.getFieldValue(course, fieldName);
                if (fieldJsonData == null) {
                    ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                if (!BaseUpDownloader.containFile(fieldJsonData, filename)) {
                    ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(Course.class, fieldName);
            if (!storeInfo.isSupportUpload()) {
                ResponseResult.output(HttpServletResponse.SC_NOT_IMPLEMENTED,
                        ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
                return;
            }
            BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
            upDownloader.doDownload(appConfig.getUploadFileBaseDir(),
                    Course.class.getSimpleName(), fieldName, filename, asImage, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 文件上传操作。
     *
     * @param fieldName  上传文件名。
     * @param asImage    是否作为图片上传。如果是图片，今后下载的时候无需权限验证。否则就是附件上传，下载时需要权限验证。
     * @param uploadFile 上传文件对象。
     */
    @PostMapping("/upload")
    public void upload(
            @RequestParam String fieldName,
            @RequestParam Boolean asImage,
            @RequestParam("uploadFile") MultipartFile uploadFile) throws Exception {
        UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(Course.class, fieldName);
        // 这里就会判断参数中指定的字段，是否支持上传操作。
        if (!storeInfo.isSupportUpload()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
            return;
        }
        // 根据字段注解中的存储类型，通过工厂方法获取匹配的上传下载实现类，从而解耦。
        BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
        UploadResponseInfo responseInfo = upDownloader.doUpload(null,
                appConfig.getUploadFileBaseDir(), Course.class.getSimpleName(), fieldName, asImage, uploadFile);
        if (responseInfo.getUploadFailed()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            return;
        }
        cacheHelper.putSessionUploadFile(responseInfo.getFilename());
        ResponseResult.output(ResponseResult.success(responseInfo));
    }

    /**
     * 以字典形式返回全部课程数据数据集合。字典的键值为[courseId, courseName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(Course filter) {
        List<Course> resultList = courseService.getListByFilter(filter, null);
        return ResponseResult.success(BeanQuery.select(
                "courseId as id", "courseName as name").executeFrom(resultList));
    }
}
