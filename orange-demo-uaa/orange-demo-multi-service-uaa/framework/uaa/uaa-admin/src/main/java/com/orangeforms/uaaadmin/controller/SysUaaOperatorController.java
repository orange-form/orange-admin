package com.orangeforms.uaaadmin.controller;

import cn.hutool.core.util.ReflectUtil;
import com.orangeforms.common.core.upload.BaseUpDownloader;
import com.orangeforms.common.core.upload.UpDownloaderFactory;
import com.orangeforms.common.core.upload.UploadResponseInfo;
import com.orangeforms.common.core.upload.UploadStoreInfo;
import com.orangeforms.common.redis.cache.SessionCacheHelper;
import com.orangeforms.uaaadmin.config.ApplicationConfig;
import com.orangeforms.uaaadmin.model.SysUaaOperator;
import com.orangeforms.uaaadmin.service.SysUaaOperatorService;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.validator.AddGroup;
import com.orangeforms.common.core.validator.UpdateGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import javax.validation.groups.Default;

/**
 * 操作员操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/admin/uaaadmin/sysUaaOperator")
public class SysUaaOperatorController {

    @Autowired
    private SysUaaOperatorService sysUaaOperatorService;
    @Autowired
    private ApplicationConfig appConfig;
    @Autowired
    private SessionCacheHelper cacheHelper;
    @Autowired
    private UpDownloaderFactory upDownloaderFactory;

    /**
     * 新增操作员数据。
     *
     * @param sysUaaOperator 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysUaaOperator sysUaaOperator) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUaaOperator, Default.class, AddGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        sysUaaOperator = sysUaaOperatorService.saveNew(sysUaaOperator);
        return ResponseResult.success(sysUaaOperator.getOperatorId());
    }

    /**
     * 更新操作员数据。
     *
     * @param sysUaaOperator 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SysUaaOperator sysUaaOperator) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUaaOperator, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        SysUaaOperator originalSysUaaOperator = sysUaaOperatorService.getById(sysUaaOperator.getOperatorId());
        if (originalSysUaaOperator == null) {
            errorMessage = "数据验证失败，当前UAA操作员并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!sysUaaOperatorService.update(sysUaaOperator, originalSysUaaOperator)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 重置UAA操作员密码到初始化密码。
     *
     * @param uaaOperatorId 操作员Id。
     * @return 应答结果对象。
     */
    @PostMapping("/resetPassword")
    public ResponseResult<Void> resetPassword(@MyRequestBody Long uaaOperatorId) {
        if (MyCommonUtil.existBlankArgument(uaaOperatorId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysUaaOperatorService.changePassword(uaaOperatorId, appConfig.getDefaultUserPassword())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除操作员数据。
     *
     * @param operatorId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long operatorId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(operatorId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        SysUaaOperator originalSysUaaOperator = sysUaaOperatorService.getById(operatorId);
        if (originalSysUaaOperator == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!sysUaaOperatorService.remove(operatorId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的操作员列表。
     *
     * @param sysUaaOperatorFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysUaaOperator>> list(
            @MyRequestBody SysUaaOperator sysUaaOperatorFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUaaOperator.class);
        List<SysUaaOperator> resultList =
                sysUaaOperatorService.getSysUaaOperatorListWithRelation(sysUaaOperatorFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定操作员对象详情。
     *
     * @param operatorId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysUaaOperator> view(@RequestParam Long operatorId) {
        if (MyCommonUtil.existBlankArgument(operatorId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysUaaOperator sysUaaOperator = sysUaaOperatorService.getByIdWithRelation(operatorId, MyRelationParam.full());
        if (sysUaaOperator == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(sysUaaOperator);
    }

    /**
     * 附件文件下载。
     * 这里将图片和其他类型的附件文件放到不同的父目录下，主要为了便于今后图片文件的迁移。
     *
     * @param operatorId 附件所在记录的主键Id。
     * @param fieldName 附件所属的字段名。
     * @param filename  文件名。如果没有提供该参数，就从当前记录的指定字段中读取。
     * @param asImage   下载文件是否为图片。
     * @param response  Http 应答对象。
     */
    @GetMapping("/download")
    public void download(
            @RequestParam Long operatorId,
            @RequestParam String fieldName,
            @RequestParam String filename,
            @RequestParam Boolean asImage,
            HttpServletResponse response) {
        if (MyCommonUtil.existBlankArgument(operatorId, fieldName, filename, asImage)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // 使用try来捕获异常，是为了保证一旦出现异常可以返回500的错误状态，便于调试。
        // 否则有可能给前端返回的是200的错误码。
        try {
            SysUaaOperator sysUaaOperator = sysUaaOperatorService.getById(operatorId);
            if (sysUaaOperator == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String fieldJsonData = (String) ReflectUtil.getFieldValue(sysUaaOperator, fieldName);
            if (fieldJsonData == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (!BaseUpDownloader.containFile(fieldJsonData, filename)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(SysUaaOperator.class, fieldName);
            if (!storeInfo.isSupportUpload()) {
                ResponseResult.output(HttpServletResponse.SC_NOT_IMPLEMENTED,
                        ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
                return;
            }
            BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
            upDownloader.doDownload(appConfig.getUploadFileBaseDir(),
                    SysUaaOperator.class.getSimpleName(), fieldName, filename, asImage, response);
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
     * @param response   Http 应答对象。
     */
    @PostMapping("/upload")
    public void upload(
            @RequestParam String fieldName,
            @RequestParam Boolean asImage,
            @RequestParam("uploadFile") MultipartFile uploadFile,
            HttpServletResponse response) throws Exception {
        UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(SysUaaOperator.class, fieldName);
        if (!storeInfo.isSupportUpload()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
            return;
        }
        BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
        UploadResponseInfo responseInfo = upDownloader.doUpload(null,
                appConfig.getUploadFileBaseDir(), SysUaaOperator.class.getSimpleName(), fieldName, asImage, uploadFile);
        if (responseInfo.getUploadFailed()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            return;
        }
        cacheHelper.putSessionUploadFile(responseInfo.getFilename());
        ResponseResult.output(ResponseResult.success(responseInfo));
    }
}
