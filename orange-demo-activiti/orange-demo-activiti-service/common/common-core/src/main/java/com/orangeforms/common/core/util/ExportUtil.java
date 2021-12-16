package com.orangeforms.common.core.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.jimmyshi.beanquery.BeanQuery;
import com.orangeforms.common.core.constant.ApplicationConstant;
import com.orangeforms.common.core.exception.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 导出工具类，目前支持xlsx和csv两种类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class ExportUtil {

    /**
     * 数据导出。目前仅支持xlsx和csv。
     *
     * @param dataList       导出数据列表。
     * @param selectFieldMap 导出的数据字段，key为对象字段名称，value为中文标题名称。
     * @param filename       导出文件名。
     * @param <T>            数据对象类型。
     * @throws IOException 文件操作失败。
     */
    public static <T> void doExport(
            Collection<T> dataList, Map<String, String> selectFieldMap, String filename) throws IOException {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        StringBuilder sb = new StringBuilder(128);
        for (Map.Entry<String, String> e : selectFieldMap.entrySet()) {
            sb.append(e.getKey()).append(" as ").append(e.getValue()).append(", ");
        }
        // 去掉末尾的逗号
        String selectFieldString = sb.substring(0, sb.length() - 2);
        // 写出数据到xcel格式的输出流
        List<Map<String, Object>> resultList = BeanQuery.select(selectFieldString).executeFrom(dataList);
        // 构建HTTP输出流参数
        HttpServletResponse response = ContextUtil.getHttpResponse();
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        if (ApplicationConstant.XLSX_EXT.equals(FilenameUtils.getExtension(filename))) {
            ServletOutputStream out = response.getOutputStream();
            ExcelWriter writer = ExcelUtil.getWriter(true);
            writer.setRowHeight(-1, 30);
            writer.setColumnWidth(-1, 30);
            writer.setColumnWidth(1, 20);
            writer.write(resultList);
            writer.flush(out);
            writer.close();
            IoUtil.close(out);
        } else if (ApplicationConstant.CSV_EXT.equals(FilenameUtils.getExtension(filename))) {
            Collection<String> headerList = selectFieldMap.values();
            String[] headerArray = new String[headerList.size()];
            headerList.toArray(headerArray);
            CSVFormat format = CSVFormat.DEFAULT.withHeader(headerArray);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            try (Writer out = response.getWriter(); CSVPrinter printer = new CSVPrinter(out, format)) {
                for (Map<String, Object> o : resultList) {
                    for (Map.Entry<String, Object> entry : o.entrySet()) {
                        printer.print(entry.getValue());
                    }
                    printer.println();
                }
                printer.flush();
            } catch (Exception e) {
                log.error("Failed to call ExportUtil.doExport", e);
            }
        } else {
            throw new MyRuntimeException("不支持的导出文件类型！");
        }
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private ExportUtil() {
    }
}
