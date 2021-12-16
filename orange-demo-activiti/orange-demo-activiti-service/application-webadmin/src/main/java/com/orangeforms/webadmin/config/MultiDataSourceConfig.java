package com.orangeforms.webadmin.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.orangeforms.common.core.config.DynamicDataSource;
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
 * @date 2021-06-06
 */
@Configuration
@EnableTransactionManagement
@MapperScan(value = {"com.orangeforms.webadmin.*.dao", "com.orangeforms.common.*.dao"})
public class MultiDataSourceConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.main")
    public DataSource mainDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>(1);
        targetDataSources.put(DataSourceType.MAIN, mainDataSource());
        // 如果当前工程支持在线表单，这里请务必保证upms数据表所在数据库为缺省数据源。
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(mainDataSource());
        return dynamicDataSource;
    }
}
