package com.orange.admin.listener;

import com.orange.admin.interceptor.MybatisDataPermInterceptor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用服务启动监听器。
 * 目前主要功能是调用DataPermInterceptor中的loadMappersWithEnableDataPerm方法，
 * 将标记有数据权限规则注解的Mapper对象，加载到缓存，以提升系统运行时效率。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Component
public class LoadDataPermMapperListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        MybatisDataPermInterceptor interceptor =
                applicationReadyEvent.getApplicationContext().getBean(MybatisDataPermInterceptor.class);
        interceptor.loadMappersWithDataPerm();
    }
}
