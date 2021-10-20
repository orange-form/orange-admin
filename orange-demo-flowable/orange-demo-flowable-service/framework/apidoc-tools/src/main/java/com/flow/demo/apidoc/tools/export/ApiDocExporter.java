package com.flow.demo.apidoc.tools.export;

import com.flow.demo.apidoc.tools.codeparser.ApiCodeParser;
import com.flow.demo.apidoc.tools.util.FreeMarkerUtils;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 根据代码解析后的工程对象数据，导出到Markdown格式的接口文档文件。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class ApiDocExporter {

    private final Configuration config;

    public ApiDocExporter() throws TemplateModelException {
        config = new Configuration(Configuration.VERSION_2_3_28);
        config.setNumberFormat("0.####");
        config.setClassicCompatible(true);
        config.setAPIBuiltinEnabled(true);
        config.setClassForTemplateLoading(ApiPostmanExporter.class, "/templates/");
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setSharedVariable("freemarkerUtils", new FreeMarkerUtils());
        config.unsetCacheStorage();
        config.clearTemplateCache();
    }

    /**
     * 生成Markdown格式的API接口文档。
     *
     * @param apiProject 解析后的工程对象。
     * @param outputFile 生成后的、包含全路径的输出文件名。
     * @throws IOException       文件操作异常。
     * @throws TemplateException 模板实例化异常。
     */
    public void doGenerate(ApiCodeParser.ApiProject apiProject, String outputFile) throws IOException, TemplateException {
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("project", apiProject);
        List<ApiCodeParser.ApiService> newServiceList = new LinkedList<>();
        if (apiProject.getMicroService()) {
            // 在微服务场景中，我们需要把upms服务放到最前面显示。
            for (ApiCodeParser.ApiService apiService : apiProject.getServiceList()) {
                if ("upms".equals(apiService.getServiceName())) {
                    newServiceList.add(apiService);
                    break;
                }
            }
            for (ApiCodeParser.ApiService apiService : apiProject.getServiceList()) {
                if (!"upms".equals(apiService.getServiceName())) {
                    newServiceList.add(apiService);
                }
            }
        } else {
            ApiCodeParser.ApiService appService = apiProject.getServiceList().get(0);
            ApiCodeParser.ApiService newUpmsService = new ApiCodeParser.ApiService();
            newUpmsService.setDefaultGroupClassSet(appService.getGroupedClassMap().get("upms"));
            newUpmsService.setServiceName("upms");
            newUpmsService.setShowName("用户权限模块");
            newServiceList.add(newUpmsService);
            ApiCodeParser.ApiService newAppService = new ApiCodeParser.ApiService();
            newAppService.setDefaultGroupClassSet(appService.getGroupedClassMap().get("app"));
            newAppService.setServiceName("app");
            newAppService.setShowName("业务应用模块");
            newServiceList.add(newAppService);
        }
        apiProject.setServiceList(newServiceList);
        FileUtils.forceMkdirParent(new File(outputFile));
        config.getTemplate("./api-doc.md.ftl").process(paramMap, new FileWriter(outputFile));
    }
}
