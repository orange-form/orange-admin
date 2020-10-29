package com.orange.demo.common.core.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import com.orange.demo.common.core.constant.ApplicationConstant;
import com.orange.demo.common.core.exception.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 导入工具类，目前支持xlsx和csv两种类型。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
public class ImportUtil {

    private static final String IMPORT_EXCEPTION_ERROR = "Failed to call ImportUtil.doImport";
    private static final String UNSUPPORT_FILE_EXT_ERROR = "不支持的导入文件类型！";
    /**
     * 同步导入方式。
     *
     * @param filename 导入文件名。
     * @return 导入数据列表。
     */
    public static List<Map<String, Object>> doImport(String filename) {
        if (ApplicationConstant.XLSX_EXT.equals(FilenameUtils.getExtension(filename))) {
            try (ExcelReader reader = ExcelUtil.getReader(filename)) {
                return reader.readAll();
            }
        } else if (ApplicationConstant.CSV_EXT.equals(FilenameUtils.getExtension(filename))) {
            List<Map<String, Object>> resultList = new LinkedList<>();
            try (FileReader reader = new FileReader(filename)) {
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
                Map<String, Integer> headerMap = parser.getHeaderMap();
                for (CSVRecord record : parser) {
                    Map<String, Object> rowMap = new LinkedHashMap<>();
                    for (final Map.Entry<String, Integer> header : headerMap.entrySet()) {
                        int col = header.getValue();
                        if (col < record.size()) {
                            rowMap.put(header.getKey(), record.get(col));
                        }
                    }
                    resultList.add(rowMap);
                }
            } catch (Exception e) {
                log.error(IMPORT_EXCEPTION_ERROR, e);
            }
            return resultList;
        }
        throw new MyRuntimeException(UNSUPPORT_FILE_EXT_ERROR);
    }

    /**
     * 异步导入方式，即SAX导入方式。
     *
     * @param filename 导入文件名。
     * @param importer 异步导入处理接口。
     * @throws IOException 文件处理异常。
     */
    public static <T> void doImport(String filename, BaseImporter<T> importer) throws IOException {
        if (ApplicationConstant.XLSX_EXT.equals(FilenameUtils.getExtension(filename))) {
            Excel07SaxReader reader = new MyExcel07SaxReader<>(importer);
            try (InputStream in = new FileInputStream(filename)) {
                reader.read(in, 0);
            }
        } else if (ApplicationConstant.CSV_EXT.equals(FilenameUtils.getExtension(filename))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                int rowIndex = 0;
                do {
                    String rowData = reader.readLine();
                    if (StringUtils.isBlank(rowData)) {
                        importer.doImport(-1, null);
                        break;
                    }
                    String[] dataArray = StringUtils.split(rowData, ",");
                    importer.doImport(rowIndex++, Arrays.asList(dataArray));
                } while (!importer.doInterrupt());
            } catch (Exception e) {
                log.error(IMPORT_EXCEPTION_ERROR, e);
            }
        }
        throw new MyRuntimeException(UNSUPPORT_FILE_EXT_ERROR);
    }

    /**
     * 异步导入抽象类。
     *
     * @param <T> 导入数据对象类型。
     */
    public abstract static class BaseImporter<T> {
        private Class<T> beanType;
        private List<T> batchRowList = new LinkedList<>();
        private int batchSize;
        private Field[] fieldArray = null;
        private Map<String, String> headerColumnMap;

        public BaseImporter(int batchSize, Class<T> beanType, Map<String, String> headerColumnMap) {
            if (batchSize <= 0) {
                batchSize = 100;
            }
            this.batchSize = batchSize;
            this.beanType = beanType;
            this.headerColumnMap = headerColumnMap;
        }

        /**
         * 导入操作执行函数。
         *
         * @param rowIndex 当前行号。
         * @param row      当前行数据列表对象。
         */
        public void doImport(long rowIndex, List<Object> row) {
            if (row == null) {
                doProcess(batchRowList);
                doFinish();
                batchRowList.clear();
                return;
            }
            if (rowIndex <= 0) {
                fieldArray = new Field[row.size()];
                List<String> headerList = row.stream().map(Object::toString).collect(Collectors.toList());
                List<String> columnList = new ArrayList<>(row.size());
                for (String headerName : headerList) {
                    String columnName = headerColumnMap.get(headerName);
                    if (columnName != null) {
                        columnList.add(columnName);
                    }
                }
                columnList.stream()
                        .map(columnName -> ReflectUtil.getField(beanType, columnName))
                        .collect(Collectors.toList())
                        .toArray(fieldArray);
                return;
            }
            T data;
            try {
                data = beanType.newInstance();
                for (int i = 0; i < row.size(); i++) {
                    Object value = row.get(i);
                    Field field = fieldArray[i];
                    if (field != null) {
                        ReflectUtil.setFieldValue(data, field, value);
                    }
                }
                batchRowList.add(data);
            } catch (Exception e) {
                log.error(IMPORT_EXCEPTION_ERROR, e);
            }
            if (rowIndex % batchSize == 0) {
                doProcess(batchRowList);
                batchRowList.clear();
            }
        }

        /**
         * 数据处理进行中回调模板函数。
         *
         * @param batchRowList 一批数据行。
         */
        public abstract void doProcess(List<T> batchRowList);

        /**
         * 数据处理完毕回调模板函数。
         */
        public abstract void doFinish();

        /**
         * 数据处理终端标记模板函数。
         * @return 是否中断。true则中断后面的处理。
         */
        public abstract boolean doInterrupt();
    }

    static class MyExcel07SaxReader<T> extends Excel07SaxReader {
        private BaseImporter<T> importer;

        MyExcel07SaxReader(BaseImporter<T> importer) {
            super((sheetIndex, rowIndex, rowList) -> importer.doImport(rowIndex, rowList));
            this.importer = importer;
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            super.endElement(uri, localName, qName);
            if (importer.doInterrupt()) {
                throw new MyRuntimeException();
            }
        }

        @Override
        public void endDocument() {
            importer.doImport(-1, null);
        }
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private ImportUtil() {
    }
}
