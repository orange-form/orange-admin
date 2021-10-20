package com.flow.demo.common.core.annotation;

import com.flow.demo.common.core.upload.UploadStoreTypeEnum;

import java.lang.annotation.*;

/**
 * 用于标记支持数据上传和下载的字段。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UploadFlagColumn {

    /**
     * 上传数据存储类型。
     *
     * @return 上传数据存储类型。
     */
    UploadStoreTypeEnum storeType();
}
