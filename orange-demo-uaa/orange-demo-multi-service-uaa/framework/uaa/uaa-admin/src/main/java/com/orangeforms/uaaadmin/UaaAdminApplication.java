package com.orangeforms.uaaadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * UAA管理服务启动类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@SpringCloudApplication
@ComponentScan("com.orangeforms")
public class UaaAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(UaaAdminApplication.class, args);
	}
}
