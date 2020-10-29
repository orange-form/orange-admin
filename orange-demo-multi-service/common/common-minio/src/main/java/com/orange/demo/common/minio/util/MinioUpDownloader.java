package com.orange.demo.common.minio.util;

import cn.hutool.core.io.IoUtil;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.upload.UpDownloaderFactory;
import com.orange.demo.common.core.upload.UploadResponseInfo;
import com.orange.demo.common.core.upload.BaseUpDownloader;
import com.orange.demo.common.core.upload.UploadStoreTypeEnum;
import com.orange.demo.common.minio.wrapper.MinioTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Objects;

/**
 * 基于Minio上传和下载文件操作的工具类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "minio", name = "enabled")
public class MinioUpDownloader extends BaseUpDownloader {

    @Autowired
    private MinioTemplate minioTemplate;
    @Autowired
    private UpDownloaderFactory factory;

    @PostConstruct
    public void doRegister() {
        factory.registerUpDownloader(UploadStoreTypeEnum.MINIO_SYSTEM, this);
    }

    /**
     * 执行文件上传操作，将文件数据存入Minio。
     *
     * @param serviceContextPath 微服务的上下文路径，如: /admin/upms。
     * @param rootBaseDir        存放上传文件的根目录。(minio中忽略该值，因为使用了bucket)
     * @param modelName          所在数据表的实体对象名。
     * @param fieldName          关联字段的实体对象属性名。
     * @param uploadFile         Http请求中上传的文件对象。
     * @param asImage            是否为图片对象。图片是无需权限验证的，因此和附件存放在不同的子目录。
     * @return 上传应答信息对象。该对象始终不为null。
     * @throws Exception minio抛出的异常。
     */
    @Override
    public UploadResponseInfo doUpload(
            String serviceContextPath,
            String rootBaseDir,
            String modelName,
            String fieldName,
            Boolean asImage,
            MultipartFile uploadFile) throws Exception {
        UploadResponseInfo responseInfo = new UploadResponseInfo();
        if (Objects.isNull(uploadFile) || uploadFile.isEmpty()) {
            responseInfo.setUploadFailed(true);
            responseInfo.setErrorMessage(ErrorCodeEnum.INVALID_UPLOAD_FILE_ARGUMENT.getErrorMessage());
            return responseInfo;
        }
        String uploadPath = super.makeFullPath(null, modelName, fieldName, asImage);
        super.fillUploadResponseInfo(responseInfo, serviceContextPath, uploadFile.getOriginalFilename());
        minioTemplate.putObject(uploadPath + "/" + responseInfo.getFilename(), uploadFile.getInputStream());
        return responseInfo;
    }

    /**
     * 执行下载操作，从Minio读取数据。并将读取的文件数据直接写入到HttpServletResponse应答对象。
     *
     * @param rootBaseDir 文件下载的根目录。(minio中忽略该值，因为使用了bucket)
     * @param modelName   所在数据表的实体对象名。
     * @param fieldName   关联字段的实体对象属性名。
     * @param fileName    文件名。
     * @param asImage     是否为图片对象。图片是无需权限验证的，因此和附件存放在不同的子目录。
     * @param response    Http 应答对象。
     */
    @Override
    public void doDownload(
            String rootBaseDir,
            String modelName,
            String fieldName,
            String fileName,
            Boolean asImage,
            HttpServletResponse response) throws Exception {
        String uploadPath = this.makeFullPath(null, modelName, fieldName, asImage);
        String fullFileanme = uploadPath + "/" + fileName;
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        InputStream in = minioTemplate.getStream(fullFileanme);
        IoUtil.copy(in, response.getOutputStream());
    }
}
