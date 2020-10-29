package com.orange.demo.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

/**
 * 数据源配置Bean对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Configuration
@EnableTransactionManagement
@MapperScan(value = {"com.orange.demo.*.dao"})
public class DataSourceConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        return DruidDataSourceBuilder.create().build();
    }
}
