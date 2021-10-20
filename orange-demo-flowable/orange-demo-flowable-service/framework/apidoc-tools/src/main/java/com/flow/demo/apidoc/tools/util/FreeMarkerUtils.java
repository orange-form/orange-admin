package com.flow.demo.apidoc.tools.util;

import java.util.UUID;

/**
 * 仅供Freemarker模板内部使用的Java工具函数。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class FreeMarkerUtils {

    /**
     * 生成GUID。
     *
     * @return 生成后的GUID。
     */
    public static String generateGuid() {
        return  UUID.randomUUID().toString();
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    public FreeMarkerUtils() {
        // FreeMarker的工具对象，Sonarqube建议给出空构造的注释。
    }
}
