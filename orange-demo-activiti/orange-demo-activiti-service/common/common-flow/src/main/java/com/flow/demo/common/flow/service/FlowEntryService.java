package com.flow.demo.common.flow.service;

import com.flow.demo.common.core.base.service.IBaseService;
import com.flow.demo.common.core.object.CallResult;
import com.flow.demo.common.flow.model.*;

import javax.xml.stream.XMLStreamException;
import java.util.List;
import java.util.Set;

/**
 * FlowEntry数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowEntryService extends IBaseService<FlowEntry, Long> {

    /**
     * 保存新增对象。
     *
     * @param flowEntry 新增工作流对象。
     * @return 返回新增对象。
     */
    FlowEntry saveNew(FlowEntry flowEntry);

    /**
     * 发布指定流程。
     *
     * @param flowEntry       待发布的流程对象。
     * @param initTaskInfo    第一个非开始节点任务的附加信息。
     * @param flowTaskExtList 所有用户任务的自定义扩展数据列表。
     * @throws XMLStreamException 解析bpmn.xml的异常。
     */
    void publish(FlowEntry flowEntry, String initTaskInfo, List<FlowTaskExt> flowTaskExtList) throws XMLStreamException;

    /**
     * 更新数据对象。
     *
     * @param flowEntry         更新的对象。
     * @param originalFlowEntry 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(FlowEntry flowEntry, FlowEntry originalFlowEntry);

    /**
     * 删除指定数据。
     *
     * @param entryId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long entryId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getFlowEntryListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowEntry> getFlowEntryList(FlowEntry filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getFlowEntryList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowEntry> getFlowEntryListWithRelation(FlowEntry filter, String orderBy);

    /**
     * 根据流程定义标识获取流程对象。
     *
     * @param processDefinitionKey 流程定义标识。
     * @return 流程对象。
     */
    FlowEntry getFlowEntryByProcessDefinitionKey(String processDefinitionKey);

    /**
     * 根据流程Id获取流程发布列表数据。
     *
     * @param entryId 流程Id。
     * @return 流程关联的发布列表数据。
     */
    List<FlowEntryPublish> getFlowEntryPublishList(Long entryId);

    /**
     * 根据流程引擎中的流程定义Id集合，查询流程发布对象。
     *
     * @param processDefinitionIdSet 流程引擎中的流程定义Id集合。
     * @return 查询结果。
     */
    List<FlowEntryPublish> getFlowEntryPublishList(Set<String> processDefinitionIdSet);

    /**
     * 获取指定工作流发布版本对象。
     *
     * @param entryPublishId 工作流发布对象Id。
     * @return 查询后的对象。
     */
    FlowEntryPublish getFlowEntryPublishById(Long entryPublishId);

    /**
     * 获取指定流程定义Id对应的流程发布对象。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 流程发布对象。
     */
    FlowEntryPublish getFlowEntryPublishByDefinitionId(String processDefinitionId);

    /**
     * 为指定工作流更新发布的主版本。
     *
     * @param flowEntry               工作流对象。
     * @param newMainFlowEntryPublish 工作流新的发布主版本对象。
     */
    void updateFlowEntryMainVersion(FlowEntry flowEntry, FlowEntryPublish newMainFlowEntryPublish);

    /**
     * 挂起指定的工作流发布对象。
     *
     * @param flowEntryPublish 待挂起的工作流发布对象。
     */
    void suspendFlowEntryPublish(FlowEntryPublish flowEntryPublish);

    /**
     * 激活指定的工作流发布对象。
     *
     * @param flowEntryPublish 待恢复的工作流发布对象。
     */
    void activateFlowEntryPublish(FlowEntryPublish flowEntryPublish);

    /**
     * 主表的关联数据验证。
     *
     * @param flowEntry         最新数据对象。
     * @param originalFlowEntry 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    CallResult verifyRelatedData(FlowEntry flowEntry, FlowEntry originalFlowEntry);
}
