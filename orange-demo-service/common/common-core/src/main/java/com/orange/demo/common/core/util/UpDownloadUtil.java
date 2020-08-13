package com.orange.demo.common.core.util;

import com.alibaba.fastjson.JSON;
import com.orange.demo.common.core.constant.ApplicationConstant;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.ResponseResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * 上传或下载附件文件的工具类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Slf4j
public class UpDownloadUtil {

    /**
     * 执行下载操作，并将读取的文件数据直接写入到HttpServletResponse应答对象。
     *
     * @param rootBaseDir 文件下载的根目录。
     * @param modelName   所在数据表的实体对象名。
     * @param fieldName   关联字段的实体对象属性名。
     * @param fileName    文件名。
     * @param asImage     是否为图片对象。图片是无需权限验证的，因此和附件存放在不同的子目录。
     * @param response    Http 应答对象。
     */
    public static void doDownload(
            String rootBaseDir,
            String modelName,
            String fieldName,
            String fileName,
            Boolean asImage,
            HttpServletResponse response) {
        StringBuilder uploadPathBuilder = new StringBuilder(128);
        uploadPathBuilder.append(rootBaseDir).append("/");
        if (Boolean.TRUE.equals(asImage)) {
            uploadPathBuilder.append(ApplicationConstant.UPLOAD_IMAGE_PARENT_PATH);
        } else {
            uploadPathBuilder.append(ApplicationConstant.UPLOAD_ATTACHMENT_PARENT_PATH);
        }
        uploadPathBuilder.append("/").append(modelName).append("/").append(fieldName).append("/").append(fileName);
        File file = new File(uploadPathBuilder.toString());
        if (!file.exists()) {
            log.warn("Download file [" + uploadPathBuilder.toString() + "] failed, no file found!");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[2048];
        try (OutputStream os = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            log.error("Failed to call UpDownloadUtil.doDownload", e);
        }
    }

    /**
     * 执行文件上传操作，并将与该文件下载对应的Url直接写入到HttpServletResponse应答对象，返回给前端。
     *
     * @param rootBaseDir        存放上传文件的根目录。
     * @param serviceContextPath 微服务的上下文路径，如: /admin/upms。
     * @param modelName          所在数据表的实体对象名。
     * @param fieldName          关联字段的实体对象属性名。
     * @param uploadFile         Http请求中上传的文件对象。
     * @param asImage            是否为图片对象。图片是无需权限验证的，因此和附件存放在不同的子目录。
     * @param response           Http 应答对象。
     * @throws IOException 文件操作错误。
     */
    public static void doUpload(
            String rootBaseDir,
            String serviceContextPath,
            String modelName,
            String fieldName,
            Boolean asImage,
            MultipartFile uploadFile,
            HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json; charset=utf-8");
        if (Objects.isNull(uploadFile) || uploadFile.isEmpty() || MyCommonUtil.isBlankOrNull(fieldName)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(JSON.toJSONString(ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FILE_ARGUMENT)));
            return;
        }
        StringBuilder uploadPathBuilder = new StringBuilder(128);
        uploadPathBuilder.append(rootBaseDir).append("/");
        if (Boolean.TRUE.equals(asImage)) {
            uploadPathBuilder.append(ApplicationConstant.UPLOAD_IMAGE_PARENT_PATH);
        } else {
            uploadPathBuilder.append(ApplicationConstant.UPLOAD_ATTACHMENT_PARENT_PATH);
        }
        uploadPathBuilder.append("/").append(modelName).append("/").append(fieldName).append("/");
        // 根据请求上传的uri构建下载uri，只是将末尾的/upload改为/download即可。
        HttpServletRequest request = ContextUtil.getHttpRequest();
        String uri = request.getRequestURI();
        uri = StringUtils.removeEnd(uri, "/");
        uri = StringUtils.removeEnd(uri, "/upload");
        String downloadUri = serviceContextPath + uri + "/download";
        StringBuilder filenameBuilder = new StringBuilder(64);
        filenameBuilder.append(MyCommonUtil.generateUuid())
                .append(".").append(FilenameUtils.getExtension(uploadFile.getOriginalFilename()));
        UploadFileInfo fileInfo = new UploadFileInfo();
        fileInfo.downloadUri = downloadUri;
        fileInfo.filename = filenameBuilder.toString();
        try {
            byte[] bytes = uploadFile.getBytes();
            Path path = Paths.get(uploadPathBuilder.toString() + filenameBuilder.toString());
            //如果没有files文件夹，则创建
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(uploadPathBuilder.toString()));
            }
            //文件写入指定路径
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("Failed to write uploaded file [" + uploadFile.getOriginalFilename() + " ].", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(JSON.toJSONString(ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FILE_IOERROR)));
            return;
        }
        out.print(JSON.toJSONString(ResponseResult.success(fileInfo)));
        out.flush();
        out.close();
    }

    /**
     * 判断filename参数指定的文件名，是否被包含在fileInfoJson参数中。
     *
     * @param fileInfoJson 内部类UploadFileInfo的JSONArray数组。
     * @param filename     被包含的文件名。
     * @return 存在返回true，否则false。
     */
    public static boolean containFile(String fileInfoJson, String filename) {
        if (StringUtils.isAnyBlank(fileInfoJson, filename)) {
            return false;
        }
        List<UploadFileInfo> fileInfoList = JSON.parseArray(fileInfoJson, UploadFileInfo.class);
        if (CollectionUtils.isNotEmpty(fileInfoList)) {
            for (UploadFileInfo fileInfo : fileInfoList) {
                if (StringUtils.equals(filename, fileInfo.filename)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private UpDownloadUtil() {
    }

    @Data
    static class UploadFileInfo {
        private String downloadUri;
        private String filename;
    }
}
