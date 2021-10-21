package com.orange.demo.operationlogconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 操作日志消费者服务启动类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@SpringCloudApplication
@ComponentScan("com.orange.demo")
public class OperationLogConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationLogConsumerApplication.class, args);
    }
}
