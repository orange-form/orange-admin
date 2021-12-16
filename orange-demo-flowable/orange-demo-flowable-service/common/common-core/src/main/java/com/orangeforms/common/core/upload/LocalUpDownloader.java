package com.orangeforms.common.core.upload;

import com.alibaba.fastjson.JSON;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * 存储本地文件的上传下载实现类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Component
public class LocalUpDownloader extends BaseUpDownloader {

    @Autowired
    private UpDownloaderFactory factory;

    @PostConstruct
    public void doRegister() {
        factory.registerUpDownloader(UploadStoreTypeEnum.LOCAL_SYSTEM, this);
    }

    /**
     * 执行下载操作，从本地文件系统读取数据，并将读取的数据直接写入到HttpServletResponse应答对象。
     *
     * @param rootBaseDir 文件下载的根目录。
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
            HttpServletResponse response) {
        String uploadPath = makeFullPath(rootBaseDir, modelName, fieldName, asImage);
        String fullFileanme = uploadPath + "/" + fileName;
        File file = new File(fullFileanme);
        if (!file.exists()) {
            log.warn("Download file [" + fullFileanme + "] failed, no file found!");
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
                os.write(buff, 0, i);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            log.error("Failed to call LocalUpDownloader.doDownload", e);
        }
    }

    /**
     * 执行文件上传操作，并存入本地文件系统，再将与该文件下载对应的Url直接写入到HttpServletResponse应答对象，返回给前端。
     *
     * @param serviceContextPath 微服务的上下文路径，如: /admin/upms。
     * @param rootBaseDir        存放上传文件的根目录。
     * @param modelName          所在数据表的实体对象名。
     * @param fieldName          关联字段的实体对象属性名。
     * @param uploadFile         Http请求中上传的文件对象。
     * @param asImage            是否为图片对象。图片是无需权限验证的，因此和附件存放在不同的子目录。
     * @return 存储在本地上传文件名。
     * @throws IOException 文件操作错误。
     */
    @Override
    public UploadResponseInfo doUpload(
            String serviceContextPath,
            String rootBaseDir,
            String modelName,
            String fieldName,
            Boolean asImage,
            MultipartFile uploadFile) throws IOException {
        UploadResponseInfo responseInfo = new UploadResponseInfo();
        if (Objects.isNull(uploadFile) || uploadFile.isEmpty()) {
            responseInfo.setUploadFailed(true);
            responseInfo.setErrorMessage(ErrorCodeEnum.INVALID_UPLOAD_FILE_ARGUMENT.getErrorMessage());
            return responseInfo;
        }
        String uploadPath = makeFullPath(rootBaseDir, modelName, fieldName, asImage);
        fillUploadResponseInfo(responseInfo, serviceContextPath, uploadFile.getOriginalFilename());
        try {
            byte[] bytes = uploadFile.getBytes();
            Path path = Paths.get(uploadPath + responseInfo.getFilename());
            // 如果没有files文件夹，则创建
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(uploadPath));
            }
            // 文件写入指定路径
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("Failed to write uploaded file [" + uploadFile.getOriginalFilename() + " ].", e);
            responseInfo.setUploadFailed(true);
            responseInfo.setErrorMessage(ErrorCodeEnum.INVALID_UPLOAD_FILE_IOERROR.getErrorMessage());
            return responseInfo;
        }
        return responseInfo;
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
        List<UploadResponseInfo> fileInfoList = JSON.parseArray(fileInfoJson, UploadResponseInfo.class);
        if (CollectionUtils.isNotEmpty(fileInfoList)) {
            for (UploadResponseInfo fileInfo : fileInfoList) {
                if (StringUtils.equals(filename, fileInfo.getFilename())) {
                    return true;
                }
            }
        }
        return false;
    }
}
