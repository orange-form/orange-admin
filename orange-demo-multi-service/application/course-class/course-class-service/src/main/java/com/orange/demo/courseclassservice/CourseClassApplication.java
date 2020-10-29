package com.orange.demo.courseclassservice;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * course-class服务启动类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.orange.demo")
@ComponentScan("com.orange.demo")
public class CourseClassApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseClassApplication.class, args);
	}
}
