package com.orangeforms.common.flow.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.orangeforms.common.flow.base.service.BaseFlowService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流实例执行结束之后，需要需要做业务表的数据同步，可实现该接口。
 * 该插件通常用于一张表单多次提交的场景。为了避免修改后的审批中数据，由于尚未通过审批，而此时依赖该业务作为关联表时，
 * 关联到的数据往往是尚未通过审批的脏数据，因此需要做审批表和发布表的数据隔离。仅当审批流程完全结束且通过审批后，在
 * 将审批表及其一对一、一对多、多对多关联表中的数据，同步到发布表及其关联表中。至于具体需要同步那些表数据，需按需求而定。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class BaseDataSyncExtHelper {

    private Map<String, BaseFlowService> serviceMap = new HashMap<>();

    /**
     * 子类要基于自身所处理的流程定义标识，把子类的this对象，注册到父类的map中。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param service              流程服务实现基类。
     */
    public synchronized void doRegister(String processDefinitionKey, BaseFlowService service) {
        Assert.isTrue(StrUtil.isNotBlank(processDefinitionKey));
        Assert.notNull(service);
        serviceMap.put(processDefinitionKey, service);
    }

    /**
     *
     * 流程结束监听器(FlowFinishedListener) 会在流程结束时调用该方法。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @param businessKey          业务主表的主键Id。
     */
    public void triggerSync(String processDefinitionKey, String processInstanceId, String businessKey) {
        BaseFlowService service = serviceMap.get(processDefinitionKey);
        if (service != null) {
            try {
                service.doSyncBusinessData(processInstanceId, businessKey);
            } catch (Exception e) {
                String errorMessage = String.format(
                        "Failed to call doSyncBusinessData with processDefinitionKey {%s}, businessKey {%s}",
                        processDefinitionKey, businessKey);
                log.error(errorMessage, e);
                throw e;
            }
        }
    }
}
