package com.flow.demo.apidoc.tools;

import com.alibaba.fastjson.JSON;
import com.flow.demo.apidoc.tools.codeparser.ApiCodeConfig;
import com.flow.demo.apidoc.tools.codeparser.ApiCodeParser;
import com.flow.demo.apidoc.tools.export.ApiPostmanExporter;
import freemarker.template.TemplateException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ExportApiApp {

    public static void main(String[] args) throws IOException, TemplateException {
        // 在第一次导出时，需要打开export-api-config.json配置文件，
        // 修改其中的工程根目录配置项(projectRootPath)，其他配置保持不变即可。
        InputStream in = ExportApiApp.class.getResourceAsStream("/export-api-config.json");
        String jsonData = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        ApiCodeConfig apiCodeConfig = JSON.parseObject(jsonData, ApiCodeConfig.class);
        ApiCodeParser apiCodeParser = new ApiCodeParser(apiCodeConfig);
        ApiCodeParser.ApiProject project = apiCodeParser.doParse();
        ApiPostmanExporter exporter = new ApiPostmanExporter();
        // 将下面的目录改为实际输出目录。
        exporter.doGenerate(project, "/xxx/Desktop/1.json");
    }
}
