package com.orangeforms.apidoc.tools.codeparser;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 解析项目中接口信息的配置对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class ApiCodeConfig {

    /**
     * 项目名称。
     */
    private String projectName;
    /**
     * 项目的基础包名，如(com.demo.multi)。
     */
    private String basePackage;
    /**
     * 项目在本地文件系统中的根目录。这里需要注意的是，Windows用户请务必使用反斜杠作为目录分隔符。
     * 如："e:/mypath/OrangeSingleDemo"，"/Users/xxx/OrangeSingleDemo"。
     */
    private String projectRootPath;
    /**
     * 是否为微服务项目。
     */
    private Boolean microService;
    /**
     * 服务配置列表。对于单体服务，至少也会有一个ServiceConfig对象。
     */
    private List<ServiceConfig> serviceList;

    @Data
    public static class ServiceConfig {
        /**
         * 服务名称。
         */
        private String serviceName;
        /**
         * 服务中文显示名称。
         */
        private String showName;
        /**
         * 服务所在目录，相对于工程目录的子目录。
         */
        private String servicePath;
        /**
         * 仅用于微服务工程。通常为服务路由路径，如：/admin/coursepaper。服务内的接口，都会加上该路径前缀。
         */
        private String serviceRequestPath;
        /**
         * 服务的端口号。
         */
        private String port;
        /**
         * Api Controller信息列表。
         */
        private List<ControllerInfo> controllerInfoList;
    }

    @Data
    public static class ControllerInfo {
        /**
         * Controller.java等接口文件的所在目录。该目录仅为相对于服务代码目录的子目录。
         * 目录分隔符请务必使用反斜杠。如："/com/orange/demo/app/controller"。
         */
        private String path;
        /**
         * 如果一个服务内，存在多个Controller目录，将再次生成二级子目录，目录名为groupName。(可使用中文)
         */
        private String groupName;
        /**
         * 在当前Controller目录下，需要忽略的Controller列表 (只写类名即可)。如：LoginController。
         */
        private Set<String> skipControllers;
    }
}
