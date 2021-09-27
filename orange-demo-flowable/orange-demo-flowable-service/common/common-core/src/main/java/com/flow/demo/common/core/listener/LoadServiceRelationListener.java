package com.flow.demo.common.core.listener;

import com.flow.demo.common.core.base.service.BaseService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 应用程序启动后的事件监听对象。主要负责加载Model之间的字典关联和一对一关联所对应的Service结构关系。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Component
public class LoadServiceRelationListener implements ApplicationListener<ApplicationReadyEvent> {

    @SuppressWarnings("all")
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Map<String, BaseService> serviceMap =
                applicationReadyEvent.getApplicationContext().getBeansOfType(BaseService.class);
        for (Map.Entry<String, BaseService> e : serviceMap.entrySet()) {
            e.getValue().loadRelationStruct();
        }
    }
}
