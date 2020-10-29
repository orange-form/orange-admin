package com.orange.demo.common.core.listener;

import com.orange.demo.common.core.base.service.BaseDictService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 应用程序启动后的事件监听对象。主要负责加载Model之间的字典关联和一对一关联所对应的Service结构关系。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Component
public class LoadCachedDataListener implements ApplicationListener<ApplicationReadyEvent> {

    @SuppressWarnings("all")
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Map<String, BaseDictService> serviceMap =
                applicationReadyEvent.getApplicationContext().getBeansOfType(BaseDictService.class);
        for (Map.Entry<String, BaseDictService> e : serviceMap.entrySet()) {
            e.getValue().loadCachedData();
        }
    }
}
