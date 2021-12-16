package com.orangeforms.apidoc.tools;

import com.alibaba.fastjson.JSON;
import com.orangeforms.apidoc.tools.codeparser.ApiCodeConfig;
import com.orangeforms.apidoc.tools.codeparser.ApiCodeParser;
import com.orangeforms.apidoc.tools.export.ApiDocExporter;
import freemarker.template.TemplateException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ExportDocApp {

    public static void main(String[] args) throws IOException, TemplateException {
        // 在第一次导出时，需要打开export-api-config.json配置文件，
        // 修改其中的工程根目录配置项(projectRootPath)，其他配置保持不变即可。
        InputStream in = ExportDocApp.class.getResourceAsStream("/export-api-config.json");
        String jsonData = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        ApiCodeConfig apiCodeConfig = JSON.parseObject(jsonData, ApiCodeConfig.class);
        ApiCodeParser apiCodeParser = new ApiCodeParser(apiCodeConfig);
        ApiCodeParser.ApiProject project = apiCodeParser.doParse();
        ApiDocExporter exporter = new ApiDocExporter();
        // 将下面的目录改为实际输出目录。
        exporter.doGenerate(project, "/xxx/Desktop/2.md");
    }
}
