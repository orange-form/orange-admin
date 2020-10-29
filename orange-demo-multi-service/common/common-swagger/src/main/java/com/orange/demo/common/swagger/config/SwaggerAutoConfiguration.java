package com.orange.demo.common.swagger.config;

import com.orange.demo.common.core.annotation.MyRequestBody;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 自动加载bean的配置对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@EnableSwagger2
@EnableKnife4j
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "swagger", name = "enabled")
public class SwaggerAutoConfiguration {

    @Bean
    public Docket rotbotDocket(SwaggerProperties properties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(MyRequestBody.class)
                .apiInfo(apiInfo(properties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo(SwaggerProperties properties) {
        return new ApiInfoBuilder()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion()).build();
    }
}
