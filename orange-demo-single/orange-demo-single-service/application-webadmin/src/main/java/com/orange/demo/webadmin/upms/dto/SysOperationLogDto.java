package com.orange.demo.webadmin.upms.dto;

import lombok.Data;

/**
 * 操作日志记录表
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class SysOperationLogDto {

    /**
     * 主键Id。
     */
    private Long logId;

    /**
     * 操作类型。
     * 常量值定义可参考SysOperationLogType对象。
     */
    private Integer operationType;

    /**
     * 每次请求的Id。
     * 对于微服务之间的调用，在同一个请求的调用链中，该值是相同的。
     */
    private String traceId;

    /**
     * HTTP 请求地址。
     */
    private String requestUrl;

    /**
     * 应答状态。
     */
    private Boolean success;

    /**
     * 操作员名称。
     */
    private String operatorName;

    /**
     * 调用时长最小值。
     */
    private Long elapseMin;

    /**
     * 调用时长最大值。
     */
    private Long elapseMax;

    /**
     * 操作开始时间。
     */
    private String operationTimeStart;

    /**
     * 操作开始时间。
     */
    private String operationTimeEnd;
}
