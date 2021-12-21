package com.orangeforms.common.core.base.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.orangeforms.common.core.annotation.*;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.client.BaseClient;
import com.orangeforms.common.core.constant.AggregationKind;
import com.orangeforms.common.core.constant.AggregationType;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.exception.InvalidDataFieldException;
import com.orangeforms.common.core.exception.MyRuntimeException;
import com.orangeforms.common.core.exception.RemoteDataBuildException;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.AopTargetUtil;
import com.orangeforms.common.core.util.ApplicationContextHolder;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.LogMessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

/**
 * 所有Service的基类。
 *
 * @param <M> Model对象的类型。
 * @param <K> Model对象主键的类型。
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public abstract class BaseService<M, K extends Serializable> extends ServiceImpl<BaseDaoMapper<M>, M> implements IBaseService<M, K> {
    /**
     * 当前Service关联的主Model实体对象的Class。
     */
    @Getter
    protected final Class<M> modelClass;
    /**
     * 当前Service关联的主Model实体对象主键字段的Class。
     */
    protected final Class<K> idFieldClass;
    /**
     * 当前Service关联的主Model对象的实际表名称。
     */
    protected final String tableName;
    /**
     * 当前Service关联的主Model对象主键字段名称。
     */
    protected String idFieldName;
    /**
     * 当前Service关联的主数据表中主键列名称。
     */
    protected String idColumnName;
    /**
     * 当前Service关联的主Model对象逻辑删除字段名称。
     */
    protected String deletedFlagFieldName;
    /**
     * 当前Service关联的主数据表中逻辑删除字段名称。
     */
    protected String deletedFlagColumnName;
    /**
     * 当前Service关联的主Model对象租户Id字段。
     */
    protected Field tenantIdField;
    /**
     * 当前Service关联的主Model对象租户Id字段名称。
     */
    protected String tenantIdFieldName;
    /**
     * 当前Service关联的主数据表中租户Id列名称。
     */
    protected String tenantIdColumnName;
    /**
     * 当前Job服务源主表Model对象的最后更新时间字段名称。
     */
    protected String jobUpdateTimeFieldName;
    /**
     * 当前Job服务源主表Model对象的最后更新时间列名称。
     */
    protected String jobUpdateTimeColumnName;
    /**
     * 当前业务服务源主表Model对象最后更新时间字段名称。
     */
    protected String updateTimeFieldName;
    /**
     * 当前业务服务源主表Model对象最后更新时间列名称。
     */
    protected String updateTimeColumnName;
    /**
     * 当前业务服务源主表Model对象最后更新用户Id字段名称。
     */
    protected String updateUserIdFieldName;
    /**
     * 当前业务服务源主表Model对象最后更新用户Id列名称。
     */
    protected String updateUserIdColumnName;
    /**
     * 当前Service关联的主Model对象主键字段赋值方法的反射对象。
     */
    protected Method setIdFieldMethod;
    /**
     * 当前Service关联的主Model对象主键字段访问方法的反射对象。
     */
    protected Method getIdFieldMethod;
    /**
     * 当前Service关联的主Model对象逻辑删除字段赋值方法的反射对象。
     */
    protected Method setDeletedFlagMethod;
    /**
     * 当前Service关联的主Model对象的所有本地服务常量字典关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<LocalRelationStruct> relationConstDictStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有本地服务字典关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<LocalRelationStruct> localRelationDictStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有本地服务一对一关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<LocalRelationStruct> localRelationOneToOneStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有本地服务一对多关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<LocalRelationStruct> localRelationOneToManyStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有多对多关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<LocalRelationStruct> localRelationManyToManyStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有本地服务一对多聚合关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<LocalRelationStruct> localRelationOneToManyAggrStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有本地服务多对多聚合关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<LocalRelationStruct> localRelationManyToManyAggrStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有远程字典关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<RemoteRelationStruct> remoteRelationDictStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有远程一对一关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<RemoteRelationStruct> remoteRelationOneToOneStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有远程一对多聚合关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<RemoteRelationStruct> remoteRelationOneToManyAggrStructList = new LinkedList<>();
    /**
     * 当前Service关联的主Model对象的所有远程多对多聚合关联的结构列表，该字段在系统启动阶段一次性预加载，提升运行时效率。
     */
    private final List<RemoteRelationStruct> remoteRelationManyToManyAggrStructList = new LinkedList<>();

    private static final String AND_OP = " AND ";

    @Override
    public BaseDaoMapper<M> getBaseMapper() {
        return mapper();
    }

    /**
     * 构造函数，在实例化的时候，一次性完成所有有关主Model对象信息的加载。
     */
    @SuppressWarnings("unchecked")
    public BaseService() {
        modelClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        idFieldClass = (Class<K>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        this.tableName = modelClass.getAnnotation(TableName.class).value();
        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field field : fields) {
            initializeField(field);
        }
    }

    private void initializeField(Field field) {
        if (idFieldName == null && null != field.getAnnotation(TableId.class)) {
            idFieldName = field.getName();
            TableId c = field.getAnnotation(TableId.class);
            idColumnName = c == null ? idFieldName : c.value();
            setIdFieldMethod = ReflectUtil.getMethod(
                    modelClass, "set" + StringUtils.capitalize(idFieldName), idFieldClass);
            getIdFieldMethod = ReflectUtil.getMethod(
                    modelClass, "get" + StringUtils.capitalize(idFieldName));
        }
        if (jobUpdateTimeFieldName == null && null != field.getAnnotation(JobUpdateTimeColumn.class)) {
            jobUpdateTimeFieldName = field.getName();
            jobUpdateTimeColumnName = this.safeMapToColumnName(jobUpdateTimeFieldName);
        }
        if (deletedFlagFieldName == null && null != field.getAnnotation(TableLogic.class)) {
            deletedFlagFieldName = field.getName();
            deletedFlagColumnName = this.safeMapToColumnName(deletedFlagFieldName);
            setDeletedFlagMethod = ReflectUtil.getMethod(
                    modelClass, "set" + StringUtils.capitalize(deletedFlagFieldName), Integer.class);
        }
        if (tenantIdFieldName == null && null != field.getAnnotation(TenantFilterColumn.class)) {
            tenantIdField = field;
            tenantIdFieldName = field.getName();
            tenantIdColumnName = this.safeMapToColumnName(tenantIdFieldName);
        }
    }

    /**
     * 获取子类中注入的Mapper类
     *
     * @return 子类中注入的Mapper类
     */
    protected abstract BaseDaoMapper<M> mapper();

    /**
     * 该方法为BaseService类buildXXX方法中使用到的模板方法，子类实现方法中提供具体的实现，
     * 可根据配置项决定是否忽略远程调用异常。由于来自于配置项，因此可在运行时动态修改。
     * 是否忽略获取远程查询数据过程中出现的任何错误。包括各种逻辑错误，系统错误等。
     * 通常建议在开发阶段设置为false，以便及时发现问题。
     *
     * @return true忽略，否则抛出RemoteDataBuildException异常。
     */
    protected boolean ignoreRpcError() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveNewOrUpdate(M data, Consumer<M> saveNew, BiConsumer<M, M> update) {
        if (data == null) {
            return;
        }
        K id = (K) ReflectUtil.getFieldValue(data, idFieldName);
        if (id == null) {
            saveNew.accept(data);
        } else {
            update.accept(data, this.getById(id));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveNewOrUpdateBatch(List<M> dataList, Consumer<List<M>> saveNewBatch, BiConsumer<M, M> update) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        List<M> saveNewDataList = dataList.stream()
                .filter(c -> ReflectUtil.getFieldValue(c, idFieldName) == null).collect(toList());
        if (CollUtil.isNotEmpty(saveNewDataList)) {
            saveNewBatch.accept(saveNewDataList);
        }
        List<M> updateDataList = dataList.stream()
                .filter(c -> ReflectUtil.getFieldValue(c, idFieldName) != null).collect(toList());
        if (CollUtil.isNotEmpty(updateDataList)) {
            for (M data : updateDataList) {
                K id = (K) ReflectUtil.getFieldValue(data, idFieldName);
                update.accept(data, this.getById(id));
            }
        }
    }

    /**
     * 根据过滤条件删除数据。
     *
     * @param filter 过滤对象。
     * @return 删除数量。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer removeBy(M filter) {
        return mapper().delete(new QueryWrapper<>(filter));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBatchOneToManyRelation(
            String relationFieldName,
            Object relationFieldValue,
            String updateUserIdFieldName,
            String updateTimeFieldName,
            List<M> dataList,
            Consumer<List<M>> batchInserter) {
        // 删除在现有数据列表dataList中不存在的从表数据。
        QueryWrapper<M> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(this.safeMapToColumnName(relationFieldName), relationFieldValue);
        if (CollUtil.isNotEmpty(dataList)) {
            Set<Object> keptIdSet = dataList.stream()
                    .filter(c -> ReflectUtil.getFieldValue(c, idFieldName) != null)
                    .map(c -> ReflectUtil.getFieldValue(c, idFieldName)).collect(toSet());
            if (CollUtil.isNotEmpty(keptIdSet)) {
                queryWrapper.notIn(idColumnName, keptIdSet);
            }
        }
        mapper().delete(queryWrapper);
        if (CollUtil.isNotEmpty(dataList)) {
            // 没有包含主键的对象被视为新对象，为了效率最优化，这里执行批量插入。
            List<M> newDataList = dataList.stream()
                    .filter(c -> ReflectUtil.getFieldValue(c, idFieldName) == null).collect(toList());
            if (CollUtil.isNotEmpty(newDataList)) {
                newDataList.forEach(o -> ReflectUtil.setFieldValue(o, relationFieldName, relationFieldValue));
                batchInserter.accept(newDataList);
            }
            // 对于主键已经存在的数据，我们视为已存在数据，这里执行逐条更新操作。
            List<M> updateDataList =
                    dataList.stream().filter(c -> ReflectUtil.getFieldValue(c, idFieldName) != null).collect(toList());
            for (M updateData : updateDataList) {
                // 如果前端将更新用户Id置空，这里使用当前用户更新该字段。
                if (updateUserIdFieldName != null && ReflectUtil.getFieldValue(updateData, updateUserIdFieldName) == null) {
                    ReflectUtil.setFieldValue(updateData, updateUserIdFieldName, TokenData.takeFromRequest().getUserId());
                }
                // 如果前端将更新时间置空，这里使用当前时间更新该字段。
                if (updateTimeFieldName != null && ReflectUtil.getFieldValue(updateData, updateTimeFieldName) == null) {
                    ReflectUtil.setFieldValue(updateData, updateTimeFieldName, new Date());
                }
                if (this.deletedFlagFieldName != null) {
                    ReflectUtil.setFieldValue(updateData, deletedFlagFieldName, GlobalDeletedFlag.NORMAL);
                }
                @SuppressWarnings("unchecked")
                UpdateWrapper<M> uw = this.createUpdateQueryForNullValue(
                        updateData, (K) ReflectUtil.getFieldValue(updateData, idFieldName));
                mapper().update(updateData, uw);
            }
        }
    }

    /**
     * 判断主键Id关联的数据是否存在。
     *
     * @param id 主键Id
     * @return 存在返回true，否则false
     */
    @Override
    public boolean existId(K id) {
        return this.getById(id) != null;
    }

    /**
     * 判断指定字段的数据是否存在，且仅仅存在一条记录。
     * 如果是基于主键的过滤，会直接调用existId过滤函数，提升性能。在有缓存的场景下，也可以利用缓存。
     *
     * @param fieldName  待过滤的字段名(Java 字段)。
     * @param fieldValue 字段值。
     * @return 存在且仅存在一条返回true，否则false。
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean existOne(String fieldName, Object fieldValue) {
        if (fieldName.equals(this.idFieldName)) {
            return this.existId((K) fieldValue);
        }
        String columnName = MyModelUtil.mapToColumnName(fieldName, modelClass);
        return mapper().selectCount(new QueryWrapper<M>().eq(columnName, fieldValue)) == 1;
    }

    /**
     * 返回符合 filterField = filterValue 条件的一条数据。
     *
     * @param filterField 过滤的Java字段。
     * @param filterValue 过滤的Java字段值。
     * @return 查询后的数据对象。
     */
    @SuppressWarnings("unchecked")
    @Override
    public M getOne(String filterField, Object filterValue) {
        if (filterField.equals(idFieldName)) {
            return this.getById((K) filterValue);
        }
        String columnName = this.safeMapToColumnName(filterField);
        QueryWrapper<M> queryWrapper = new QueryWrapper<M>().eq(columnName, filterValue);
        return mapper().selectOne(queryWrapper);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     *
     * @param id             主表主键Id。
     * @param relationParam  实体对象数据组装的参数构建器。
     * @return 查询结果对象。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    @Override
    public M getByIdWithRelation(K id, MyRelationParam relationParam) {
        M dataObject = this.getById(id);
        this.buildRelationForData(dataObject, relationParam);
        return dataObject;
     }

    /**
     * 获取所有数据。单表查询，不进行任何数据关联。
     *
     * @return 返回所有数据。
     */
    @Override
    public List<M> getAllList() {
        return mapper().selectList(Wrappers.emptyWrapper());
    }

    /**
     * 获取排序后所有数据。单表查询，不进行任何数据关联。
     *
     * @param orderByProperties 需要排序的字段属性，这里使用Java对象中的属性名，而不是数据库字段名。
     * @return 返回排序后所有数据。
     */
    @Override
    public List<M> getAllListByOrder(String... orderByProperties) {
        String[] columns = new String[orderByProperties.length];
        for (int i = 0; i < orderByProperties.length; i++) {
            columns[i] = this.safeMapToColumnName(orderByProperties[i]);
        }
        return mapper().selectList(new QueryWrapper<M>().orderByAsc(columns));
    }

    /**
     * 判断参数值主键集合中的所有数据，是否全部存在
     *
     * @param idSet  待校验的主键集合。
     * @return 全部存在返回true，否则false。
     */
    @Override
    public boolean existAllPrimaryKeys(Set<K> idSet) {
        if (CollectionUtils.isEmpty(idSet)) {
            return true;
        }
        return this.existUniqueKeyList(idFieldName, idSet);
    }

    /**
     * 判断参数值列表中的所有数据，是否全部存在。另外，keyName字段在数据表中必须是唯一键值，否则返回结果会出现误判。
     *
     * @param inFilterField  待校验的数据字段，这里使用Java对象中的属性，如courseId，而不是数据字段名course_id。
     * @param inFilterValues 数据值集合。
     * @return 全部存在返回true，否则false。
     */
    @Override
    public <T> boolean existUniqueKeyList(String inFilterField, Set<T> inFilterValues) {
        if (CollectionUtils.isEmpty(inFilterValues)) {
            return true;
        }
        String column = this.safeMapToColumnName(inFilterField);
        return mapper().selectCount(new QueryWrapper<M>().in(column, inFilterValues)) == inFilterValues.size();
    }

    /**
     * 返回符合主键 in (idValues) 条件的所有数据。单表查询，不进行任何数据关联。
     *
     * @param idValues 主键值列表。
     * @return 检索后的数据列表。
     */
    @Override
    public List<M> getInList(Set<K> idValues) {
        return this.getInList(idFieldName, idValues, null);
    }

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据。单表查询，不进行任何数据关联。
     *
     * @param inFilterField  参与(In-list)过滤的Java对象字段。
     * @param inFilterValues 参与(In-list)过滤的字段值集合。
     * @return 检索后的数据列表。
     */
    @Override
    public <T> List<M> getInList(String inFilterField, Set<T> inFilterValues) {
        return this.getInList(inFilterField, inFilterValues, null);
    }

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据，并根据orderBy字段排序。单表查询，不进行任何数据关联。
     *
     * @param inFilterField  参与(In-list)过滤的Java对象字段。
     * @param inFilterValues 参与(In-list)过滤的字段值集合。
     * @param orderBy        SQL的ORDER BY排序从句。
     * @return 检索后的数据列表。
     */
    @Override
    public <T> List<M> getInList(String inFilterField, Set<T> inFilterValues, String orderBy) {
        if (CollectionUtils.isEmpty(inFilterValues)) {
            return new LinkedList<>();
        }
        String column = this.safeMapToColumnName(inFilterField);
        QueryWrapper<M> queryWrapper = new QueryWrapper<M>().in(column, inFilterValues);
        if (StringUtils.isNotBlank(orderBy)) {
            queryWrapper.last(orderBy);
        }
        return mapper().selectList(queryWrapper);
    }

    /**
     * 返回符合主键 in (idValues) 条件的所有数据。同时返回关联数据。
     *
     * @param idValues      主键值集合。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @return 检索后的数据列表。
     */
    @Override
    public List<M> getInListWithRelation(Set<K> idValues, MyRelationParam relationParam) {
        List<M> resultList = this.getInList(idValues);
        this.buildRelationForDataList(resultList, relationParam);
        return resultList;
    }

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据。同时返回关联数据。
     *
     * @param inFilterField  参与(In-list)过滤的Java字段。
     * @param inFilterValues 参与(In-list)过滤的Java字段值集合。
     * @param relationParam  实体对象数据组装的参数构建器。
     * @return 检索后的数据列表。
     */
    @Override
    public <T> List<M> getInListWithRelation(String inFilterField, Set<T> inFilterValues, MyRelationParam relationParam) {
        List<M> resultList = this.getInList(inFilterField, inFilterValues);
        this.buildRelationForDataList(resultList, relationParam);
        return resultList;
    }

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据，并根据orderBy字段排序。同时返回关联数据。
     *
     * @param inFilterField  参与(In-list)过滤的Java字段。
     * @param inFilterValues 参与(In-list)过滤的Java字段值集合。
     * @param orderBy        排序字段。
     * @param relationParam  实体对象数据组装的参数构建器。
     * @return 检索后的数据列表。
     */
    @Override
    public <T> List<M> getInListWithRelation(
            String inFilterField, Set<T> inFilterValues, String orderBy, MyRelationParam relationParam) {
        List<M> resultList = this.getInList(inFilterField, inFilterValues, orderBy);
        this.buildRelationForDataList(resultList, relationParam);
        return resultList;
    }

    /**
     * 用参数对象作为过滤条件，获取数据数量。
     *
     * @param filter 该方法基于mybatis 通用mapper，过滤对象中，只有被赋值的字段，才会成为where中的条件。
     * @return 返回过滤后的数据数量。
     */
    @Override
    public int getCountByFilter(M filter) {
        return mapper().selectCount(new QueryWrapper<>(filter));
    }

    /**
     * 用参数对象作为过滤条件，判断是否存在过滤数据。
     *
     * @param filter 该方法基于mybatis 通用mapper，过滤对象中，只有被赋值的字段，才会成为where中的条件
     * @return 存在返回true，否则false
     */
    @Override
    public boolean existByFilter(M filter) {
        return this.getCountByFilter(filter) > 0;
    }

    /**
     * 用参数对象作为过滤条件，获取查询结果。
     *
     * @param filter 该方法基于mybatis的通用mapper，如果参数为null，则返回全部数据。
     * @return 返回过滤后的数据。
     */
    @Override
    public List<M> getListByFilter(M filter) {
        return mapper().selectList(new QueryWrapper<>(filter));
    }

    /**
     * 获取父主键Id下的所有子数据列表。单表查询，不进行任何数据关联。
     *
     * @param parentIdFieldName 父主键字段名字，如"courseId"。
     * @param parentId          父主键的值。
     * @return 父主键Id下的所有子数据列表。
     */
    @Override
    public List<M> getListByParentId(String parentIdFieldName, K parentId) {
        QueryWrapper<M> queryWrapper = new QueryWrapper<>();
        String parentIdColumn = this.safeMapToColumnName(parentIdFieldName);
        if (parentId != null) {
            queryWrapper.eq(parentIdColumn, parentId);
        } else {
            queryWrapper.isNull(parentIdColumn);
        }
        return mapper().selectList(queryWrapper);
    }

    /**
     * 根据指定的显示字段列表、过滤条件字符串和分组字符串，返回聚合计算后的查询结果。(基本是内部框架使用，不建议外部接口直接使用)。
     *
     * @param selectFields 选择的字段列表，多个字段逗号分隔。
     *                     NOTE: 如果数据表字段和Java对象字段名字不同，Java对象字段应该以别名的形式出现。
     *                     如: table_column_name modelFieldName。否则无法被反射回Bean对象。
     * @param whereClause  SQL常量形式的条件从句。
     * @param groupBy      SQL常量形式分组字段列表，逗号分隔。
     * @return 聚合计算后的数据结果集。
     */
    @Override
    public List<Map<String, Object>> getGroupedListByCondition(
            String selectFields, String whereClause, String groupBy) {
        return mapper().getGroupedListByCondition(tableName, selectFields, whereClause, groupBy);
    }

    /**
     * 根据指定的显示字段列表、过滤条件字符串和排序字符串，返回查询结果。(基本是内部框架使用，不建议外部接口直接使用)。
     *
     * @param selectList  选择的Java字段列表。如果为空表示返回全部字段。
     * @param filter      过滤对象。
     * @param whereClause SQL常量形式的条件从句。
     * @param orderBy     SQL常量形式排序字段列表，逗号分隔。
     * @return 查询结果。
     */
    @Override
    public List<M> getListByCondition(List<String> selectList, M filter, String whereClause, String orderBy) {
        QueryWrapper<M> queryWrapper = new QueryWrapper<>(filter);
        if (CollectionUtils.isNotEmpty(selectList)) {
            String[] columns = new String[selectList.size()];
            for (int i = 0; i < selectList.size(); i++) {
                columns[i] = this.safeMapToColumnName(selectList.get(i));
            }
            queryWrapper.select(columns);
        }
        if (StringUtils.isNotBlank(whereClause)) {
            queryWrapper.apply(whereClause);
        }
        if (StringUtils.isNotBlank(orderBy)) {
            queryWrapper.last(" ORDER BY " + orderBy);
        }
        return mapper().selectList(queryWrapper);
    }

    /**
     * 用指定过滤条件，计算记录数量。(基本是内部框架使用，不建议外部接口直接使用)。
     *
     * @param whereClause SQL常量形式的条件从句。
     * @return 返回过滤后的数据数量。
     */
    @Override
    public Integer getCountByCondition(String whereClause) {
        return mapper().getCountByCondition(this.tableName, whereClause);
    }

    @Override
    public CallResult verifyRelatedData(M data, M originalData) {
        return CallResult.ok();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CallResult verifyRelatedData(List<M> dataList) {
        if (CollUtil.isEmpty(dataList)) {
            return CallResult.ok();
        }
        // 1. 先过滤出数据列表中的主键Id集合。
        Set<K> idList = dataList.stream()
                .filter(c -> ReflectUtil.getFieldValue(c, idFieldName) != null)
                .map(c -> (K) ReflectUtil.getFieldValue(c, idFieldName)).collect(toSet());
        // 2. 列表中，我们目前仅支持全部是更新数据，或全部新增数据，不能混着。如果有主键值，说明当前全是更新数据。
        if (CollUtil.isNotEmpty(idList)) {
            // 3. 这里是批量读取的优化，用一个主键值得in list查询，一步获取全部原有数据。然后再在内存中基于Map排序。
            List<M> originalList = this.getInList(idList);
            Map<Object, M> originalMap = originalList.stream()
                    .collect(toMap(c -> ReflectUtil.getFieldValue(c, idFieldName), c2 -> c2));
            // 迭代列表，传入当前最新数据和更新前数据进行比对，如果关联数据变化了，就对新数据进行合法性验证。
            for (M data : dataList) {
                CallResult result = this.verifyRelatedData(
                        data, originalMap.get(ReflectUtil.getFieldValue(data, idFieldName)));
                if (!result.isSuccess()) {
                    return result;
                }
            }
        } else {
            // 4. 迭代列表，传入当前最新数据，对关联数据进行合法性验证。
            for (M data : dataList) {
                CallResult result = this.verifyRelatedData(data, null);
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }
        return CallResult.ok();
    }

    @Override
    public CallResult verifyRemoteRelatedData(M data, M originalData) {
        return CallResult.ok();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CallResult verifyRemoteRelatedData(List<M> dataList) {
        if (CollUtil.isEmpty(dataList)) {
            return CallResult.ok();
        }
        // 1. 先过滤出数据列表中的主键Id集合。
        Set<K> idList = dataList.stream()
                .filter(c -> ReflectUtil.getFieldValue(c, idFieldName) != null)
                .map(c -> (K) ReflectUtil.getFieldValue(c, idFieldName)).collect(toSet());
        // 2. 列表中，我们目前仅支持全部是更新数据，或全部新增数据，不能混着。如果有主键值，说明当前全是更新数据。
        if (CollUtil.isNotEmpty(idList)) {
            // 3. 这里是批量读取的优化，用一个主键值得in list查询，一步获取全部原有数据。然后再在内存中基于Map排序。
            List<M> originalList = this.getInList(idList);
            Map<Object, M> originalMap = originalList.stream()
                    .collect(toMap(c -> ReflectUtil.getFieldValue(c, idFieldName), c2 -> c2));
            // 迭代列表，传入当前最新数据和更新前数据进行比对，如果关联数据变化了，就对新数据进行合法性验证。
            for (M data : dataList) {
                CallResult result = this.verifyRemoteRelatedData(
                        data, originalMap.get(ReflectUtil.getFieldValue(data, idFieldName)));
                if (!result.isSuccess()) {
                    return result;
                }
            }
        } else {
            // 4. 迭代列表，传入当前最新数据，对关联数据进行合法性验证。
            for (M data : dataList) {
                CallResult result = this.verifyRemoteRelatedData(data, null);
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }
        return CallResult.ok();
    }

    @Override
    public CallResult verifyAllRelatedData(M data, M originalData) {
        CallResult verifyResult = this.verifyRelatedData(data, originalData);
        if (!verifyResult.isSuccess()) {
            return verifyResult;
        }
        return this.verifyRemoteRelatedData(data, originalData);
    }

    @Override
    public CallResult verifyAllRelatedData(List<M> dataList) {
        CallResult verifyResult = this.verifyRelatedData(dataList);
        if (!verifyResult.isSuccess()) {
            return verifyResult;
        }
        return this.verifyRemoteRelatedData(dataList);
    }

    /**
     * 集成所有与主表实体对象相关的关联数据列表。包括本地和远程服务的一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param resultList    主表实体对象列表。数据集成将直接作用于该对象列表。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    @Override
    public void buildRelationForDataList(List<M> resultList, MyRelationParam relationParam) {
        this.buildRelationForDataList(resultList, relationParam, null);
    }

    /**
     * 集成所有与主表实体对象相关的关联数据列表。包括本地和远程服务的一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param resultList      主表实体对象列表。数据集成将直接作用于该对象列表。
     * @param relationParam   实体对象数据组装的参数构建器。
     * @param ignoreFields  该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    @Override
    public void buildRelationForDataList(
            List<M> resultList, MyRelationParam relationParam, Set<String> ignoreFields) {
        if (relationParam == null || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        boolean dataFilterValue = GlobalThreadLocal.setDataFilter(false);
        try {
            // 集成本地一对一和字段级别的数据关联。
            // NOTE: 这里必须要在集成远程一对一之前集成本地一对一。因为远程集成方法中，会为本地一对一从表数据进行远程集成。
            boolean buildOneToOne = relationParam.isBuildOneToOne() || relationParam.isBuildOneToOneWithDict();
            // 这里集成一对一关联。
            if (buildOneToOne) {
                this.buildOneToOneForDataList(resultList, relationParam.isBuildOneToOneWithDict(), ignoreFields);
            }
            // 集成一对多关联
            if (relationParam.isBuildOneToMany()) {
                this.buildOneToManyForDataList(resultList, ignoreFields);
            }
            // 这里集成字典关联
            if (relationParam.isBuildDict()) {
                // 构建常量字典关联关系
                this.buildConstDictForDataList(resultList, ignoreFields);
                this.buildDictForDataList(resultList, buildOneToOne, ignoreFields);
            }
            // 集成远程一对一和字段级别的数据关联。
            boolean buildRemoteOneToOne =
                    relationParam.isBuildRemoteOneToOne() || relationParam.isBuildRemoteOneToOneWithDict();
            if (buildRemoteOneToOne) {
                this.buildRemoteOneToOneForDataList(resultList, relationParam.isBuildRemoteOneToOneWithDict(), ignoreFields);
            }
            if (relationParam.isBuildRemoteDict()) {
                this.buildRemoteDictForDataList(resultList, buildRemoteOneToOne, ignoreFields);
            }
            // 组装本地聚合计算关联数据
            if (relationParam.isBuildAggregation()) {
                // 处理多一多场景下，根据主表的结果，进行从表聚合数据的计算。
                this.buildOneToManyAggregationForDataList(resultList, buildAggregationAdditionalWhereCriteria(), ignoreFields);
                // 处理多对多场景下，根据主表的结果，进行从表聚合数据的计算。
                this.buildManyToManyAggregationForDataList(resultList, buildAggregationAdditionalWhereCriteria(), ignoreFields);
            }
            // 组装远程聚合计算关联数据
            if (relationParam.isBuildRemoteAggregation()) {
                // 一对多场景。
                this.buildRemoteOneToManyAggregationForDataList(resultList, buildAggregationAdditionalWhereCriteria(), ignoreFields);
                // 处理多对多场景。
                this.buildRemoteManyToManyAggregationForDataList(resultList, buildAggregationAdditionalWhereCriteria(), ignoreFields);
            }
        } finally {
            GlobalThreadLocal.setDataFilter(dataFilterValue);
        }
    }

    /**
     * 该函数主要用于对查询结果的批量导出。不同于支持分页的列表查询，批量导出没有分页机制，
     * 因此在导出数据量较大的情况下，很容易给数据库的内存、CPU和IO带来较大的压力。而通过
     * 我们的分批处理，可以极大的规避该问题的出现几率。调整batchSize的大小，也可以有效的
     * 改善运行效率。
     * 我们目前的处理机制是，先从主表取出所有符合条件的主表数据，这样可以避免分批处理时，
     * 后面几批数据，因为skip过多而带来的效率问题。因为是单表过滤，不会给数据库带来过大的压力。
     * 之后再在主表结果集数据上进行分批级联处理。
     * 集成所有与主表实体对象相关的关联数据列表。包括一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param resultList    主表实体对象列表。数据集成将直接作用于该对象列表。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @param batchSize     每批集成的记录数量。小于等于0时将不做分批处理。
     */
    @Override
    public void buildRelationForDataList(List<M> resultList, MyRelationParam relationParam, int batchSize) {
        this.buildRelationForDataList(resultList, relationParam, batchSize, null);
    }

    /**
     * 该函数主要用于对查询结果的批量导出。不同于支持分页的列表查询，批量导出没有分页机制，
     * 因此在导出数据量较大的情况下，很容易给数据库的内存、CPU和IO带来较大的压力。而通过
     * 我们的分批处理，可以极大的规避该问题的出现几率。调整batchSize的大小，也可以有效的
     * 改善运行效率。
     * 我们目前的处理机制是，先从主表取出所有符合条件的主表数据，这样可以避免分批处理时，
     * 后面几批数据，因为skip过多而带来的效率问题。因为是单表过滤，不会给数据库带来过大的压力。
     * 之后再在主表结果集数据上进行分批级联处理。
     * 集成所有与主表实体对象相关的关联数据列表。包括一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param resultList    主表实体对象列表。数据集成将直接作用于该对象列表。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @param batchSize     每批集成的记录数量。小于等于0时将不做分批处理。
     * @param ignoreFields  该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    @Override
    public void buildRelationForDataList(
            List<M> resultList, MyRelationParam relationParam, int batchSize, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }
        if (batchSize <= 0) {
            this.buildRelationForDataList(resultList, relationParam);
            return;
        }
        int totalCount = resultList.size();
        int fromIndex = 0;
        int toIndex = Math.min(batchSize, totalCount);
        while (toIndex > fromIndex) {
            List<M> subResultList = resultList.subList(fromIndex, toIndex);
            this.buildRelationForDataList(subResultList, relationParam);
            fromIndex = toIndex;
            toIndex = Math.min(batchSize + fromIndex, totalCount);
        }
    }

    /**
     * 集成所有与主表实体对象相关的关联数据对象。包括本地和远程服务的一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param dataObject    主表实体对象。数据集成将直接作用于该对象。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @param <T>           实体对象类型。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    @Override
    public <T extends M> void buildRelationForData(T dataObject, MyRelationParam relationParam) {
        this.buildRelationForData(dataObject, relationParam, null);
    }

    /**
     * 集成所有与主表实体对象相关的关联数据对象。包括本地和远程服务的一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param dataObject    主表实体对象。数据集成将直接作用于该对象。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @param ignoreFields  该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @param <T>           实体对象类型。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    @Override
    public <T extends M> void buildRelationForData(T dataObject, MyRelationParam relationParam, Set<String> ignoreFields) {
        if (dataObject == null || relationParam == null) {
            return;
        }
        boolean dataFilterValue = GlobalThreadLocal.setDataFilter(false);
        try {
            // 集成本地一对一和字段级别的数据关联。
            boolean buildOneToOne = relationParam.isBuildOneToOne() || relationParam.isBuildOneToOneWithDict();
            if (buildOneToOne) {
                this.buildOneToOneForData(dataObject, relationParam.isBuildOneToOneWithDict(), ignoreFields);
            }
            // 集成一对多关联
            if (relationParam.isBuildOneToMany()) {
                this.buildOneToManyForData(dataObject, ignoreFields);
            }
            if (relationParam.isBuildDict()) {
                // 构建常量字典关联关系
                this.buildConstDictForData(dataObject, ignoreFields);
                // 构建本地数据字典关联关系。
                this.buildDictForData(dataObject, buildOneToOne, ignoreFields);
            }
            boolean buildRemoteOneToOne =
                    relationParam.isBuildRemoteOneToOne() || relationParam.isBuildRemoteOneToOneWithDict();
            if (buildRemoteOneToOne) {
                this.buildRemoteOneToOneForData(dataObject, relationParam.isBuildRemoteOneToOneWithDict(), ignoreFields);
            }
            if (relationParam.isBuildRemoteDict()) {
                this.buildRemoteDictForData(dataObject, buildRemoteOneToOne, ignoreFields);
            }
            // 组装本地聚合计算关联数据
            if (relationParam.isBuildAggregation()) {
                // 构建一对多场景
                buildOneToManyAggregationForData(dataObject, buildAggregationAdditionalWhereCriteria(), ignoreFields);
                // 开始处理多对多场景。
                buildManyToManyAggregationForData(dataObject, buildAggregationAdditionalWhereCriteria(), ignoreFields);
            }
            // 组装远程聚合计算关联数据
            if (relationParam.isBuildRemoteAggregation()) {
                // 处理一对多场景
                this.buildRemoteOneToManyAggregationForData(dataObject, buildAggregationAdditionalWhereCriteria(), ignoreFields);
                // 处理多对多场景
                this.buildRemoteManyToManyAggregationForData(dataObject, buildAggregationAdditionalWhereCriteria(), ignoreFields);
            }
            if (relationParam.isBuildRelationManyToMany()) {
                this.buildRelationManyToMany(dataObject, ignoreFields);
            }
        } finally {
            GlobalThreadLocal.setDataFilter(dataFilterValue);
        }
    }

    /**
     * 为参数列表数据集成本地静态字典关联数据。
     *
     * @param resultList   主表数据列表。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildConstDictForDataList(List<M> resultList, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.relationConstDictStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.relationConstDictStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            for (M dataObject : resultList) {
                Object id = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
                if (id != null) {
                    String name = relationStruct.dictMap.get(id);
                    if (name != null) {
                        Map<String, Object> dictMap = new HashMap<>(2);
                        dictMap.put("id", id);
                        dictMap.put("name", name);
                        ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, dictMap);
                    }
                }
            }
        }
    }

    /**
     * 为参数实体对象数据集成本地静态字典关联数据。
     *
     * @param dataObject   实体对象。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private <T extends M> void buildConstDictForData(T dataObject, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.relationConstDictStructList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.relationConstDictStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object id = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (id != null) {
                String name = relationStruct.dictMap.get(id);
                if (name != null) {
                    Map<String, Object> dictMap = new HashMap<>(2);
                    dictMap.put("id", id);
                    dictMap.put("name", name);
                    ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, dictMap);
                }
            }
        }
    }

    /**
     * 集成主表和多对多中间表之间的关联关系。
     *
     * @param dataObject   关联后的主表数据对象。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private <T extends M> void buildRelationManyToMany(T dataObject, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.localRelationManyToManyStructList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.localRelationManyToManyStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object masterIdValue = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            String masterIdColumn = this.safeMapToColumnName(relationStruct.masterIdField.getName());
            Map<String, Object> filterMap = new HashMap<>(1);
            filterMap.put(masterIdColumn, masterIdValue);
            List<?> manyToManyList = relationStruct.manyToManyMapper.selectByMap(filterMap);
            ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, manyToManyList);
        }
    }

    /**
     * 为实体对象参数列表数据集成远程一对一关联数据。
     *
     * @param resultList   实体对象数据列表。
     * @param withDict     关联从表数据后，是否把从表的字典数据也一起关联了。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    private void buildRemoteOneToOneForDataList(List<M> resultList, boolean withDict, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.remoteRelationOneToOneStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationOneToOneStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Set<Object> masterIdSet = resultList.stream()
                    .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                    .filter(Objects::nonNull)
                    .collect(toSet());
            if (CollectionUtils.isEmpty(masterIdSet)) {
                continue;
            }
            boolean buildRemoteOneToOneDict = withDict && relationStruct.relationOneToOne.loadSlaveDict();
            MyQueryParam queryParam = new MyQueryParam(buildRemoteOneToOneDict);
            queryParam.setUseDataFilter(false);
            MyWhereCriteria whereCriteria = new MyWhereCriteria();
            whereCriteria.setCriteria(
                    relationStruct.relationOneToOne.slaveIdField(), MyWhereCriteria.OPERATOR_IN, masterIdSet);
            queryParam.addCriteriaList(whereCriteria);
            ResponseResult<MyPageData<Object>> result = relationStruct.remoteClient.listBy(queryParam);
            if (result.isSuccess()) {
                List<Object> relationList = result.getData().getDataList();
                MyModelUtil.makeOneToOneRelation(
                        modelClass, resultList, relationList, relationStruct.relationField.getName());
            } else {
                this.logErrorOrThrowException(result.getErrorMessage());
            }
        }
    }

    /**
     * 为实体对象数据集成远程一对一关联数据。
     *
     * @param dataObject   实体对象。
     * @param withDict     关联从表数据后，是否把从表的字典数据也一起关联了。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    private <T extends M> void buildRemoteOneToOneForData(T dataObject, boolean withDict, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.remoteRelationOneToOneStructList)) {
            return;
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationOneToOneStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object id = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (id != null) {
                MyQueryParam queryParam = new MyQueryParam(withDict);
                queryParam.setUseDataFilter(false);
                MyWhereCriteria whereCriteria = new MyWhereCriteria();
                whereCriteria.setCriteria(
                        relationStruct.relationOneToOne.slaveIdField(), MyWhereCriteria.OPERATOR_EQUAL, id);
                queryParam.addCriteriaList(whereCriteria);
                ResponseResult<Object> result = relationStruct.remoteClient.getBy(queryParam);
                if (result.isSuccess()) {
                    Object relationObject = this.normalizeData(
                            result.getData(), relationStruct.relationOneToOne.slaveModelClass());
                    if (relationObject != null) {
                        ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, relationObject);
                    }
                } else {
                    this.logErrorOrThrowException(result.getErrorMessage());
                }
            }
        }
    }

    /**
     * 为实体对象参数列表数据集成远程字典关联数据。
     *
     * @param resultList       实体对象数据列表。
     * @param hasBuiltOneToOne 性能优化参数。如果该值为true，同时注解参数RelationDict.equalOneToOneRelationField
     *                         不为空，则直接从已经完成一对一数据关联的从表对象中获取数据，减少一次数据库交互。
     * @param ignoreFields     该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    private void buildRemoteDictForDataList(List<M> resultList, boolean hasBuiltOneToOne, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.remoteRelationDictStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationDictStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            List<Object> relationList = null;
            if (hasBuiltOneToOne && relationStruct.equalOneToOneRelationField != null) {
                relationList = resultList.stream()
                        .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.equalOneToOneRelationField))
                        .filter(Objects::nonNull)
                        .collect(toList());
            } else {
                Set<Object> masterIdSet = resultList.stream()
                        .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                        .filter(Objects::nonNull)
                        .collect(toSet());
                if (CollectionUtils.isEmpty(masterIdSet)) {
                    continue;
                }
                MyQueryParam queryParam = new MyQueryParam(false);
                queryParam.setUseDataFilter(false);
                MyWhereCriteria whereCriteria = new MyWhereCriteria();
                whereCriteria.setCriteria(
                        relationStruct.relationDict.slaveIdField(), MyWhereCriteria.OPERATOR_IN, masterIdSet);
                queryParam.addCriteriaList(whereCriteria);
                ResponseResult<MyPageData<Object>> result = relationStruct.remoteClient.listBy(queryParam);
                // 成功或者没有数据
                if (result.isSuccess()) {
                    relationList = result.getData().getDataList();
                } else {
                    logErrorOrThrowException(result.getErrorMessage());
                }
            }
            MyModelUtil.makeDictRelation(
                    modelClass, resultList, relationList, relationStruct.relationField.getName());
        }
    }

    /**
     * 为实体对象数据集成远程字典关联数据。
     *
     * @param dataObject       实体对象。
     * @param hasBuiltOneToOne 性能优化参数。如果该值为true，同时注解参数RelationDict.equalOneToOneRelationField
     *                         不为空，则直接从已经完成一对一数据关联的从表对象中获取数据，减少一次数据库交互。
     * @param ignoreFields     该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    private <T extends M> void buildRemoteDictForData(T dataObject, boolean hasBuiltOneToOne, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.remoteRelationDictStructList)) {
            return;
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationDictStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object relationObject = null;
            if (hasBuiltOneToOne && relationStruct.equalOneToOneRelationField != null) {
                relationObject = ReflectUtil.getFieldValue(dataObject, relationStruct.equalOneToOneRelationField);
            } else {
                Object id = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
                if (id == null) {
                    continue;
                }
                MyQueryParam queryParam = new MyQueryParam(false);
                queryParam.setUseDataFilter(false);
                MyWhereCriteria whereCriteria = new MyWhereCriteria();
                whereCriteria.setCriteria(
                        relationStruct.relationDict.slaveIdField(), MyWhereCriteria.OPERATOR_EQUAL, id);
                queryParam.addCriteriaList(whereCriteria);
                ResponseResult<Object> result = relationStruct.remoteClient.getBy(queryParam);
                if (result.isSuccess()) {
                    relationObject = this.normalizeData(
                            result.getData(), relationStruct.relationDict.slaveModelClass());
                } else {
                    this.logErrorOrThrowException(result.getErrorMessage());
                }
            }
            MyModelUtil.makeDictRelation(
                    modelClass, dataObject, relationObject, relationStruct.relationField.getName());
        }
    }

    /**
     * 根据实体对象参数列表和过滤条件，集成远程一对多关联聚合计算数据。
     *
     * @param resultList      实体对象数据列表。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildRemoteOneToManyAggregationForDataList(
            List<M> resultList, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.remoteRelationOneToManyAggrStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(this.remoteRelationOneToManyAggrStructList.size());
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationOneToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Set<Object> masterIdSet = resultList.stream()
                    .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                    .filter(Objects::nonNull)
                    .collect(toSet());
            if (CollectionUtils.isEmpty(masterIdSet)) {
                continue;
            }
            RelationOneToManyAggregation relation = relationStruct.relationOneToManyAggregation;
            MyAggregationParam aggregationParam =
                    createAggregationParam(AggregationKind.ONE_TO_MANY,
                            relation.aggregationType(), relation.aggregationField(), relation.slaveIdField());
            List<MyWhereCriteria> criteriaList =
                    criteriaListMap.get(relationStruct.relationField.getName());
            if (criteriaList == null) {
                criteriaList = new LinkedList<>();
            }
            MyWhereCriteria criteria = new MyWhereCriteria();
            CallResult result = criteria.setCriteria(
                    relation.slaveIdField(), MyWhereCriteria.OPERATOR_IN, masterIdSet);
            if (!result.isSuccess()) {
                log.error("过滤条件设置失败，错误：" + result.getErrorMessage());
                throw new IllegalStateException(result.getErrorMessage());
            }
            criteriaList.add(criteria);
            aggregationParam.setWhereCriteriaList(criteriaList);
            aggregationParam.setUseDataFilter(false);
            ResponseResult<List<Map<String, Object>>> responseResult =
                    relationStruct.remoteClient.aggregateBy(aggregationParam);
            if (responseResult.isSuccess()) {
                this.doMakeAggregationData(responseResult.getData(), resultList, relationStruct);
            } else {
                this.logErrorOrThrowException(result.getErrorMessage());
            }
        }
    }

    /**
     * 根据实体对象和过滤条件，集成远程一对多关联聚合计算数据。
     *
     * @param dataObject      实体对象。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private <T extends M> void buildRemoteOneToManyAggregationForData(
            T dataObject, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        // 处理一对多场景
        if (dataObject == null || CollectionUtils.isEmpty(this.remoteRelationOneToManyAggrStructList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(this.remoteRelationOneToManyAggrStructList.size());
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationOneToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object masterIdValue = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (masterIdValue == null) {
                continue;
            }
            RelationOneToManyAggregation relation = relationStruct.relationOneToManyAggregation;
            MyAggregationParam aggregationParam = createAggregationParam(AggregationKind.ONE_TO_MANY,
                    relation.aggregationType(), relation.aggregationField(), relation.slaveIdField());
            List<MyWhereCriteria> criteriaList = criteriaListMap.get(relationStruct.relationField.getName());
            if (criteriaList == null) {
                criteriaList = new LinkedList<>();
            }
            MyWhereCriteria criteria = new MyWhereCriteria();
            criteria.setCriteria(relation.slaveModelClass(),
                    relation.slaveIdField(), MyWhereCriteria.OPERATOR_EQUAL, masterIdValue);
            criteriaList.add(criteria);
            aggregationParam.setWhereCriteriaList(criteriaList);
            aggregationParam.setUseDataFilter(false);
            ResponseResult<List<Map<String, Object>>> result =
                    relationStruct.remoteClient.aggregateBy(aggregationParam);
            if (result.isSuccess()) {
                List<M> resultList = new LinkedList<>();
                resultList.add(dataObject);
                this.doMakeAggregationData(result.getData(), resultList, relationStruct);
            } else {
                this.logErrorOrThrowException(result.getErrorMessage());
            }
        }
    }

    /**
     * 根据实体对象参数列表和过滤条件，集成远程多对多关联聚合计算数据。
     *
     * @param resultList      实体对象数据列表。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildRemoteManyToManyAggregationForDataList(
            List<M> resultList, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.remoteRelationManyToManyAggrStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(this.remoteRelationManyToManyAggrStructList.size());
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationManyToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Set<Object> masterIdSet = resultList.stream()
                    .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                    .filter(Objects::nonNull)
                    .collect(toSet());
            if (CollectionUtils.isEmpty(masterIdSet)) {
                continue;
            }
            RelationManyToManyAggregation relation = relationStruct.relationManyToManyAggregation;
            // 这里需要拆分出哪些是关联表过滤，哪些是从表过滤。
            RemoteAggregationRelationInfo relationInfo = this.parseRemoteAggregationRelationInfo(
                    relationStruct, criteriaListMap, masterIdSet, true);
            // 先处理聚合字段位于中间表的case。
            if (relation.aggregationModelClass().equals(relation.relationModelClass())) {
                this.processRemoteManyToManyAggregationWithRelationModel(
                        relationInfo, relation, relationStruct, resultList, masterIdSet, true);
            } else {
                this.processRemoteManyToManyAggregationWithSlaveModel(
                        relationInfo, relation, relationStruct, resultList);
            }
        }
    }

    /**
     * 根据实体对象和过滤条件，集成本地多对多关联聚合计算数据。
     *
     * @param dataObject      实体对象。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private <T extends M> void buildRemoteManyToManyAggregationForData(
            T dataObject, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.remoteRelationManyToManyAggrStructList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(this.remoteRelationManyToManyAggrStructList.size());
        }
        for (RemoteRelationStruct relationStruct : this.remoteRelationManyToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object masterIdValue = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (masterIdValue == null) {
                continue;
            }
            RelationManyToManyAggregation relation = relationStruct.relationManyToManyAggregation;
            // 这里需要拆分出哪些是关联表过滤，哪些是从表过滤。
            RemoteAggregationRelationInfo relationInfo = this.parseRemoteAggregationRelationInfo(
                    relationStruct, criteriaListMap, masterIdValue, false);
            List<M> resultList = new LinkedList<>();
            resultList.add(dataObject);
            // 先处理聚合字段位于中间表的case。
            if (relation.aggregationModelClass().equals(relation.relationModelClass())) {
                this.processRemoteManyToManyAggregationWithRelationModel(
                        relationInfo, relation, relationStruct, resultList, masterIdValue, false);
            } else {
                this.processRemoteManyToManyAggregationWithSlaveModel(
                        relationInfo, relation, relationStruct, resultList);
            }
        }
    }

    /**
     * 为实体对象参数列表数据集成本地一对一关联数据。
     * NOTE: 当参数withDict为true时，由于一对一从表对象可能关联远程字典，如从表是Teacher对象，
     *       Teacher对象包含gradeId字段，而该字段关联的Grade对象与Teacher对象位于不同的服务。
     *       此时，在关联gradeId远程字典时，就可能产生RPC调用。参数ignoreRpcError应用于该远程
     *       调用。
     *
     * @param resultList   实体对象数据列表。
     * @param withDict     关联从表数据后，是否把从表的字典数据也一起关联了。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    private void buildOneToOneForDataList(List<M> resultList, boolean withDict, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.localRelationOneToOneStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.localRelationOneToOneStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Set<Object> masterIdSet = resultList.stream()
                    .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                    .filter(Objects::nonNull)
                    .collect(toSet());
            // 从主表集合中，抽取主表关联字段的集合，再以in list形式去从表中查询。
            if (CollectionUtils.isEmpty(masterIdSet)) {
                continue;
            }
            BaseService<Object, Serializable> relationService = relationStruct.localService;
            List<Object> relationList =
                    relationService.getInList(relationStruct.relationOneToOne.slaveIdField(), masterIdSet);
            MyModelUtil.makeOneToOneRelation(
                    modelClass, resultList, relationList, relationStruct.relationField.getName());
            // 仅仅当需要加载从表字典关联时，才去加载。
            if (withDict && relationStruct.relationOneToOne.loadSlaveDict()
                    && CollectionUtils.isNotEmpty(relationList)) {
                @SuppressWarnings("unchecked")
                BaseService<Object, Serializable> proxyTarget =
                        (BaseService<Object, Serializable>) AopTargetUtil.getTarget(relationService);
                // 关联常量字典
                proxyTarget.buildConstDictForDataList(relationList, ignoreFields);
                // 关联本地字典。
                proxyTarget.buildDictForDataList(relationList, false, ignoreFields);
                // 关联远程字典。
                proxyTarget.buildRemoteDictForDataList(relationList, false, ignoreFields);
            }
        }
    }

    /**
     * 为实体对象数据集成本地一对一关联数据。
     *
     * @param dataObject   实体对象。
     * @param withDict     关联从表数据后，是否把从表的字典数据也一起关联了。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @throws RemoteDataBuildException ignoreRpcError()方法返回false，同时远程服务调用出现错误时抛出此异常。
     */
    private void buildOneToOneForData(M dataObject, boolean withDict, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.localRelationOneToOneStructList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.localRelationOneToOneStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object id = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (id != null) {
                BaseService<Object, Serializable> relationService = relationStruct.localService;
                Object relationObject = relationService.getOne(relationStruct.relationOneToOne.slaveIdField(), id);
                ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, relationObject);
                // 仅仅当需要加载从表字典关联时，才去加载。
                if (withDict && relationStruct.relationOneToOne.loadSlaveDict() && relationObject != null) {
                    @SuppressWarnings("unchecked")
                    BaseService<Object, Serializable> proxyTarget =
                            (BaseService<Object, Serializable>) AopTargetUtil.getTarget(relationService);
                    // 关联常量字典
                    proxyTarget.buildConstDictForData(relationObject, ignoreFields);
                    // 关联本地字典。
                    proxyTarget.buildDictForData(relationObject, false, ignoreFields);
                    // 关联远程字典。
                    proxyTarget.buildRemoteDictForData(relationObject, false, ignoreFields);
                }
            }
        }
    }

    /**
     * 为实体对象参数列表数据集成本地一对多关联数据。
     *
     * @param resultList   实体对象数据列表。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildOneToManyForDataList(List<M> resultList, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.localRelationOneToManyStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.localRelationOneToManyStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Set<Object> masterIdSet = resultList.stream()
                    .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                    .filter(Objects::nonNull)
                    .collect(toSet());
            // 从主表集合中，抽取主表关联字段的集合，再以in list形式去从表中查询。
            if (CollectionUtils.isNotEmpty(masterIdSet)) {
                BaseService<Object, Serializable> relationService = relationStruct.localService;
                List<Object> relationList =
                        relationService.getInList(relationStruct.relationOneToMany.slaveIdField(), masterIdSet);
                MyModelUtil.makeOneToManyRelation(
                        modelClass, resultList, relationList, relationStruct.relationField.getName());
            }
        }
    }

    /**
     * 为实体对象数据集成本地一对多关联数据。
     *
     * @param dataObject   实体对象。
     * @param ignoreFields 该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildOneToManyForData(M dataObject, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.localRelationOneToManyStructList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.localRelationOneToManyStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object id = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (id != null) {
                BaseService<Object, Serializable> relationService = relationStruct.localService;
                Set<Object> masterIdSet = new HashSet<>(1);
                masterIdSet.add(id);
                List<Object> relationObject = relationService.getInList(
                        relationStruct.relationOneToMany.slaveIdField(), masterIdSet);
                ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, relationObject);
            }
        }
    }

    /**
     * 为实体对象参数列表数据集成本地字典关联数据。
     *
     * @param resultList       实体对象数据列表。
     * @param hasBuiltOneToOne 性能优化参数。如果该值为true，同时注解参数RelationDict.equalOneToOneRelationField
     *                         不为空，则直接从已经完成一对一数据关联的从表对象中获取数据，减少一次数据库交互。
     * @param ignoreFields     该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildDictForDataList(List<M> resultList, boolean hasBuiltOneToOne, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.localRelationDictStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.localRelationDictStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            List<Object> relationList = null;
            if (hasBuiltOneToOne && relationStruct.equalOneToOneRelationField != null) {
                relationList = resultList.stream()
                        .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.equalOneToOneRelationField))
                        .filter(Objects::nonNull)
                        .collect(toList());
            } else {
                String slaveId = relationStruct.relationDict.slaveIdField();
                Set<Object> masterIdSet = resultList.stream()
                        .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                        .filter(Objects::nonNull)
                        .collect(toSet());
                if (CollectionUtils.isNotEmpty(masterIdSet)) {
                    relationList = relationStruct.localService.getInList(slaveId, masterIdSet);
                }
            }
            MyModelUtil.makeDictRelation(
                    modelClass, resultList, relationList, relationStruct.relationField.getName());
        }
    }

    /**
     * 为实体对象数据集成本地数据字典关联数据。
     *
     * @param dataObject       实体对象。
     * @param hasBuiltOneToOne 性能优化参数。如果该值为true，同时注解参数RelationDict.equalOneToOneRelationField
     *                         不为空，则直接从已经完成一对一数据关联的从表对象中获取数据，减少一次数据库交互。
     * @param ignoreFields     该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private <T extends M> void buildDictForData(T dataObject, boolean hasBuiltOneToOne, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.localRelationDictStructList)) {
            return;
        }
        for (LocalRelationStruct relationStruct : this.localRelationDictStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object relationObject = null;
            if (hasBuiltOneToOne && relationStruct.equalOneToOneRelationField != null) {
                relationObject = ReflectUtil.getFieldValue(dataObject, relationStruct.equalOneToOneRelationField);
            } else {
                Object id = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
                if (id != null) {
                    relationObject = relationStruct.localService.getOne(relationStruct.relationDict.slaveIdField(), id);
                }
            }
            MyModelUtil.makeDictRelation(
                    modelClass, dataObject, relationObject, relationStruct.relationField.getName());
        }
    }

    /**
     * 根据实体对象参数列表和过滤条件，集成本地多对多关联聚合计算数据。
     *
     * @param resultList      实体对象数据列表。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildManyToManyAggregationForDataList(
            List<M> resultList, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.localRelationManyToManyAggrStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(this.localRelationManyToManyAggrStructList.size());
        }
        for (LocalRelationStruct relationStruct : this.localRelationManyToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Set<Object> masterIdSet = resultList.stream()
                    .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                    .filter(Objects::nonNull)
                    .collect(toSet());
            if (CollectionUtils.isEmpty(masterIdSet)) {
                continue;
            }
            RelationManyToManyAggregation relation = relationStruct.relationManyToManyAggregation;
            // 提取关联中用到的各种字段和表数据。
            LocalAggregationRelationInfo basicRelationInfo =
                    this.parseLocalAggregationRelationInfo(relationStruct, criteriaListMap);
            // 构建多表关联的where语句
            StringBuilder whereClause = new StringBuilder(256);
            // 如果需要从表聚合计算或参与过滤，则需要把中间表和从表之间的关联条件加上。
            if (!basicRelationInfo.onlySelectRelationTable) {
                whereClause.append(basicRelationInfo.relationTable)
                        .append(".")
                        .append(basicRelationInfo.relationSlaveColumn)
                        .append(" = ")
                        .append(basicRelationInfo.slaveTable)
                        .append(".")
                        .append(basicRelationInfo.slaveColumn);
            } else {
                whereClause.append("1 = 1");
            }
            List<MyWhereCriteria> criteriaList = criteriaListMap.get(relationStruct.relationField.getName());
            if (criteriaList == null) {
                criteriaList = new LinkedList<>();
            }
            MyWhereCriteria inlistFilter = new MyWhereCriteria();
            inlistFilter.setCriteria(relation.relationModelClass(),
                    relation.relationMasterIdField(), MyWhereCriteria.OPERATOR_IN, masterIdSet);
            criteriaList.add(inlistFilter);
            if (StringUtils.isNotBlank(relationStruct.localService.deletedFlagFieldName)) {
                MyWhereCriteria deleteFilter = new MyWhereCriteria();
                deleteFilter.setCriteria(
                        relation.slaveModelClass(),
                        relationStruct.localService.deletedFlagFieldName,
                        MyWhereCriteria.OPERATOR_EQUAL,
                        GlobalDeletedFlag.NORMAL);
                criteriaList.add(deleteFilter);
            }
            String criteriaString = MyWhereCriteria.makeCriteriaString(criteriaList);
            whereClause.append(AND_OP).append(criteriaString);
            StringBuilder tableNames = new StringBuilder(64);
            tableNames.append(basicRelationInfo.relationTable);
            if (!basicRelationInfo.onlySelectRelationTable) {
                tableNames.append(", ").append(basicRelationInfo.slaveTable);
            }
            List<Map<String, Object>> aggregationMapList =
                    mapper().getGroupedListByCondition(tableNames.toString(),
                            basicRelationInfo.selectList, whereClause.toString(), basicRelationInfo.groupBy);
            doMakeAggregationData(aggregationMapList, resultList, relationStruct);
        }
    }

    /**
     * 根据实体对象参数列表和过滤条件，集成本地一对多关联聚合计算数据。
     *
     * @param resultList      实体对象数据列表。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private void buildOneToManyAggregationForDataList(
            List<M> resultList, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        if (CollectionUtils.isEmpty(this.localRelationOneToManyAggrStructList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(localRelationOneToManyAggrStructList.size());
        }
        for (LocalRelationStruct relationStruct : this.localRelationOneToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Set<Object> masterIdSet = resultList.stream()
                    .map(obj -> ReflectUtil.getFieldValue(obj, relationStruct.masterIdField))
                    .filter(Objects::nonNull)
                    .collect(toSet());
            if (CollectionUtils.isNotEmpty(masterIdSet)) {
                RelationOneToManyAggregation relation = relationStruct.relationOneToManyAggregation;
                // 开始获取后面所需的各种关联数据。此部分今后可以移植到缓存中，无需每次计算。
                String slaveTable = MyModelUtil.mapToTableName(relation.slaveModelClass());
                String slaveColumnName = MyModelUtil.mapToColumnName(
                        relation.slaveIdField(), relation.slaveModelClass());
                Tuple2<String, String> selectAndGroupByTuple = makeSelectListAndGroupByClause(
                        slaveTable, slaveColumnName, relation.slaveModelClass(),
                        slaveTable, relation.aggregationField(), relation.aggregationType());
                String selectList = selectAndGroupByTuple.getFirst();
                String groupBy = selectAndGroupByTuple.getSecond();
                List<MyWhereCriteria> criteriaList = criteriaListMap.get(relationStruct.relationField.getName());
                if (criteriaList == null) {
                    criteriaList = new LinkedList<>();
                }
                MyWhereCriteria inlistFilter = new MyWhereCriteria();
                inlistFilter.setCriteria(relation.slaveModelClass(),
                        relation.slaveIdField(), MyWhereCriteria.OPERATOR_IN, masterIdSet);
                criteriaList.add(inlistFilter);
                if (StringUtils.isNotBlank(relationStruct.localService.deletedFlagFieldName)) {
                    MyWhereCriteria deleteFilter = new MyWhereCriteria();
                    deleteFilter.setCriteria(
                            relation.slaveModelClass(),
                            relationStruct.localService.deletedFlagFieldName,
                            MyWhereCriteria.OPERATOR_EQUAL,
                            GlobalDeletedFlag.NORMAL);
                    criteriaList.add(deleteFilter);
                }
                String criteriaString = MyWhereCriteria.makeCriteriaString(criteriaList);
                List<Map<String, Object>> aggregationMapList =
                        mapper().getGroupedListByCondition(slaveTable, selectList, criteriaString, groupBy);
                doMakeAggregationData(aggregationMapList, resultList, relationStruct);
            }
        }
    }

    /**
     * 根据实体对象和过滤条件，集成本地多对多关联聚合计算数据。
     *
     * @param dataObject      实体对象。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private <T extends M> void buildManyToManyAggregationForData(
            T dataObject, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.localRelationManyToManyAggrStructList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(localRelationManyToManyAggrStructList.size());
        }
        for (LocalRelationStruct relationStruct : this.localRelationManyToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object masterIdValue = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (masterIdValue != null) {
                LocalAggregationRelationInfo basicRelationInfo =
                        this.parseLocalAggregationRelationInfo(relationStruct, criteriaListMap);
                // 组装过滤条件
                String whereClause = this.makeManyToManyWhereClause(
                        relationStruct, masterIdValue, basicRelationInfo, criteriaListMap);
                StringBuilder tableNames = new StringBuilder(64);
                tableNames.append(basicRelationInfo.relationTable);
                if (!basicRelationInfo.onlySelectRelationTable) {
                    tableNames.append(", ").append(basicRelationInfo.slaveTable);
                }
                List<Map<String, Object>> aggregationMapList =
                        mapper().getGroupedListByCondition(tableNames.toString(),
                                basicRelationInfo.selectList, whereClause, basicRelationInfo.groupBy);
                // 将查询后的结果回填到主表数据中。
                if (CollectionUtils.isNotEmpty(aggregationMapList)) {
                    Object value = aggregationMapList.get(0).get(MyAggregationParam.VALUE_NAME);
                    if (value != null) {
                        ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, value);
                    }
                }
            }
        }
    }

    /**
     * 根据实体对象和过滤条件，集成本地一对多关联聚合计算数据。
     *
     * @param dataObject      实体对象。
     * @param criteriaListMap 过滤参数。key为主表字段名称，value是过滤条件列表。
     * @param ignoreFields    该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    private <T extends M> void buildOneToManyAggregationForData(
            T dataObject, Map<String, List<MyWhereCriteria>> criteriaListMap, Set<String> ignoreFields) {
        if (dataObject == null || CollectionUtils.isEmpty(this.localRelationOneToManyAggrStructList)) {
            return;
        }
        if (criteriaListMap == null) {
            criteriaListMap = new HashMap<>(localRelationOneToManyAggrStructList.size());
        }
        for (LocalRelationStruct relationStruct : this.localRelationOneToManyAggrStructList) {
            if (ignoreFields != null && ignoreFields.contains(relationStruct.relationField.getName())) {
                continue;
            }
            Object masterIdValue = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
            if (masterIdValue != null) {
                RelationOneToManyAggregation relation = relationStruct.relationOneToManyAggregation;
                String slaveTable = MyModelUtil.mapToTableName(relation.slaveModelClass());
                String slaveColumnName =
                        MyModelUtil.mapToColumnName(relation.slaveIdField(), relation.slaveModelClass());
                Tuple2<String, String> selectAndGroupByTuple = makeSelectListAndGroupByClause(
                        slaveTable, slaveColumnName, relation.slaveModelClass(),
                        slaveTable, relation.aggregationField(), relation.aggregationType());
                String selectList = selectAndGroupByTuple.getFirst();
                String groupBy = selectAndGroupByTuple.getSecond();
                String whereClause = this.makeOneToManyWhereClause(
                        relationStruct, masterIdValue, slaveColumnName, criteriaListMap);
                // 获取分组聚合计算结果
                List<Map<String, Object>> aggregationMapList =
                        mapper().getGroupedListByCondition(slaveTable, selectList, whereClause, groupBy);
                // 将计算结果回填到主表关联字段
                if (CollectionUtils.isNotEmpty(aggregationMapList)) {
                    Object value = aggregationMapList.get(0).get(MyAggregationParam.VALUE_NAME);
                    if (value != null) {
                        ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, value);
                    }
                }
            }
        }
    }

    /**
     * 仅仅在spring boot 启动后的监听器事件中调用，缓存所有远程调用Client的关联关系，加速后续的数据绑定效率。
     */
    @Override
    public void loadRemoteRelationStruct() {
        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field f : fields) {
            initializeRemoteRelationDictStruct(f);
            initializeRemoteRelationStruct(f);
            initializeRemoteRelationAggregationStruct(f);
        }
    }

    /**
     * 仅仅在spring boot 启动后的监听器事件中调用，缓存所有service的关联关系，加速后续的数据绑定效率。
     */
    @Override
    public void loadLocalRelationStruct() {
        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field f : fields) {
            initializeLocalRelationDictStruct(f);
            initializeLocalRelationStruct(f);
            initializeLocalRelationAggregationStruct(f);
        }
    }

    /**
     * 缺省实现返回null，在进行一对多和多对多聚合计算时，没有额外的自定义过滤条件。如有需要，需子类自行实现。
     *
     * @return 自定义过滤条件列表。
     */
    protected Map<String, List<MyWhereCriteria>> buildAggregationAdditionalWhereCriteria() {
        return null;
    }

    /**
     * 判断当前对象的关联字段数据是否需要被验证，如果原有对象为null，表示新对象第一次插入，则必须验证。
     *
     * @param object         新对象。
     * @param originalObject 原有对象。
     * @param fieldGetter    获取需要验证字段的函数对象。
     * @param <T>            需要验证字段的类型。
     * @return 需要关联验证返回true，否则false。
     */
    protected <T> boolean needToVerify(M object, M originalObject, Function<M, T> fieldGetter) {
        if (object == null) {
            return false;
        }
        T data = fieldGetter.apply(object);
        if (data == null) {
            return false;
        }
        if (data instanceof String) {
            String stringData = (String) data;
            if (stringData.length() == 0) {
                return false;
            }
        }
        if (originalObject == null) {
            return true;
        }
        T originalData = fieldGetter.apply(originalObject);
        return !data.equals(originalData);
    }

    /**
     * 判断远程关联数据是否包含错误信息。
     *
     * @param responseResult 远程服务返回结果对象。
     * @return true表示包含错误信息，否则false。
     */
    protected boolean hasErrorOfVerifyRemoteRelatedData(ResponseResult<Boolean> responseResult) {
        return !responseResult.isSuccess() || Boolean.FALSE.equals(responseResult.getData());
    }

    /**
     * 因为Mybatis Plus中QueryWrapper的条件方法都要求传入数据表字段名，因此提供该函数将
     * Java实体对象的字段名转换为数据表字段名，如果不存在会抛出异常。
     * 另外在MyModelUtil.mapToColumnName有一级缓存，对于查询过的对象字段都会放到缓存中，
     * 下次映射转换的时候，会直接从缓存获取。
     *
     * @param fieldName Java实体对象的字段名。
     * @return 对应的数据表字段名。
     */
    protected String safeMapToColumnName(String fieldName) {
        String columnName = MyModelUtil.mapToColumnName(fieldName, modelClass);
        if (columnName == null) {
            throw new InvalidDataFieldException(modelClass.getSimpleName(), fieldName);
        }
        return columnName;
    }

    /**
     * 因为Mybatis Plus在update的时候，不能将实体对象中值为null的字段，更新为null，
     * 而且忽略更新，在全部更新场景下，这个是非常重要的，所以我们写了这个函数绕开这一问题。
     * 该函数会遍历实体对象中，所有不包含@Transient注解，没有transient修饰符的字段，如果
     * 当前对象的该字段值为null，则会调用UpdateWrapper的set方法，将该字段赋值为null。
     * 相比于其他重载方法，该方法会将参数中的主键id，设置到UpdateWrapper的过滤条件中。
     *
     * @param o  实体对象。
     * @param id 实体对象的主键值。
     * @return 创建后的UpdateWrapper。
     */
    protected UpdateWrapper<M> createUpdateQueryForNullValue(M o, K id) {
        UpdateWrapper<M> uw = createUpdateQueryForNullValue(o, modelClass);
        try {
            M filter = modelClass.newInstance();
            this.setIdFieldMethod.invoke(filter, id);
            uw.setEntity(filter);
        } catch (Exception e) {
            log.error("Failed to call reflection code of BaseService.createUpdateQueryForNullValue.", e);
            throw new MyRuntimeException(e);
        }
        return uw;
    }

    /**
     * 因为Mybatis Plus在update的时候，不能将实体对象中值为null的字段，更新为null，
     * 而且忽略更新，在全部更新场景下，这个是非常重要的，所以我们写了这个函数绕开这一问题。
     * 该函数会遍历实体对象中，所有不包含@Transient注解，没有transient修饰符的字段，如果
     * 当前对象的该字段值为null，则会调用UpdateWrapper的set方法，将该字段赋值为null。
     *
     * @param o 实体对象。
     * @return 创建后的UpdateWrapper。
     */
    protected UpdateWrapper<M> createUpdateQueryForNullValue(M o) {
        return createUpdateQueryForNullValue(o, modelClass);
    }

    /**
     * 因为Mybatis Plus在update的时候，不能将实体对象中值为null的字段，更新为null，
     * 而且忽略更新，在全部更新场景下，这个是非常重要的，所以我们写了这个函数绕开这一问题。
     * 该函数会遍历实体对象中，所有不包含@Transient注解，没有transient修饰符的字段，如果
     * 当前对象的该字段值为null，则会调用UpdateWrapper的set方法，将该字段赋值为null。
     *
     * @param o     实体对象。
     * @param clazz 实体对象的class。
     * @return 创建后的UpdateWrapper。
     */
    public static <T> UpdateWrapper<T> createUpdateQueryForNullValue(T o, Class<T> clazz) {
        UpdateWrapper<T> uw = new UpdateWrapper<>();
        Field[] fields = ReflectUtil.getFields(clazz);
        List<String> nullColumnList = new LinkedList<>();
        for (Field field : fields) {
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField == null || tableField.exist()) {
                int modifiers = field.getModifiers();
                // transient类型的字段不能作为查询条件，静态字段和逻辑删除都不考虑。
                int transientMask = 128;
                if ((modifiers & transientMask) == 1
                        || Modifier.isStatic(modifiers)
                        || field.getAnnotation(TableLogic.class) != null) {
                    continue;
                }
                // 仅当实体对象参数中，当前字段值为null的时候，才会赋值给UpdateWrapper。
                // 以便在后续的更新中，可以将这些null字段的值设置到数据库表对应的字段中。
                if (ReflectUtil.getFieldValue(o, field) == null) {
                    nullColumnList.add(MyModelUtil.safeMapToColumnName(field.getName(), clazz));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(nullColumnList)) {
            for (String nullColumn : nullColumnList) {
                uw.set(nullColumn, null);
            }
        }
        return uw;
    }

    @SuppressWarnings("unchecked")
    private void initializeRemoteRelationDictStruct(Field f) {
        RelationDict relationDict = f.getAnnotation(RelationDict.class);
        if (relationDict != null) {
            if (relationDict.slaveClientClass().equals(DummyClass.class)) {
                return;
            }
            RemoteRelationStruct relationStruct = new RemoteRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationDict.masterIdField());
            relationStruct.relationDict = relationDict;
            if (StringUtils.isNotBlank(relationDict.equalOneToOneRelationField())) {
                relationStruct.equalOneToOneRelationField =
                        ReflectUtil.getField(modelClass, relationDict.equalOneToOneRelationField());
            }
            Object client = ApplicationContextHolder.getBean(relationDict.slaveClientClass());
            relationStruct.remoteClient = (BaseClient<Object, Object, Object>) client;
            remoteRelationDictStructList.add(relationStruct);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeRemoteRelationStruct(Field f) {
        RelationOneToOne relationOneToOne = f.getAnnotation(RelationOneToOne.class);
        if (relationOneToOne != null) {
            if (relationOneToOne.slaveClientClass().equals(DummyClass.class)) {
                return;
            }
            RemoteRelationStruct relationStruct = new RemoteRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationOneToOne.masterIdField());
            relationStruct.relationOneToOne = relationOneToOne;
            Object client = ApplicationContextHolder.getBean(relationOneToOne.slaveClientClass());
            relationStruct.remoteClient = (BaseClient<Object, Object, Object>) client;
            remoteRelationOneToOneStructList.add(relationStruct);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeRemoteRelationAggregationStruct(Field f) {
        RelationOneToManyAggregation relationOneToManyAggregation =
                f.getAnnotation(RelationOneToManyAggregation.class);
        if (relationOneToManyAggregation != null) {
            if (relationOneToManyAggregation.slaveClientClass().equals(DummyClass.class)) {
                return;
            }
            RemoteRelationStruct relationStruct = new RemoteRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationOneToManyAggregation.masterIdField());
            relationStruct.relationOneToManyAggregation = relationOneToManyAggregation;
            Object client = ApplicationContextHolder.getBean(relationOneToManyAggregation.slaveClientClass());
            relationStruct.remoteClient = (BaseClient<Object, Object, Object>) client;
            remoteRelationOneToManyAggrStructList.add(relationStruct);
            return;
        }
        RelationManyToManyAggregation relationManyToManyAggregation =
                f.getAnnotation(RelationManyToManyAggregation.class);
        if (relationManyToManyAggregation != null) {
            if (relationManyToManyAggregation.slaveClientClass().equals(DummyClass.class)) {
                return;
            }
            RemoteRelationStruct relationStruct = new RemoteRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(
                    modelClass, relationManyToManyAggregation.masterIdField());
            relationStruct.relationManyToManyAggregation = relationManyToManyAggregation;
            Object client = ApplicationContextHolder.getBean(relationManyToManyAggregation.slaveClientClass());
            relationStruct.remoteClient = (BaseClient<Object, Object, Object>) client;
            remoteRelationManyToManyAggrStructList.add(relationStruct);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeLocalRelationStruct(Field f) {
        RelationOneToOne relationOneToOne = f.getAnnotation(RelationOneToOne.class);
        if (relationOneToOne != null) {
            if (StringUtils.isBlank(relationOneToOne.slaveServiceName())) {
                return;
            }
            LocalRelationStruct relationStruct = new LocalRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationOneToOne.masterIdField());
            relationStruct.relationOneToOne = relationOneToOne;
            if (StringUtils.isNotBlank(relationOneToOne.slaveServiceName())) {
                relationStruct.localService = ApplicationContextHolder.getBean(
                        StringUtils.uncapitalize(relationOneToOne.slaveServiceName()));
            } else {
                relationStruct.localService = (BaseService<Object, Serializable>)
                        ApplicationContextHolder.getBean(relationOneToOne.slaveServiceClass());
            }
            localRelationOneToOneStructList.add(relationStruct);
            return;
        }
        RelationOneToMany relationOneToMany = f.getAnnotation(RelationOneToMany.class);
        if (relationOneToMany != null) {
            LocalRelationStruct relationStruct = new LocalRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationOneToMany.masterIdField());
            relationStruct.relationOneToMany = relationOneToMany;
            if (StringUtils.isNotBlank(relationOneToMany.slaveServiceName())) {
                relationStruct.localService = ApplicationContextHolder.getBean(
                        StringUtils.uncapitalize(relationOneToMany.slaveServiceName()));
            } else {
                relationStruct.localService = (BaseService<Object, Serializable>)
                        ApplicationContextHolder.getBean(relationOneToMany.slaveServiceClass());
            }
            localRelationOneToManyStructList.add(relationStruct);
            return;
        }
        RelationManyToMany relationManyToMany = f.getAnnotation(RelationManyToMany.class);
        if (relationManyToMany != null) {
            LocalRelationStruct relationStruct = new LocalRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationManyToMany.relationMasterIdField());
            relationStruct.relationManyToMany = relationManyToMany;
            relationStruct.manyToManyMapper = ApplicationContextHolder.getBean(
                    StringUtils.uncapitalize(relationManyToMany.relationMapperName()));
            localRelationManyToManyStructList.add(relationStruct);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeLocalRelationDictStruct(Field f) {
        RelationConstDict relationConstDict = f.getAnnotation(RelationConstDict.class);
        if (relationConstDict != null) {
            LocalRelationStruct relationStruct = new LocalRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationConstDict.masterIdField());
            Field dictMapField = ReflectUtil.getField(relationConstDict.constantDictClass(), "DICT_MAP");
            relationStruct.dictMap = (Map<Object, String>) ReflectUtil.getFieldValue(modelClass, dictMapField);
            relationConstDictStructList.add(relationStruct);
            return;
        }
        RelationDict relationDict = f.getAnnotation(RelationDict.class);
        if (relationDict != null) {
            if (StringUtils.isBlank(relationDict.slaveServiceName())) {
                return;
            }
            LocalRelationStruct relationStruct = new LocalRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationDict.masterIdField());
            relationStruct.relationDict = relationDict;
            if (StringUtils.isNotBlank(relationDict.equalOneToOneRelationField())) {
                relationStruct.equalOneToOneRelationField =
                        ReflectUtil.getField(modelClass, relationDict.equalOneToOneRelationField());
            }
            if (StringUtils.isNotBlank(relationDict.slaveServiceName())) {
                relationStruct.localService =
                        ApplicationContextHolder.getBean(StringUtils.uncapitalize(relationDict.slaveServiceName()));
            } else {
                relationStruct.localService = (BaseService<Object, Serializable>)
                        ApplicationContextHolder.getBean(relationDict.slaveServiceClass());
            }
            localRelationDictStructList.add(relationStruct);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeLocalRelationAggregationStruct(Field f) {
        RelationOneToManyAggregation relationOneToManyAggregation = f.getAnnotation(RelationOneToManyAggregation.class);
        if (relationOneToManyAggregation != null) {
            if (!relationOneToManyAggregation.slaveClientClass().equals(DummyClass.class)) {
                return;
            }
            LocalRelationStruct relationStruct = new LocalRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationOneToManyAggregation.masterIdField());
            relationStruct.relationOneToManyAggregation = relationOneToManyAggregation;
            if (StringUtils.isNotBlank(relationOneToManyAggregation.slaveServiceName())) {
                relationStruct.localService = ApplicationContextHolder.getBean(
                        StringUtils.uncapitalize(relationOneToManyAggregation.slaveServiceName()));
            } else {
                relationStruct.localService = (BaseService<Object, Serializable>)
                        ApplicationContextHolder.getBean(relationOneToManyAggregation.slaveServiceClass());
            }
            localRelationOneToManyAggrStructList.add(relationStruct);
            return;
        }
        RelationManyToManyAggregation relationManyToManyAggregation = f.getAnnotation(RelationManyToManyAggregation.class);
        if (relationManyToManyAggregation != null) {
            if (!relationManyToManyAggregation.slaveClientClass().equals(DummyClass.class)) {
                return;
            }
            LocalRelationStruct relationStruct = new LocalRelationStruct();
            relationStruct.relationField = f;
            relationStruct.masterIdField = ReflectUtil.getField(modelClass, relationManyToManyAggregation.masterIdField());
            relationStruct.relationManyToManyAggregation = relationManyToManyAggregation;
            if (StringUtils.isNotBlank(relationManyToManyAggregation.slaveServiceName())) {
                relationStruct.localService = ApplicationContextHolder.getBean(
                        StringUtils.uncapitalize(relationManyToManyAggregation.slaveServiceName()));
            } else {
                relationStruct.localService = (BaseService<Object, Serializable>)
                        ApplicationContextHolder.getBean(relationManyToManyAggregation.slaveServiceClass());
            }
            localRelationManyToManyAggrStructList.add(relationStruct);
        }
    }

    private <T> Object normalizeData(Object data, Class<T> clazz) {
        if (data instanceof JSONObject) {
            return ((JSONObject) data).toJavaObject(clazz);
        }
        return data;
    }

    private void logErrorOrThrowException(String errorMsg) {
        log.error(LogMessageUtil.makeRpcError(errorMsg));
        if (!this.ignoreRpcError()) {
            throw new RemoteDataBuildException(errorMsg);
        }
    }

    private LocalAggregationRelationInfo parseLocalAggregationRelationInfo(
            LocalRelationStruct relationStruct, Map<String, List<MyWhereCriteria>> criteriaListMap) {
        RelationManyToManyAggregation relation = relationStruct.relationManyToManyAggregation;
        LocalAggregationRelationInfo relationInfo = new LocalAggregationRelationInfo();
        // 提取关联中用到的各种字段和表数据。
        relationInfo.slaveTable = MyModelUtil.mapToTableName(relation.slaveModelClass());
        relationInfo.relationTable = MyModelUtil.mapToTableName(relation.relationModelClass());
        relationInfo.relationMasterColumn =
                MyModelUtil.mapToColumnName(relation.relationMasterIdField(), relation.relationModelClass());
        relationInfo.relationSlaveColumn =
                MyModelUtil.mapToColumnName(relation.relationSlaveIdField(), relation.relationModelClass());
        relationInfo.slaveColumn = MyModelUtil.mapToColumnName(relation.slaveIdField(), relation.slaveModelClass());
        // 判断是否只需要关联中间表即可，从而提升查询统计的效率。
        // 1. 统计字段为中间表字段。2. 自定义过滤条件中没有基于从表字段的过滤条件。
        relationInfo.onlySelectRelationTable =
                relation.aggregationModelClass().equals(relation.relationModelClass());
        if (relationInfo.onlySelectRelationTable && MapUtils.isNotEmpty(criteriaListMap)) {
            List<MyWhereCriteria> criteriaList =
                    criteriaListMap.get(relationStruct.relationField.getName());
            if (CollectionUtils.isNotEmpty(criteriaList)) {
                for (MyWhereCriteria whereCriteria : criteriaList) {
                    if (whereCriteria.getModelClazz().equals(relation.slaveModelClass())) {
                        relationInfo.onlySelectRelationTable = false;
                        break;
                    }
                }
            }
        }
        String aggregationTable = relation.aggregationModelClass().equals(relation.relationModelClass())
                ? relationInfo.relationTable : relationInfo.slaveTable;
        Tuple2<String, String> selectAndGroupByTuple = makeSelectListAndGroupByClause(
                relationInfo.relationTable, relationInfo.relationMasterColumn, relation.aggregationModelClass(),
                aggregationTable, relation.aggregationField(), relation.aggregationType());
        relationInfo.selectList = selectAndGroupByTuple.getFirst();
        relationInfo.groupBy = selectAndGroupByTuple.getSecond();
        return relationInfo;
    }

    private RemoteAggregationRelationInfo parseRemoteAggregationRelationInfo(
            RemoteRelationStruct relationStruct,
            Map<String, List<MyWhereCriteria>> criteriaListMap,
            Object value,
            boolean forDataList) {
        RemoteAggregationRelationInfo relationInfo = new RemoteAggregationRelationInfo();
        // 这里需要拆分出哪些是关联表过滤，哪些是从表过滤。
        RelationManyToManyAggregation relation = relationStruct.relationManyToManyAggregation;
        relationInfo.relationTable = MyModelUtil.mapToTableName(relation.relationModelClass());
        relationInfo.relationMasterColumn = MyModelUtil.mapToColumnName(
                relation.relationMasterIdField(), relation.relationModelClass());
        relationInfo.relationSlaveColumn = MyModelUtil.mapToColumnName(
                relation.relationSlaveIdField(), relation.relationModelClass());
        // 这里需要拆分出哪些是关联表过滤，哪些是从表过滤。
        List<MyWhereCriteria> relationCriteriaList = new LinkedList<>();
        // 先把关联表的inlist过滤 [r_table.master_id = masterIdValue] 预先插入。
        MyWhereCriteria criteria = new MyWhereCriteria();
        if (forDataList) {
            criteria.setCriteria(relation.relationModelClass(),
                    relation.relationMasterIdField(), MyWhereCriteria.OPERATOR_IN, value);
        } else {
            criteria.setCriteria(relation.relationModelClass(),
                    relation.relationMasterIdField(), MyWhereCriteria.OPERATOR_EQUAL, value);
        }
        relationCriteriaList.add(criteria);
        List<MyWhereCriteria> criteriaList = criteriaListMap.get(relationStruct.relationField.getName());
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            for (MyWhereCriteria c : criteriaList) {
                if (c.getModelClazz().equals(relation.relationModelClass())) {
                    relationCriteriaList.add(c);
                } else if (c.getModelClazz().equals(relation.slaveModelClass())) {
                    if (relationInfo.slaveCriteriaList == null) {
                        relationInfo.slaveCriteriaList = new LinkedList<>();
                    }
                    relationInfo.slaveCriteriaList.add(c);
                }
            }
        }
        relationInfo.relationWhereClause = MyWhereCriteria.makeCriteriaString(relationCriteriaList);
        return relationInfo;
    }

    private void processRemoteManyToManyAggregationWithRelationModel(
        RemoteAggregationRelationInfo relationInfo, RelationManyToManyAggregation relation,
        RemoteRelationStruct relationStruct, List<M> resultList, Object value, boolean forDataList) {
        // 1. 先构建SQL中的Select List，下面的函数返回值是：
        // relationTable.relationMasterColumn as GROUPED_KEY, SUM(relationTable.aggregationField) AGGREGATED_VALUE
        // 2. 基于中间表与主表的关联字段构建SQL语句中的GROUP BY从句。
        Tuple2<String, String> selectAndGroupByTuple = makeSelectListAndGroupByClause(
                relationInfo.relationTable, relationInfo.relationMasterColumn,
                relation.aggregationModelClass(), relationInfo.relationTable,
                relation.aggregationField(), relation.aggregationType());
        String groupedSelectList = selectAndGroupByTuple.getFirst();
        String groupBy = selectAndGroupByTuple.getSecond();
        List<Map<String, Object>> aggregationMapList = null;
        // 过滤条件中不涉及远程从表，那么可以直接进行基于中间表的SQL计算即可，避免了远程调用。
        if (relationInfo.slaveCriteriaList == null) {
            // 3. 下面的函数调用将会执行仅包含中间表的SQL。
            aggregationMapList = mapper().getGroupedListByCondition(
                    relationInfo.relationTable, groupedSelectList, relationInfo.relationWhereClause, groupBy);
            this.doMakeAggregationData(aggregationMapList, resultList, relationStruct);
            return;
        }
        // 此场景是最复杂的，实现步骤如下：
        // 1. 先基于主表的inlist和中间表的过滤条件，过滤出符合条件的中间表数据列表。
        // 构建获取中间表数据SQL的Select List部分。
        StringBuilder selectList = new StringBuilder(64);
        selectList.append(relationInfo.relationMasterColumn).append(", ").append(relationInfo.relationSlaveColumn);
        // SQL中的Where从句不变，和上面的过滤逻辑是一致的。
        List<Map<String, Object>> mapList = mapper().getListByCondition(
                relationInfo.relationTable, selectList.toString(), relationInfo.relationWhereClause, null);
        if (CollectionUtils.isNotEmpty(mapList)) {
            // 2. 基于中间表的查询结果，即中间表中与从表关联字段的列表作为从表的inlist过滤条件，
            // 同时结合远程从表的自身过滤条件，获取远程从表数据。目前仅需从表中的关联字段即可。
            MyWhereCriteria slaveInlistFilterCriteria = new MyWhereCriteria();
            // 将中间表对从表进行inlist过滤的条件手动插入。
            slaveInlistFilterCriteria.setCriteria(relation.slaveIdField(), MyWhereCriteria.OPERATOR_IN,
                    mapList.stream().map(m -> m.get(relationInfo.relationSlaveColumn)).collect(toSet()));
            relationInfo.slaveCriteriaList.add(slaveInlistFilterCriteria);
            MyQueryParam queryParam = new MyQueryParam();
            queryParam.setCriteriaList(relationInfo.slaveCriteriaList);
            // 远程从表返回的结果集中，仅仅包含从表中的关联字段即可。
            List<String> slaveSelectList = new LinkedList<>();
            slaveSelectList.add(relation.slaveIdField());
            queryParam.setSelectFieldList(slaveSelectList);
            // 关联集成数据需要把数据权限过滤关闭，以保证计算结果的正确性。
            queryParam.setUseDataFilter(false);
            ResponseResult<MyPageData<Map<String, Object>>> result = relationStruct.remoteClient.listMapBy(queryParam);
            if (!result.isSuccess()) {
                this.logErrorOrThrowException(result.getErrorMessage());
                return;
            }
            // 3. 将远程从表查询的返回结果，作为中间表与从表关联字段(relationSlaveColumn)的inlistFilter。
            // 并计算最终聚合结果。
            List<Object> slaveList = null;
            if (result.getData() != null) {
                slaveList = result.getData().getDataList()
                        .stream().map(m -> m.get(relation.slaveIdField())).collect(toList());
            }
            if (CollectionUtils.isNotEmpty(slaveList)) {
                // 中间表的最终过滤条件是从表返回的id列表将作为关联表slaveIdColumn的inlist-filter，
                // 同时原有主表的id列表仍将作为关联表masterIdColumn的inlist-filter。
                List<MyWhereCriteria> finalRelationCriteriaList = new LinkedList<>();
                MyWhereCriteria relationSlaveIdInListFilterCriteria = new MyWhereCriteria();
                relationSlaveIdInListFilterCriteria.setCriteria(relation.relationModelClass(),
                        relation.relationSlaveIdField(), MyWhereCriteria.OPERATOR_IN, slaveList);
                finalRelationCriteriaList.add(relationSlaveIdInListFilterCriteria);
                MyWhereCriteria relationMasterIdInListFilterCriteria = new MyWhereCriteria();
                if (forDataList) {
                    relationMasterIdInListFilterCriteria.setCriteria(relation.relationModelClass(),
                            relation.relationMasterIdField(), MyWhereCriteria.OPERATOR_IN, value);
                } else {
                    relationMasterIdInListFilterCriteria.setCriteria(relation.relationModelClass(),
                            relation.relationMasterIdField(), MyWhereCriteria.OPERATOR_EQUAL, value);
                }
                finalRelationCriteriaList.add(relationMasterIdInListFilterCriteria);
                String whereClause = MyWhereCriteria.makeCriteriaString(finalRelationCriteriaList);
                aggregationMapList = mapper().getGroupedListByCondition(
                        relationInfo.relationTable, groupedSelectList, whereClause, groupBy);
            }
        }
        this.doMakeAggregationData(aggregationMapList, resultList, relationStruct);
    }

    private void processRemoteManyToManyAggregationWithSlaveModel(
            RemoteAggregationRelationInfo relationInfo,
            RelationManyToManyAggregation relation,
            RemoteRelationStruct relationStruct,
            List<M> resultList) {
        // 聚合字段在远程从表，计算步骤如下：
        // 1. 先进行中间表过滤，并将中间表的过滤结果。
        StringBuilder selectList = new StringBuilder(64);
        selectList.append(relationInfo.relationMasterColumn).append(", ").append(relationInfo.relationSlaveColumn);
        List<Map<String, Object>> relationMapList =
                mapper().getListByCondition(relationInfo.relationTable,
                        selectList.toString(), relationInfo.relationWhereClause, null);
        if (CollectionUtils.isNotEmpty(relationMapList)) {
            // 2. 将中间表的过滤结果，作为从表关联字段的inlistFilter过滤，再结合从表自身的过滤条件，
            // 一并传给远程服务进行聚合计算，并返回计算结果。
            MyAggregationParam aggregationParam = createAggregationParam(AggregationKind.MANY_TO_MANY,
                    relation.aggregationType(), relation.aggregationField(), null);
            aggregationParam.setInFilterField(relation.slaveIdField());
            if (relationInfo.slaveCriteriaList != null) {
                aggregationParam.setWhereCriteriaList(relationInfo.slaveCriteriaList);
            }
            // 将过滤后的中间表结果集手动分组，传给远程分组计算调用，保证一次返回所有分组结果。
            Map<Object, Set<Object>> groupedFilterMap =
                    relationMapList.stream().collect(
                            groupingBy(m -> m.get(relationInfo.relationMasterColumn),
                                    mapping(n -> n.get(relationInfo.relationSlaveColumn), toSet())));
            aggregationParam.setGroupedInFilterValues(groupedFilterMap);
            aggregationParam.setUseDataFilter(false);
            // 开始将远程返回的聚合计算结果集合，回填到主表中的聚合虚拟字段。
            ResponseResult<List<Map<String, Object>>> result =
                    relationStruct.remoteClient.aggregateBy(aggregationParam);
            if (result.isSuccess()) {
                this.doMakeAggregationData(result.getData(), resultList, relationStruct);
            } else {
                this.logErrorOrThrowException(result.getErrorMessage());
            }
        }
    }

    private String makeManyToManyWhereClause(
            LocalRelationStruct relationStruct,
            Object masterIdValue,
            LocalAggregationRelationInfo localRelationInfo,
            Map<String, List<MyWhereCriteria>> criteriaListMap) {
        StringBuilder whereClause = new StringBuilder(256);
        whereClause.append(localRelationInfo.relationTable)
                .append(".").append(localRelationInfo.relationMasterColumn);
        if (masterIdValue instanceof Number) {
            whereClause.append(" = ").append(masterIdValue);
        } else {
            whereClause.append(" = '").append(masterIdValue).append("'");
        }
        // 如果需要从表聚合计算或参与过滤，则需要把中间表和从表之间的关联条件加上。
        if (!localRelationInfo.onlySelectRelationTable) {
            whereClause.append(AND_OP)
                    .append(localRelationInfo.relationTable)
                    .append(".")
                    .append(localRelationInfo.relationSlaveColumn)
                    .append(" = ")
                    .append(localRelationInfo.slaveTable)
                    .append(".")
                    .append(localRelationInfo.slaveColumn);
        }
        List<MyWhereCriteria> criteriaList = criteriaListMap.get(relationStruct.relationField.getName());
        if (criteriaList == null) {
            criteriaList = new LinkedList<>();
        }
        if (StringUtils.isNotBlank(relationStruct.localService.deletedFlagFieldName)) {
            MyWhereCriteria deleteFilter = new MyWhereCriteria();
            deleteFilter.setCriteria(
                    relationStruct.relationManyToManyAggregation.slaveModelClass(),
                    relationStruct.localService.deletedFlagFieldName,
                    MyWhereCriteria.OPERATOR_EQUAL,
                    GlobalDeletedFlag.NORMAL);
            criteriaList.add(deleteFilter);
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            String criteriaString = MyWhereCriteria.makeCriteriaString(criteriaList);
            whereClause.append(AND_OP).append(criteriaString);
        }
        return whereClause.toString();
    }

    private String makeOneToManyWhereClause(
            LocalRelationStruct relationStruct,
            Object masterIdValue,
            String slaveColumnName,
            Map<String, List<MyWhereCriteria>> criteriaListMap) {
        StringBuilder whereClause = new StringBuilder(64);
        if (masterIdValue instanceof Number) {
            whereClause.append(slaveColumnName).append(" = ").append(masterIdValue);
        } else {
            whereClause.append(slaveColumnName).append(" = '").append(masterIdValue).append("'");
        }
        List<MyWhereCriteria> criteriaList = criteriaListMap.get(relationStruct.relationField.getName());
        if (criteriaList == null) {
            criteriaList = new LinkedList<>();
        }
        if (StringUtils.isNotBlank(relationStruct.localService.deletedFlagFieldName)) {
            MyWhereCriteria deleteFilter = new MyWhereCriteria();
            deleteFilter.setCriteria(
                    relationStruct.relationOneToManyAggregation.slaveModelClass(),
                    relationStruct.localService.deletedFlagFieldName,
                    MyWhereCriteria.OPERATOR_EQUAL,
                    GlobalDeletedFlag.NORMAL);
            criteriaList.add(deleteFilter);
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            String criteriaString = MyWhereCriteria.makeCriteriaString(criteriaList);
            whereClause.append(AND_OP).append(criteriaString);
        }
        return whereClause.toString();
    }

    private static class LocalAggregationRelationInfo {
        private String slaveTable;
        private String slaveColumn;
        private String relationTable;
        private String relationMasterColumn;
        private String relationSlaveColumn;
        private String selectList;
        private String groupBy;
        private boolean onlySelectRelationTable;
    }

    private static class RemoteAggregationRelationInfo {
        private String relationTable;
        private String relationMasterColumn;
        private String relationSlaveColumn;
        private String relationWhereClause;
        private List<MyWhereCriteria> slaveCriteriaList;
    }

    private void doMakeAggregationData(
            List<Map<String, Object>> aggregationMapList,
            List<M> resultList,
            RelationStruct relationStruct) {
        // 根据获取的分组聚合结果集，绑定到主表总的关联字段。
        if (CollectionUtils.isNotEmpty(aggregationMapList)) {
            Map<Object, Object> relatedMap = new HashMap<>(aggregationMapList.size());
            for (Map<String, Object> map : aggregationMapList) {
                relatedMap.put(map.get(MyAggregationParam.KEY_NAME), map.get(MyAggregationParam.VALUE_NAME));
            }
            for (M dataObject : resultList) {
                Object masterIdValue = ReflectUtil.getFieldValue(dataObject, relationStruct.masterIdField);
                if (masterIdValue != null) {
                    Object value = relatedMap.get(masterIdValue);
                    if (value != null) {
                        ReflectUtil.setFieldValue(dataObject, relationStruct.relationField, value);
                    }
                }
            }
        }
    }

    private MyAggregationParam createAggregationParam(int kind, int type, String aggregationField, String groupField) {
        MyAggregationParam aggregationParam = new MyAggregationParam();
        aggregationParam.setAggregationKind(kind);
        aggregationParam.setAggregationType(type);
        aggregationParam.setAggregationField(aggregationField);
        aggregationParam.setGroupField(groupField);
        return aggregationParam;
    }

    private Tuple2<String, String> makeSelectListAndGroupByClause(
            String groupTableName,
            String groupColumnName,
            Class<?> aggregationModel,
            String aggregationTableName,
            String aggregationField,
            Integer aggregationType) {
        if (!AggregationType.isValid(aggregationType)) {
            throw new IllegalArgumentException("Invalid AggregationType Value ["
                    + aggregationType + "] in Model [" + aggregationModel.getName() + "].");
        }
        String aggregationFunc = AggregationType.getAggregationFunction(aggregationType);
        String aggregationColumn = MyModelUtil.mapToColumnName(aggregationField, aggregationModel);
        if (StringUtils.isBlank(aggregationColumn)) {
            throw new IllegalArgumentException("Invalid AggregationField ["
                    + aggregationField + "] in Model [" + aggregationModel.getName() + "].");
        }
        // 构建Select List
        // 如：r_table.master_id groupedKey, SUM(r_table.aggr_column) aggregated_value
        StringBuilder groupedSelectList = new StringBuilder(128);
        groupedSelectList.append(groupTableName)
                .append(".")
                .append(groupColumnName)
                .append(" ")
                .append(MyAggregationParam.KEY_NAME)
                .append(", ")
                .append(aggregationFunc)
                .append("(")
                .append(aggregationTableName)
                .append(".")
                .append(aggregationColumn)
                .append(") ")
                .append(MyAggregationParam.VALUE_NAME)
                .append(" ");
        StringBuilder groupBy = new StringBuilder(64);
        groupBy.append(groupTableName).append(".").append(groupColumnName);
        return new Tuple2<>(groupedSelectList.toString(), groupBy.toString());
    }

    static class RelationStruct {
        protected Field relationField;
        protected Field masterIdField;
    }

    static class LocalRelationStruct extends RelationStruct {
        private Field equalOneToOneRelationField;
        private BaseService<Object, Serializable> localService;
        private BaseDaoMapper<Object> manyToManyMapper;
        private Map<Object, String> dictMap;
        private RelationDict relationDict;
        private RelationOneToOne relationOneToOne;
        private RelationOneToMany relationOneToMany;
        private RelationManyToMany relationManyToMany;
        private RelationOneToManyAggregation relationOneToManyAggregation;
        private RelationManyToManyAggregation relationManyToManyAggregation;
    }

    static class RemoteRelationStruct extends RelationStruct {
        private Field equalOneToOneRelationField;
        private BaseClient<Object, Object, Object> remoteClient;
        private RelationDict relationDict;
        private RelationOneToOne relationOneToOne;
        private RelationOneToManyAggregation relationOneToManyAggregation;
        private RelationManyToManyAggregation relationManyToManyAggregation;
    }
}
