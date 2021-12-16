package com.orangeforms.common.swagger.plugin;

import com.orangeforms.common.core.annotation.MyRequestBody;
import com.google.common.base.CaseFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 构建操作接口参数对象的插件。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 102)
@ConditionalOnProperty(prefix = "swagger", name = "enabled")
public class DynamicBodyParameterBuilder implements OperationBuilderPlugin {

    @Override
    public void apply(OperationContext context) {
        List<ResolvedMethodParameter> methodParameters = context.getParameters();
        List<Parameter> parameters = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(methodParameters)) {
            List<ResolvedMethodParameter> bodyParameter = methodParameters.stream()
                    .filter(p -> p.hasParameterAnnotation(MyRequestBody.class)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(bodyParameter)) {
                // 构造model
                String groupName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, context.getGroupName());
                String clazzName = groupName + StringUtils.capitalize(context.getName());
                ResolvedMethodParameter methodParameter = bodyParameter.get(0);
                ParameterContext parameterContext = new ParameterContext(methodParameter,
                        new ParameterBuilder(),
                        context.getDocumentationContext(),
                        context.getGenericsNamingStrategy(),
                        context);
                Parameter parameter = parameterContext.parameterBuilder()
                        .parameterType("body").modelRef(new ModelRef(clazzName)).name(clazzName).build();
                parameters.add(parameter);
            }
        }
        context.operationBuilder().parameters(parameters);
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return delimiter == DocumentationType.SWAGGER_2;
    }
}
