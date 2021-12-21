package com.orangeforms.uaaauth;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * UAA服务启动类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@SpringCloudApplication
@ComponentScan("com.orangeforms")
public class UaaAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(UaaAuthApplication.class, args);
	}
}
