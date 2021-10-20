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
import java.util.Map;

/**
 * 根据代码解析后的工程对象数据，导出到Postman支持的JSON格式的文件。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class ApiPostmanExporter {

    private final Configuration config;

    public ApiPostmanExporter() throws TemplateModelException {
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
     * 生成Postman支持的JSON文档。
     * @param apiProject 解析后的工程对象。
     * @param outputFile 生成后的、包含全路径的输出文件名。
     * @throws IOException 文件操作异常。
     * @throws TemplateException 模板实例化异常。
     */
    public void doGenerate(ApiCodeParser.ApiProject apiProject, String outputFile) throws IOException, TemplateException {
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("project", apiProject);
        FileUtils.forceMkdirParent(new File(outputFile));
        config.getTemplate("./postman_collection.json.ftl").process(paramMap, new FileWriter(outputFile));
    }
}
