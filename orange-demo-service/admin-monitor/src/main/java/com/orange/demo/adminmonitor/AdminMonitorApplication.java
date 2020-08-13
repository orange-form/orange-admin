package com.orange.demo.adminmonitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 服务指标监控启动类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminMonitorApplication.class, args);
    }
}
