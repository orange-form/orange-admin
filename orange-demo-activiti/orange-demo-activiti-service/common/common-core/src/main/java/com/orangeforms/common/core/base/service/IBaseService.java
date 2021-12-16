package com.orangeforms.common.core.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.object.TableModelInfo;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 所有Service的接口。
 *
 * @param <M> Model对象的类型。
 * @param <K> Model对象主键的类型。
 * @author Jerry
 * @date 2021-06-06
 */
public interface IBaseService<M, K extends Serializable> extends IService<M>{

    /**
     * 如果主键存在则更新，否则新增保存实体对象。
     *
     * @param data    实体对象数据。
     * @param saveNew 新增实体对象方法。
     * @param update  更新实体对象方法。
     */
    void saveNewOrUpdate(M data, Consumer<M> saveNew, BiConsumer<M, M> update);

    /**
     * 如果主键存在的则更新，否则批量新增保存实体对象。
     *
     * @param dataList     实体对象数据列表。
     * @param saveNewBatch 批量新增实体对象方法。
     * @param update       更新实体对象方法。
     */
    void saveNewOrUpdateBatch(List<M> dataList, Consumer<List<M>> saveNewBatch, BiConsumer<M, M> update);

    /**
     * 根据过滤条件删除数据。
     *
     * @param filter 过滤对象。
     * @return 删除数量。
     */
    Integer removeBy(M filter);

    /**
     * 基于主从表之间的关联字段，批量改更新一对多从表数据。
     * 该操作会覆盖增、删、改三个操作，具体如下：
     * 1. 先删除。从表中relationFieldName字段的值为relationFieldValue, 同时主键Id不在dataList中的。
     * 2. 再批量插入。遍历dataList中没有主键Id的对象，视为新对象批量插入。
     * 3. 最后逐条更新，遍历dataList中有主键Id的对象，视为已存在对象并逐条更新。
     * 4. 如果更新时间和更新用户Id为空，我们将视当前记录为变化数据，因此使用当前时间和用户分别填充这两个字段。
     *
     * @param relationFieldName     主从表关联中，从表的Java字段名。
     * @param relationFieldValue    主从表关联中，与从表关联的主表字段值。该值会被赋值给从表关联字段。
     * @param updateUserIdFieldName 一对多从表的更新用户Id字段名。
     * @param updateTimeFieldName   一对多从表的更新时间字段名
     * @param dataList              批量更新的从表数据列表。
     * @param batchInserter         从表批量插入方法。
     */
    void updateBatchOneToManyRelation(
            String relationFieldName,
            Object relationFieldValue,
            String updateUserIdFieldName,
            String updateTimeFieldName,
            List<M> dataList,
            Consumer<List<M>> batchInserter);

    /**
     * 判断指定字段的数据是否存在，且仅仅存在一条记录。
     * 如果是基于主键的过滤，会直接调用existId过滤函数，提升性能。在有缓存的场景下，也可以利用缓存。
     *
     * @param fieldName  待过滤的字段名(Java 字段)。
     * @param fieldValue 字段值。
     * @return 存在且仅存在一条返回true，否则false。
     */
    boolean existOne(String fieldName, Object fieldValue);

    /**
     * 判断主键Id关联的数据是否存在。
     *
     * @param id 主键Id。
     * @return 存在返回true，否则false。
     */
    boolean existId(K id);

    /**
     * 返回符合 filterField = filterValue 条件的一条数据。
     *
     * @param filterField 过滤的Java字段。
     * @param filterValue 过滤的Java字段值。
     * @return 查询后的数据对象。
     */
    M getOne(String filterField, Object filterValue);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     *
     * @param id             主表主键Id。
     * @param relationParam  实体对象数据组装的参数构建器。
     * @return 查询结果对象。
     */
    M getByIdWithRelation(K id, MyRelationParam relationParam);

    /**
     * 获取所有数据。
     *
     * @return 返回所有数据。
     */
    List<M> getAllList();

    /**
     * 获取排序后所有数据。
     *
     * @param orderByProperties 需要排序的字段属性，这里使用Java对象中的属性名，而不是数据库字段名。
     * @return 返回排序后所有数据。
     */
    List<M> getAllListByOrder(String... orderByProperties);

    /**
     * 判断参数值主键集合中的所有数据，是否全部存在
     *
     * @param idSet  待校验的主键集合。
     * @return 全部存在返回true，否则false。
     */
    boolean existAllPrimaryKeys(Set<K> idSet);

    /**
     * 判断参数值列表中的所有数据，是否全部存在。另外，keyName字段在数据表中必须是唯一键值，否则返回结果会出现误判。
     *
     * @param inFilterField  待校验的数据字段，这里使用Java对象中的属性，如courseId，而不是数据字段名course_id
     * @param inFilterValues 数据值列表。
     * @return 全部存在返回true，否则false。
     */
    <T> boolean existUniqueKeyList(String inFilterField, Set<T> inFilterValues);

    /**
     * 返回符合主键 in (idValues) 条件的所有数据。
     *
     * @param idValues 主键值集合。
     * @return 检索后的数据列表。
     */
    List<M> getInList(Set<K> idValues);

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据。
     *
     * @param inFilterField  参与(In-list)过滤的Java字段。
     * @param inFilterValues 参与(In-list)过滤的Java字段值集合。
     * @return 检索后的数据列表。
     */
    <T> List<M> getInList(String inFilterField, Set<T> inFilterValues);

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据，并根据orderBy字段排序。
     *
     * @param inFilterField  参与(In-list)过滤的Java字段。
     * @param inFilterValues 参与(In-list)过滤的Java字段值集合。
     * @param orderBy        排序字段。
     * @return 检索后的数据列表。
     */
    <T> List<M> getInList(String inFilterField, Set<T> inFilterValues, String orderBy);

    /**
     * 返回符合主键 in (idValues) 条件的所有数据。同时返回关联数据。
     *
     * @param idValues      主键值集合。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @return 检索后的数据列表。
     */
    List<M> getInListWithRelation(Set<K> idValues, MyRelationParam relationParam);

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据。同时返回关联数据。
     *
     * @param inFilterField  参与(In-list)过滤的Java字段。
     * @param inFilterValues 参与(In-list)过滤的Java字段值集合。
     * @param relationParam  实体对象数据组装的参数构建器。
     * @return 检索后的数据列表。
     */
    <T> List<M> getInListWithRelation(String inFilterField, Set<T> inFilterValues, MyRelationParam relationParam);

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据，并根据orderBy字段排序。同时返回关联数据。
     *
     * @param inFilterField  参与(In-list)过滤的Java字段。
     * @param inFilterValues 参与(In-list)过滤的Java字段值集合。
     * @param orderBy        排序字段。
     * @param relationParam  实体对象数据组装的参数构建器。
     * @return 检索后的数据列表。
     */
    <T> List<M> getInListWithRelation(
            String inFilterField, Set<T> inFilterValues, String orderBy, MyRelationParam relationParam);

    /**
     * 用参数对象作为过滤条件，获取数据数量。
     *
     * @param filter 该方法基于mybatis 通用mapper，过滤对象中，只有被赋值的字段，才会成为where中的条件。
     * @return 返回过滤后的数据数量。
     */
    int getCountByFilter(M filter);

    /**
     * 用参数对象作为过滤条件，判断是否存在过滤数据。
     *
     * @param filter 该方法基于mybatis 通用mapper，过滤对象中，只有被赋值的字段，才会成为where中的条件。
     * @return 存在返回true，否则false。
     */
    boolean existByFilter(M filter);

    /**
     * 用参数对象作为过滤条件，获取查询结果。
     *
     * @param filter 该方法基于mybatis的通用mapper。如果参数为null，则返回全部数据。
     * @return 返回过滤后的数据。
     */
    List<M> getListByFilter(M filter);

    /**
     * 获取父主键Id下的所有子数据列表。
     *
     * @param parentIdFieldName 父主键字段名字，如"courseId"。
     * @param parentId          父主键的值。
     * @return 父主键Id下的所有子数据列表。
     */
    List<M> getListByParentId(String parentIdFieldName, K parentId);

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
    List<Map<String, Object>> getGroupedListByCondition(String selectFields, String whereClause, String groupBy);

    /**
     * 根据指定的显示字段列表、过滤条件字符串和排序字符串，返回查询结果。(基本是内部框架使用，不建议外部接口直接使用)。
     *
     * @param selectList  选择的Java字段列表。如果为空表示返回全部字段。
     * @param filter      过滤对象。
     * @param whereClause SQL常量形式的条件从句。
     * @param orderBy     SQL常量形式排序字段列表，逗号分隔。
     * @return 查询结果。
     */
    List<M> getListByCondition(List<String> selectList, M filter, String whereClause, String orderBy);

    /**
     * 用指定过滤条件，计算记录数量。(基本是内部框架使用，不建议外部接口直接使用)。
     *
     * @param whereClause SQL常量形式的条件从句。
     * @return 返回过滤后的数据数量。
     */
    Integer getCountByCondition(String whereClause);

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     * NOTE: BaseService中会给出返回CallResult.ok()的缺省实现。每个业务服务实现类在需要的时候可以重载该方法。
     *
     * @param data         数据对象。
     * @param originalData 原有数据对象，null表示data为新增对象。
     * @return 应答结果对象。
     */
    CallResult verifyRelatedData(M data, M originalData);

    /**
     * 根据最新对象列表和原有对象列表的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     * NOTE: BaseService中会给出返回CallResult.ok()的缺省实现。每个业务服务实现类在需要的时候可以重载该方法。
     *
     * @param dataList 数据对象列表。
     * @return 应答结果对象。
     */
    CallResult verifyRelatedData(List<M> dataList);

    /**
     * 集成所有与主表实体对象相关的关联数据列表。包括一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param resultList      主表实体对象列表。数据集成将直接作用于该对象列表。
     * @param relationParam   实体对象数据组装的参数构建器。
     */
    void buildRelationForDataList(List<M> resultList, MyRelationParam relationParam);

    /**
     * 集成所有与主表实体对象相关的关联数据列表。包括本地和远程服务的一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param resultList    主表实体对象列表。数据集成将直接作用于该对象列表。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @param ignoreFields  该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     */
    void buildRelationForDataList(List<M> resultList, MyRelationParam relationParam, Set<String> ignoreFields);

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
    void buildRelationForDataList(List<M> resultList, MyRelationParam relationParam, int batchSize);

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
    void buildRelationForDataList(
            List<M> resultList, MyRelationParam relationParam, int batchSize, Set<String> ignoreFields);

    /**
     * 集成所有与主表实体对象相关的关联数据对象。包括一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param dataObject      主表实体对象。数据集成将直接作用于该对象。
     * @param relationParam   实体对象数据组装的参数构建器。
     * @param <T>             实体对象类型。
     */
    <T extends M> void buildRelationForData(T dataObject, MyRelationParam relationParam);

    /**
     * 集成所有与主表实体对象相关的关联数据对象。包括本地和远程服务的一对一、字典、一对多和多对多聚合运算等。
     * 也可以根据实际需求，单独调用该函数所包含的各个数据集成函数。
     * NOTE: 该方法内执行的SQL将禁用数据权限过滤。
     *
     * @param dataObject    主表实体对象。数据集成将直接作用于该对象。
     * @param relationParam 实体对象数据组装的参数构建器。
     * @param ignoreFields  该集合中的字段，即便包含注解也不会在当前调用中进行数据组装。
     * @param <T>           实体对象类型。
     */
    <T extends M> void buildRelationForData(T dataObject, MyRelationParam relationParam, Set<String> ignoreFields);

    /**
     * 仅仅在spring boot 启动后的监听器事件中调用，缓存所有service的关联关系，加速后续的数据绑定效率。
     */
    void loadRelationStruct();

    /**
     * 获取当前服务引用的实体对象及表信息。
     *
     * @return 实体对象及表信息。
     */
    TableModelInfo getTableModelInfo();
}
