package com.orangeforms.common.datafilter.listener;

import com.orangeforms.common.datafilter.interceptor.MybatisDataFilterInterceptor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用服务启动监听器。
 * 目前主要功能是调用MybatisDataFilterInterceptor中的loadInfoWithDataFilter方法，
 * 将标记有过滤注解的数据加载到缓存，以提升系统运行时效率。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Component
public class LoadDataFilterInfoListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        MybatisDataFilterInterceptor interceptor =
                applicationReadyEvent.getApplicationContext().getBean(MybatisDataFilterInterceptor.class);
        interceptor.loadInfoWithDataFilter();
    }
}
