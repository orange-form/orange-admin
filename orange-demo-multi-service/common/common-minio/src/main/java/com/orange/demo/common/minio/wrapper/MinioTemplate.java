package com.orange.demo.common.minio.wrapper;

import com.orange.demo.common.minio.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;

/**
 * 封装的minio客户端模板类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class MinioTemplate {

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    private final MinioProperties properties;
    private final MinioClient client;

    public MinioTemplate(MinioProperties properties, MinioClient client) {
        super();
        this.properties = properties;
        this.client = client;
    }

    /**
     * 判断bucket是否存在。
     *
     * @param bucketName 桶名称。
     * @return 存在返回true，否则false。
     */
    public boolean bucketExists(String bucketName) {
        try {
            return client.bucketExists(bucketName);
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }

    /**
     * 创建桶。
     *
     * @param bucketName 桶名称。
     */
    public void makeBucket(String bucketName) throws Exception {
        if (!client.bucketExists(bucketName)) {
            client.makeBucket(bucketName);
        }
    }

    /**
     * 存放对象。
     *
     * @param bucketName 桶名称。
     * @param objectName 对象名称。
     * @param filename   本地上传的文件名称。
     */
    public void putObject(String bucketName, String objectName, String filename) throws Exception {
        client.putObject(bucketName, objectName, filename, new PutObjectOptions(-1, -1));
    }

    /**
     * 存放对象。桶名称为配置中的桶名称。
     *
     * @param objectName 对象名称。
     * @param filename   本地上传的文件名称。
     */
    public void putObject(String objectName, String filename) throws Exception {
        this.putObject(properties.getBucketName(), objectName, filename);
    }

    /**
     * 读取输入流并存放。
     *
     * @param bucketName 桶名称。
     * @param objectName 对象名称。
     * @param stream     读取后上传的文件流。
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
        client.putObject(bucketName, objectName, stream, new PutObjectOptions(stream.available(), -1));
    }

    /**
     * 读取输入流并存放。
     *
     * @param objectName 对象名称。
     * @param stream     读取后上传的文件流。
     */
    public void putObject(String objectName, InputStream stream) throws Exception {
        this.putObject(properties.getBucketName(), objectName, stream);
    }

    /**
     * 移除对象。
     *
     * @param bucketName 桶名称。
     * @param objectName 对象名称。
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        client.removeObject(bucketName, objectName);
    }

    /**
     * 移除对象。桶名称为配置中的桶名称。
     *
     * @param objectName 对象名称。
     */
    public void removeObject(String objectName) throws Exception {
        this.removeObject(properties.getBucketName(), objectName);
    }

    /**
     * 获取文件输入流。
     *
     * @param bucket     桶名称。
     * @param objectName 对象名称。
     * @return 文件的输入流。
     */
    public InputStream getStream(String bucket, String objectName) throws Exception {
        return client.getObject(bucket, objectName);
    }

    /**
     * 获取文件输入流。
     *
     * @param objectName 对象名称。
     * @return 文件的输入流。
     */
    public InputStream getStream(String objectName) throws Exception {
        return this.getStream(properties.getBucketName(), objectName);
    }

    /**
     * 获取存储的文件对象。
     *
     * @param bucket     桶名称。
     * @param objectName 对象名称。
     * @return 读取后存储到文件的文件对象。
     */
    public File getFile(String bucket, String objectName) throws Exception {
        InputStream in = getStream(bucket, objectName);
        File dir = new File(TMP_DIR);
        if (!dir.exists() || dir.isFile()) {
            dir.mkdirs();
        }
        File file = new File(TMP_DIR + objectName);
        FileUtils.copyInputStreamToFile(in, file);
        return file;
    }

    /**
     * 获取存储的文件对象。桶名称为配置中的桶名称。
     *
     * @param objectName 对象名称。
     * @return 读取后存储到文件的文件对象。
     */
    public File getFile(String objectName) throws Exception {
        return this.getFile(properties.getBucketName(), objectName);
    }

    /**
     * 获取指定文件的URL。
     *
     * @param bucketName 桶名称。
     * @param objectName 对象名称。
     * @return 指定文件的URL。
     */
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        return client.getObjectUrl(bucketName, objectName);
    }

    /**
     * 获取指定文件的URL。桶名称为配置中的桶名称。
     *
     * @param objectName 对象名称。
     * @return 指定文件的URL。
     */
    public String getObjectUrl(String objectName) throws Exception {
        return this.getObjectUrl(properties.getBucketName(), objectName);
    }

    /**
     * 获取对象状态信息。
     *
     * @param bucketName 桶名称。
     * @param objectName 对象名称。
     * @return 对象状态和meta信息。
     */
    public ObjectStat statObject(String bucketName, String objectName) throws Exception {
        return client.statObject(bucketName, objectName, null);
    }

    /**
     * 获取对象状态信息。桶名称为配置中的桶名称。
     *
     * @param objectName 对象名称。
     * @return 对象状态和meta信息。
     */
    public ObjectStat statObject(String objectName) throws Exception {
        return client.statObject(properties.getBucketName(), objectName, null);
    }
}
