package com.orangeforms.webadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 应用服务启动类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@EnableAsync
@SpringBootApplication
@ComponentScan("com.orangeforms")
public class WebAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebAdminApplication.class, args);
	}
}
