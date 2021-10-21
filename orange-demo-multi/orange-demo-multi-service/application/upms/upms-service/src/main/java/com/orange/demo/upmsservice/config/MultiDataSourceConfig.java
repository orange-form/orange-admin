package com.orange.demo.upmsservice.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.orange.demo.common.core.config.DynamicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Configuration
@EnableTransactionManagement
@MapperScan(value = {"com.orange.demo.*.dao", "com.orange.demo.common.*.dao"})
public class MultiDataSourceConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.main")
    public DataSource mainDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 默认生成的用于保存操作日志的数据源，可根据需求修改。
     * 这里我们还是非常推荐给操作日志使用独立的数据源，这样便于今后的数据迁移。
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.operation-log")
    public DataSource operationLogDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>(1);
        targetDataSources.put(DataSourceType.MAIN, mainDataSource());
        targetDataSources.put(DataSourceType.OPERATION_LOG, operationLogDataSource());
        // 如果当前工程支持在线表单，这里请务必保证upms数据表所在数据库为缺省数据源。
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(mainDataSource());
        return dynamicDataSource;
    }
}
