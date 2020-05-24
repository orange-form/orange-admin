package com.orange.admin.common.core.constant;

/**
 * 应用程序的常量声明对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public final class ApplicationConstant {

    /**
     * 图片文件上传的父目录。
     */
    public static final String UPLOAD_IMAGE_PARENT_PATH = "image";
    /**
     * 附件文件上传的父目录。
     */
    public static final String UPLOAD_ATTACHMENT_PARENT_PATH = "attachment";
    /**
     * CSV文件扩展名。
     */
    public static final String CSV_EXT = "csv";
    /**
     * XLSX文件扩展名。
     */
    public static final String XLSX_EXT = "xlsx";
    /**
     * 统计分类计算时，按天聚合计算的常量值。(前端在MyOrderParam和MyGroupParam中传给后台)
     */
    public static final String DAY_AGGREGATION = "day";
    /**
     * 统计分类计算时，按月聚合计算的常量值。(前端在MyOrderParam和MyGroupParam中传给后台)
     */
    public static final String MONTH_AGGREGATION = "month";
    /**
     * 统计分类计算时，按年聚合计算的常量值。(前端在MyOrderParam和MyGroupParam中传给后台)
     */
    public static final String YEAR_AGGREGATION = "year";
    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private ApplicationConstant() {
    }
}
