package com.flow.demo.common.flow.util;

import org.springframework.stereotype.Component;

/**
 * 工作流自定义扩展工厂类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Component
public class FlowCustomExtFactory {

    private BaseFlowDeptPostExtHelper flowDeptPostExtHelper;

    /**
     * 获取业务模块自行实现的部门岗位扩展帮助实现类。
     *
     * @return 业务模块自行实现的部门岗位扩展帮助实现类。
     */
    public BaseFlowDeptPostExtHelper getFlowDeptPostExtHelper() {
        return flowDeptPostExtHelper;
    }

    /**
     * 注册业务模块自行实现的部门岗位扩展帮助实现类。
     *
     * @param helper 业务模块自行实现的部门岗位扩展帮助实现类。
     */
    public void registerFlowDeptPostExtHelper(BaseFlowDeptPostExtHelper helper) {
        this.flowDeptPostExtHelper = helper;
    }
}
