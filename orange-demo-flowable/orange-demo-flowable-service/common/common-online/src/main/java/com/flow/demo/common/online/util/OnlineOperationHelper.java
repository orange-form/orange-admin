package com.flow.demo.common.online.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flow.demo.common.core.constant.ErrorCodeEnum;
import com.flow.demo.common.core.object.ResponseResult;
import com.flow.demo.common.core.upload.BaseUpDownloader;
import com.flow.demo.common.core.upload.UpDownloaderFactory;
import com.flow.demo.common.core.upload.UploadResponseInfo;
import com.flow.demo.common.core.upload.UploadStoreTypeEnum;
import com.flow.demo.common.online.config.OnlineProperties;
import com.flow.demo.common.online.model.OnlineColumn;
import com.flow.demo.common.online.model.OnlineDatasource;
import com.flow.demo.common.online.model.OnlineDatasourceRelation;
import com.flow.demo.common.online.model.OnlineTable;
import com.flow.demo.common.online.model.constant.FieldKind;
import com.flow.demo.common.online.model.constant.RelationType;
import com.flow.demo.common.online.object.ColumnData;
import com.flow.demo.common.online.service.OnlineDatasourceRelationService;
import com.flow.demo.common.online.service.OnlineDatasourceService;
import com.flow.demo.common.online.service.OnlineOperationService;
import com.flow.demo.common.online.service.OnlineTableService;
import com.flow.demo.common.redis.cache.SessionCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线表单操作的通用帮助对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Component
public class OnlineOperationHelper {

    @Autowired
    private OnlineDatasourceService onlineDatasourceService;
    @Autowired
    private OnlineDatasourceRelationService onlineDatasourceRelationService;
    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlineOperationService onlineOperationService;
    @Autowired
    private OnlineProperties onlineProperties;
    @Autowired
    private UpDownloaderFactory upDownloaderFactory;
    @Autowired
    private SessionCacheHelper cacheHelper;

    /**
     * 验证并获取数据源数据。
     *
     * @param datasourceId 数据源Id。
     * @return 数据源详情数据。
     */
    public ResponseResult<OnlineDatasource> verifyAndGetDatasource(Long datasourceId) {
        String errorMessage;
        OnlineDatasource datasource = onlineDatasourceService.getById(datasourceId);
        if (datasource == null) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineTable masterTable = onlineTableService.getOnlineTableFromCache(datasource.getMasterTableId());
        if (masterTable == null) {
            errorMessage = "数据验证失败，数据源主表Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        datasource.setMasterTable(masterTable);
        return ResponseResult.success(datasource);
    }

    /**
     * 验证并获取数据源的关联数据。
     *
     * @param datasourceId 数据源Id。
     * @param relationId   数据源关联Id。
     * @return 数据源的关联详情数据。
     */
    public ResponseResult<OnlineDatasourceRelation> verifyAndGetRelation(Long datasourceId, Long relationId) {
        String errorMessage;
        OnlineDatasourceRelation relation = onlineDatasourceRelationService.getById(relationId);
        if (relation == null || !relation.getDatasourceId().equals(datasourceId)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineTable slaveTable = onlineTableService.getOnlineTableFromCache(relation.getSlaveTableId());
        if (slaveTable == null) {
            errorMessage = "数据验证失败，数据源关联 [" + relation.getRelationName() + " ] 引用的从表不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        relation.setSlaveTable(slaveTable);
        return ResponseResult.success(relation);
    }

    /**
     * 验证并获取数据源的一对多关联数据。
     *
     * @param datasourceId 数据源Id。
     * @param relationId   数据源一对多关联Id。
     * @return 数据源的一对多关联详情数据。
     */
    public ResponseResult<OnlineDatasourceRelation> verifyAndGetOneToManyRelation(Long datasourceId, Long relationId) {
        String errorMessage;
        OnlineDatasourceRelation relation = onlineDatasourceRelationService.getById(relationId);
        if (relation == null || !relation.getDatasourceId().equals(datasourceId)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (!relation.getRelationType().equals(RelationType.ONE_TO_MANY)) {
            errorMessage = "数据验证失败，数据源关联 [" + relation.getRelationName() + " ] 不是一对多关联，不能调用该接口！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineTable slaveTable = onlineTableService.getOnlineTableFromCache(relation.getSlaveTableId());
        if (slaveTable == null) {
            errorMessage = "数据验证失败，数据源关联 [" + relation.getRelationName() + " ] 引用的从表不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        relation.setSlaveTable(slaveTable);
        relation.setSlaveColumn(slaveTable.getColumnMap().get(relation.getSlaveColumnId()));
        return ResponseResult.success(relation);
    }

    /**
     * 验证并获取数据源的指定类型关联数据。
     *
     * @param datasourceId 数据源Id。
     * @param relationType 数据源关联类型。
     * @return 数据源指定关联类型的关联数据详情列表。
     */
    public ResponseResult<List<OnlineDatasourceRelation>> verifyAndGetRelationList(
            Long datasourceId, Integer relationType) {
        String errorMessage;
        Set<Long> datasourceIdSet = CollUtil.newHashSet(datasourceId);
        List<OnlineDatasourceRelation> relationList = onlineDatasourceRelationService
                .getOnlineDatasourceRelationListByDatasourceIds(datasourceIdSet, relationType);
        for (OnlineDatasourceRelation relation : relationList) {
            OnlineTable slaveTable = onlineTableService.getOnlineTableFromCache(relation.getSlaveTableId());
            if (slaveTable == null) {
                errorMessage = "数据验证失败，数据源关联 [" + relation.getRelationName() + "] 的从表Id不存在！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            relation.setSlaveTable(slaveTable);
        }
        return ResponseResult.success(relationList);
    }

    /**
     * 构建在线表的数据记录。
     *
     * @param table             在线数据表对象。
     * @param tableData         在线数据表数据。
     * @param forUpdate         是否为更新。
     * @param ignoreSetColumnId 忽略设置的字段Id。
     * @return 在线表的数据记录。
     */
    public ResponseResult<List<ColumnData>> buildTableData(
            OnlineTable table, JSONObject tableData, boolean forUpdate, Long ignoreSetColumnId) {
        List<ColumnData> columnDataList = new LinkedList<>();
        String errorMessage;
        for (OnlineColumn column : table.getColumnMap().values()) {
            // 判断一下是否为需要自动填入的字段，如果是，这里就都暂时给空值了，后续操作会自动填补。
            // 这里还能避免一次基于tableData的查询，能快几纳秒也是好的。
            if (this.isAutoSettingField(column) || ObjectUtil.equal(column.getColumnId(), ignoreSetColumnId)) {
                columnDataList.add(new ColumnData(column, null));
                continue;
            }
            Object value = tableData.get(column.getColumnName());
            // 对于主键数据的处理。
            if (column.getPrimaryKey()) {
                // 如果是更新则必须包含主键参数。
                if (forUpdate && value == null) {
                    errorMessage = "数据验证失败，数据表 ["
                            + table.getTableName() + "] 主键字段 [" + column.getColumnName() + "] 不能为空值！";
                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
                }
            } else {
                if (value == null && !column.getNullable()) {
                    errorMessage = "数据验证失败，数据表 ["
                            + table.getTableName() + "] 字段 [" + column.getColumnName() + "] 不能为空值！";
                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
                }
            }
            columnDataList.add(new ColumnData(column, value));
        }
        return ResponseResult.success(columnDataList);
    }

    /**
     * 构建多个一对多从表的数据列表。
     *
     * @param datasourceId 数据源Id。
     * @param slaveData    多个一对多从表数据的JSON对象。
     * @return 构建后的多个一对多从表数据列表。
     */
    public ResponseResult<Map<OnlineDatasourceRelation, List<List<ColumnData>>>> buildSlaveDataList(
            Long datasourceId, JSONObject slaveData) {
        Map<OnlineDatasourceRelation, List<List<ColumnData>>> relationDataMap = new HashMap<>(slaveData.size());
        for (String key : slaveData.keySet()) {
            Long relationId = Long.parseLong(key);
            ResponseResult<OnlineDatasourceRelation> relationResult = this.verifyAndGetRelation(datasourceId, relationId);
            if (!relationResult.isSuccess()) {
                return ResponseResult.errorFrom(relationResult);
            }
            OnlineDatasourceRelation relation = relationResult.getData();
            List<List<ColumnData>> relationDataList = new LinkedList<>();
            relationDataMap.put(relation, relationDataList);
            OnlineTable slaveTable = relation.getSlaveTable();
            JSONArray slaveObjectArray = slaveData.getJSONArray(key);
            for (int i = 0; i < slaveObjectArray.size(); i++) {
                JSONObject slaveObject = slaveObjectArray.getJSONObject(i);
                ResponseResult<List<ColumnData>> slaveColumnDataListResult =
                        this.buildTableData(slaveTable, slaveObject, false, relation.getSlaveColumnId());
                if (!slaveColumnDataListResult.isSuccess()) {
                    return ResponseResult.errorFrom(slaveColumnDataListResult);
                }
                relationDataList.add(slaveColumnDataListResult.getData());
            }
        }
        return ResponseResult.success(relationDataMap);
    }

    /**
     * 将字符型字段值转换为与参数字段类型匹配的字段值。
     *
     * @param column 在线表单字段。
     * @param dataId 字符型字段值。
     * @return 转换后与参数字段类型匹配的字段值。
     */
    public Object convertToTypeValue(OnlineColumn column, String dataId) {
        if ("Long".equals(column.getObjectFieldType())) {
            return Long.valueOf(dataId);
        } else if ("Integer".equals(column.getObjectFieldType())) {
            return Integer.valueOf(dataId);
        }
        return dataId;
    }

    /**
     * 将字符型字段值集合转换为与参数字段类型匹配的字段值集合。
     *
     * @param column    在线表单字段。
     * @param dataIdSet 字符型字段值集合。
     * @return 转换后与参数字段类型匹配的字段值集合。
     */
    public Set<?> convertToTypeValue(OnlineColumn column, Set<String> dataIdSet) {
        if ("Long".equals(column.getObjectFieldType())) {
            return dataIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        } else if ("Integer".equals(column.getObjectFieldType())) {
            return dataIdSet.stream().map(Integer::valueOf).collect(Collectors.toSet());
        }
        return dataIdSet;
    }

    /**
     * 下载数据。
     *
     * @param table     在线表对象。
     * @param dataId    在线表数据主键Id。
     * @param fieldName 数据表字段名。
     * @param filename  下载文件名。
     * @param asImage   是否为图片。
     * @param response  HTTP 应对对象。
     */
    public void doDownload(
            OnlineTable table, String dataId, String fieldName, String filename, Boolean asImage, HttpServletResponse response) {
        // 使用try来捕获异常，是为了保证一旦出现异常可以返回500的错误状态，便于调试。
        // 否则有可能给前端返回的是200的错误码。
        try {
            // 如果请求参数中没有包含主键Id，就判断该文件是否为当前session上传的。
            if (ObjectUtil.isEmpty(dataId)) {
                if (!cacheHelper.existSessionUploadFile(filename)) {
                    ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                Map<String, Object> dataMap =
                        onlineOperationService.getMasterData(table, null, null, dataId);
                if (dataMap == null) {
                    ResponseResult.output(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                String fieldJsonData = (String) dataMap.get(fieldName);
                if (fieldJsonData == null) {
                    ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                if (!BaseUpDownloader.containFile(fieldJsonData, filename)) {
                    ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            OnlineColumn downloadColumn = null;
            for (OnlineColumn column : table.getColumnMap().values()) {
                if (column.getColumnName().equals(fieldName)) {
                    if (asImage) {
                        if (ObjectUtil.notEqual(column.getFieldKind(), FieldKind.UPLOAD_IMAGE)) {
                            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                                    ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
                            return;
                        }
                    } else {
                        if (ObjectUtil.notEqual(column.getFieldKind(), FieldKind.UPLOAD)) {
                            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                                    ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
                            return;
                        }
                    }
                    downloadColumn = column;
                    break;
                }
            }
            if (downloadColumn == null) {
                ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                        ResponseResult.error(ErrorCodeEnum.INVALID_DATA_FIELD));
                return;
            }
            BaseUpDownloader upDownloader = upDownloaderFactory.get(UploadStoreTypeEnum.LOCAL_SYSTEM);
            upDownloader.doDownload(onlineProperties.getUploadFileBaseDir(),
                    table.getModelName(), fieldName, filename, asImage, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 上传数据。
     *
     * @param table      在线表对象。
     * @param fieldName  数据表字段名。
     * @param asImage    是否为图片。
     * @param uploadFile 上传的文件。
     */
    public void doUpload(OnlineTable table, String fieldName, Boolean asImage, MultipartFile uploadFile) throws Exception {
        OnlineColumn uploadColumn = null;
        for (OnlineColumn column : table.getColumnMap().values()) {
            if (column.getColumnName().equals(fieldName)) {
                if (asImage) {
                    if (ObjectUtil.notEqual(column.getFieldKind(), FieldKind.UPLOAD_IMAGE)) {
                        ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                                ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
                        return;
                    }
                } else {
                    if (ObjectUtil.notEqual(column.getFieldKind(), FieldKind.UPLOAD)) {
                        ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                                ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
                        return;
                    }
                }
                uploadColumn = column;
                break;
            }
        }
        if (uploadColumn == null) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.INVALID_DATA_FIELD));
            return;
        }
        BaseUpDownloader upDownloader = upDownloaderFactory.get(UploadStoreTypeEnum.LOCAL_SYSTEM);
        UploadResponseInfo responseInfo = upDownloader.doUpload(null,
                onlineProperties.getUploadFileBaseDir(), table.getModelName(), fieldName, asImage, uploadFile);
        if (responseInfo.getUploadFailed()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            return;
        }
        // 动态表单的下载url和普通表单有所不同，由前端负责动态拼接。
        responseInfo.setDownloadUri(null);
        cacheHelper.putSessionUploadFile(responseInfo.getFilename());
        ResponseResult.output(ResponseResult.success(responseInfo));
    }

    private boolean isAutoSettingField(OnlineColumn column) {
        return (ObjectUtil.equal(column.getFieldKind(), FieldKind.CREATE_TIME)
                || ObjectUtil.equal(column.getFieldKind(), FieldKind.CREATE_USER_ID)
                || ObjectUtil.equal(column.getFieldKind(), FieldKind.UPDATE_TIME)
                || ObjectUtil.equal(column.getFieldKind(), FieldKind.UPDATE_USER_ID)
                || ObjectUtil.equal(column.getFieldKind(), FieldKind.LOGIC_DELETE));
    }
}
