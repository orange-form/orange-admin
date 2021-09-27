package com.flow.demo.common.core.upload;

import lombok.Data;

/**
 * 数据上传操作的应答信息对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class UploadResponseInfo {
    /**
     * 上传是否出现错误。
     */
    private Boolean uploadFailed = false;
    /**
     * 具体错误信息。
     */
    private String errorMessage;
    /**
     * 返回前端的下载url。
     */
    private String downloadUri;
    /**
     * 返回给前端的文件名。
     */
    private String filename;
}
