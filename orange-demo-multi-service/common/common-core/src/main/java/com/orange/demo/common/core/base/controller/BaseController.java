package com.orange.demo.common.core.base.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.constant.AggregationKind;
import com.orange.demo.common.core.constant.AggregationType;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.exception.RemoteDataBuildException;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.util.MyModelUtil;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 控制器Controller的基类。
 *
 * @param <M> 主Model实体对象类型。
 * @param <D> 主DomainDto域对象类型。
 * @param <K> 主键类型。
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public abstract class BaseController<M, D, K> {

    /**
     * 当前Service关联的主Model实体对象的Class。
     */
    protected Class<M> modelClass;
    /**
     * 当前Service关联的主DomainDto域对象的Class。
     */
    protected Class<D> domainDtoClass;
    /**
     * 当前Service关联的主Model对象主键字段名称。
     */
    protected String idFieldName;

    /**
     * 获取子类中注入的BaseService类。
     *
     * @return 子类中注入的BaseService类。
     */
    protected abstract BaseService<M, D, K> service();

    /**
     * 构造函数。
     */
    @SuppressWarnings("unchecked")
    public BaseController() {
        modelClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        domainDtoClass = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field field : fields) {
            if (null != field.getAnnotation(Id.class)) {
                idFieldName = field.getName();
                break;
            }
        }
    }

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param filterIds   主键Id集合。
     * @param withDict    是否包含字典关联。
     * @param modelMapper 对象映射函数对象。如果为空，则使用MyModelUtil中的缺省转换函数。
     * @return 应答结果对象，包含主对象集合。
     * @throws RemoteDataBuildException buildRelationForDataList会抛出该异常。
     */
    public ResponseResult<List<D>> baseListByIds(
            Set<K> filterIds, Boolean withDict, BaseModelMapper<D, M> modelMapper) {
        if (MyCommonUtil.existBlankArgument(filterIds, withDict)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        List<M> resultList = service().getInList(idFieldName, filterIds);
        List<D> resultDtoList = null;
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseResult.success(resultDtoList);
        }
        if (Boolean.TRUE.equals(withDict)) {
            service().buildRelationForDataList(resultList, MyRelationParam.dictOnly(), null);
        }
        resultDtoList = convertToDomainList(resultList, modelMapper);
        return ResponseResult.success(resultDtoList);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param id          主键Id。
     * @param withDict    是否包含字典关联。
     * @param modelMapper 对象映射函数对象。如果为空，则使用MyModelUtil中的缺省转换函数。
     * @return 应答结果对象，包含主对象数据。
     * @throws RemoteDataBuildException buildRelationForData会抛出此异常。
     */
    public ResponseResult<D> baseGetById(K id, Boolean withDict, BaseModelMapper<D, M> modelMapper) {
        if (MyCommonUtil.existBlankArgument(id, withDict)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        M resultObject = service().getById(id);
        D resultDtoObject = null;
        if (resultObject == null) {
            return ResponseResult.success(resultDtoObject);
        }
        if (Boolean.TRUE.equals(withDict)) {
            service().buildRelationForData(resultObject, MyRelationParam.dictOnly(), null);
        }
        resultDtoObject = this.convertToDomain(resultObject, modelMapper);
        return ResponseResult.success(resultDtoObject);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param filterIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    public ResponseResult<Boolean> baseExistIds(Set<K> filterIds) {
        return ResponseResult.success(CollectionUtils.isNotEmpty(filterIds)
                && service().existUniqueKeyList(idFieldName, filterIds));
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param id 主键Id。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    public ResponseResult<Boolean> baseExistId(K id) {
        return ResponseResult.success(
                !MyCommonUtil.existBlankArgument(id) && service().getById(id) != null);
    }

    /**
     * 自定义过滤条件、显示字段和排序字段的单表查询。主要用于微服务间远程过程调用。
     * NOTE: 和baseListMapBy方法的差别只是返回的数据形式不同，该接口以对象列表的形式返回数据。
     *
     * @param queryParam  查询参数。
     * @param modelMapper 对象映射函数对象。如果为空，则使用MyModelUtil中的缺省转换函数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     * @throws RemoteDataBuildException buildRelationForDataList会抛出此异常。
     */
    public ResponseResult<List<D>> baseListBy(MyQueryParam queryParam, BaseModelMapper<D, M> modelMapper) {
        if (CollectionUtils.isNotEmpty(queryParam.getSelectFieldList())) {
            for (String fieldName : queryParam.getSelectFieldList()) {
                String columnName = MyModelUtil.mapToColumnName(fieldName, modelClass);
                if (columnName == null) {
                    String errorMessage = "数据验证失败，实体对象 ["
                            + modelClass.getSimpleName() + "] 中不存在字段 [" + fieldName + "]!";
                    return ResponseResult.error(ErrorCodeEnum.INVALID_DATA_FIELD, errorMessage);
                }
            }
        }
        String whereClause = MyWhereCriteria.makeCriteriaString(queryParam.getCriteriaList(), modelClass);
        String orderBy = MyOrderParam.buildOrderBy(queryParam.getOrderParam(), modelClass);
        MyPageParam pageParam = queryParam.getPageParam();
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<M> resultList = service().getListByCondition(queryParam.getSelectFieldList(), whereClause, orderBy);
        List<D> resultDtoList = null;
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseResult.success(resultDtoList);
        }
        if (queryParam.getWithDict()) {
            service().buildRelationForDataList(resultList, MyRelationParam.dictOnly(), null);
        }
        resultDtoList = convertToDomainList(resultList, modelMapper);
        return ResponseResult.success(resultDtoList);
    }

    /**
     * 自定义过滤条件、显示字段和排序字段的单表查询。主要用于微服务间远程过程调用。
     * NOTE: 和baseListBy方法的差别只是返回的数据形式不同，该接口以Map列表的形式返回数据。
     *
     * @param queryParam     查询参数。
     * @param modelMapper    对象映射函数对象。如果为空，则使用MyModelUtil中的缺省转换函数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    public ResponseResult<List<Map<String, Object>>> baseListMapBy(
            MyQueryParam queryParam, BaseModelMapper<D, M> modelMapper) {
        ResponseResult<List<D>> result = this.baseListBy(queryParam, modelMapper);
        if (!result.isSuccess()) {
            return ResponseResult.errorFrom(result);
        }
        List<Map<String, Object>> resultMapList =
                result.getData().stream().map(BeanUtil::beanToMap).collect(Collectors.toList());
        return ResponseResult.success(resultMapList);
    }

    /**
     * 自定义过滤条件、显示字段，并返回单条记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam  查询参数。
     * @param modelMapper 对象映射函数对象。如果为空，则使用MyModelUtil中的缺省转换函数。
     * @return 应答结果对象，包含符合查询过滤条件的单条实体对象。
     */
    public ResponseResult<D> baseGetBy(MyQueryParam queryParam, BaseModelMapper<D, M> modelMapper) {
        ResponseResult<List<D>> result = baseListBy(queryParam, modelMapper);
        if (!result.isSuccess()) {
            return ResponseResult.errorFrom(result);
        }
        List<D> dataList = result.getData();
        D data = null;
        if (CollectionUtils.isNotEmpty(dataList)) {
            data = dataList.get(0);
        }
        return ResponseResult.success(data);
    }

    /**
     * 自定义过滤条件的记录数量统计。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的记录数量。
     */
    public ResponseResult<Integer> baseCountBy(MyQueryParam queryParam) {
        String whereClause = MyWhereCriteria.makeCriteriaString(queryParam.getCriteriaList(), modelClass);
        Integer count = service().getCountByCondition(whereClause);
        return ResponseResult.success(count);
    }

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param param 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    public ResponseResult<List<Map<String, Object>>> baseAggregateBy(MyAggregationParam param) {
        // 完成一些共同性规则的验证。
        VerifyAggregationInfo verifyInfo = this.verifyAndParseAggregationParam(param);
        if (!verifyInfo.isSuccess) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, verifyInfo.errorMsg);
        }
        // 构建SelectList
        StringBuilder selectList = new StringBuilder(64);
        // 一对多场景相对比较简单，直接基于从表的关联键进行group，同时对聚合字段进行聚合计算即可。
        // SQL返回的数据，可直接返回给调用端服务。
        if (param.getAggregationKind() == AggregationKind.ONE_TO_MANY) {
            selectList.append(verifyInfo.groupColumn).append(" ").append(MyAggregationParam.KEY_NAME).append(", ");
        }
        selectList.append(AggregationType.getAggregationFunction(verifyInfo.aggregationType))
                .append("(")
                .append(verifyInfo.aggregationColumn)
                .append(") ")
                .append(MyAggregationParam.VALUE_NAME);
        String whereClause = MyWhereCriteria.makeCriteriaString(param.getWhereCriteriaList(), modelClass);
        List<Map<String, Object>> resultMapList = null;
        // 一对多场景直接返回分组查询计算结果即可。
        if (param.getAggregationKind() == AggregationKind.ONE_TO_MANY) {
            resultMapList = service().getGroupedListByCondition(
                    selectList.toString(), whereClause, verifyInfo.groupColumn);
        } else if (param.getAggregationKind() == AggregationKind.MANY_TO_MANY) {
            boolean stringKey = true;
            if (param.getGroupedInFilterValues().entrySet().iterator().next().getKey() instanceof Number) {
                stringKey = false;
            }
            resultMapList = new LinkedList<>();
            // 迭代分组map，通过多次查询的方式进行数据分组。通过该方式可以避免多次rpc调用，以提升性能。
            for (Map.Entry<Object, Set<Object>> entry : param.getGroupedInFilterValues().entrySet()) {
                StringBuilder groupedSelectList = new StringBuilder(64);
                if (stringKey) {
                    groupedSelectList.append("'").append(entry.getKey()).append("' ");
                } else {
                    groupedSelectList.append(entry.getKey()).append(" ");
                }
                groupedSelectList.append(MyAggregationParam.KEY_NAME).append(", ").append(selectList);
                MyWhereCriteria criteria = new MyWhereCriteria();
                criteria.setModelClazz(modelClass);
                criteria.setCriteria(param.getInFilterField(), MyWhereCriteria.OPERATOR_IN, entry.getValue());
                StringBuilder groupedClause = new StringBuilder(128);
                groupedClause.append(criteria.makeCriteriaString());
                if (StringUtils.isNotBlank(whereClause)) {
                    groupedClause.append(" AND ").append(whereClause);
                }
                List<Map<String, Object>> subResultMapList = service().getGroupedListByCondition(
                        groupedSelectList.toString(), groupedClause.toString(), null);
                resultMapList.addAll(subResultMapList);
            }
        }
        return ResponseResult.success(resultMapList);
    }

    /**
     * 将查询对象中，过滤域对象转换为实体对象。以便作为Service的参数进行数据过滤。
     * 如果Model存在该实体的ModelMapper，就用该ModelMapper转换，否则使用缺省的基于字段反射的copy。
     *
     * @param filterDto   过滤域对象。
     * @param modelMapper 从Dto对象到实体对象的映射对象。
     * @return 转换后的实体过滤对象。
     */
    public M convertFilter(D filterDto, BaseModelMapper<D, M> modelMapper) {
        M filter = null;
        if (filterDto != null) {
            if (modelMapper != null) {
                filter = modelMapper.toModel(filterDto);
            } else {
                filter = MyModelUtil.copyTo(filterDto, modelClass);
            }
        }
        return filter;
    }

    /**
     * 将Model实体对象的集合转换为DomainDto域对象的集合。
     * 如果Model存在该实体的ModelMapper，就用该ModelMapper转换，否则使用缺省的基于字段反射的copy。
     *
     * @param modelList   实体对象列表。
     * @param modelMapper 从实体对象到Dto对象的映射对象。
     * @return 转换后的Dto域对象列表。
     */
    private List<D> convertToDomainList(List<M> modelList, BaseModelMapper<D, M> modelMapper) {
        List<D> resultDtoList;
        if (modelMapper != null) {
            resultDtoList = modelMapper.fromModelList(modelList);
        } else {
            resultDtoList = MyModelUtil.copyCollectionTo(modelList, domainDtoClass);
        }
        return resultDtoList;
    }

    /**
     * 将Model实体对象转换为DomainDto域对象。
     * 如果Model存在该实体的ModelMapper，就用该ModelMapper转换，否则使用缺省的基于字段反射的copy。
     *
     * @param model       实体对象。
     * @param modelMapper 从实体对象到Dto对象的映射对象。
     * @return 转换后的Dto域对象。
     */
    private D convertToDomain(M model, BaseModelMapper<D, M> modelMapper) {
        D resultDto;
        if (modelMapper != null) {
            resultDto = modelMapper.fromModel(model);
        } else {
            resultDto = MyModelUtil.copyTo(model, domainDtoClass);
        }
        return resultDto;
    }

    private VerifyAggregationInfo verifyAndParseAggregationParam(MyAggregationParam param) {
        VerifyAggregationInfo verifyInfo = new VerifyAggregationInfo();
        if (!AggregationKind.isValid(param.getAggregationKind())) {
            verifyInfo.errorMsg = "参数验证失败，聚合类别 [MyAggregationParam.AggregationKind] 数值无效！";
            return verifyInfo;
        }
        Integer aggregationType = param.getAggregationType();
        if (!AggregationType.isValid(aggregationType)) {
            verifyInfo.errorMsg = "参数验证失败，聚合类型 [MyAggregationParam.AggregationType] 数值无效！";
            return verifyInfo;
        }
        String aggregationColumn = MyModelUtil.mapToColumnName(param.getAggregationField(), modelClass);
        if (StringUtils.isBlank(aggregationColumn)) {
            verifyInfo.errorMsg = "参数验证失败，聚合字段 [MyAggregationParam.AggregationField] 为非法值！";
            return verifyInfo;
        }
        // 一对多场景相对比较简单，直接基于从表的关联键进行group，同时对聚合字段进行聚合计算即可。
        // SQL返回的数据，可直接返回给调用端服务。
        if (param.getAggregationKind() == AggregationKind.ONE_TO_MANY) {
            verifyInfo.groupColumn = MyModelUtil.mapToColumnName(param.getGroupField(), modelClass);
            if (StringUtils.isBlank(verifyInfo.groupColumn)) {
                verifyInfo.errorMsg = "参数验证失败，一对多聚合 [MyAggregationParam.GroupField] 分组字段为非法值！";
                return verifyInfo;
            }
        } else {
            String inFilterColumn = MyModelUtil.mapToColumnName(param.getInFilterField(), modelClass);
            if (StringUtils.isBlank(inFilterColumn)) {
                verifyInfo.errorMsg = "参数验证失败，多对多 [MyAggregationParam.InfilterField] 过滤字段为非法值！";
                return verifyInfo;
            }
            if (MapUtils.isEmpty(param.getGroupedInFilterValues())) {
                verifyInfo.errorMsg = "参数验证失败，多对多 [MyAggregationParam.GroupedInFilterValues] 数据集合不能为空！";
                return verifyInfo;
            }
        }
        verifyInfo.isSuccess = true;
        verifyInfo.aggregationType = aggregationType;
        verifyInfo.aggregationColumn = aggregationColumn;
        return verifyInfo;
    }

    private static final class VerifyAggregationInfo {
        private boolean isSuccess = false;
        private String errorMsg;
        private Integer aggregationType;
        private String aggregationColumn;
        private String groupColumn;
    }
}
