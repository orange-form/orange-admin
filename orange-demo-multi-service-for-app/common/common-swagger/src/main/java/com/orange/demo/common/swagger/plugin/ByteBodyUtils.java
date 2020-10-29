package com.orange.demo.common.swagger.plugin;

import cn.hutool.core.lang.Assert;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.github.xiaoymin.knife4j.core.conf.Consts;
import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.List;

/**
 * 通过字节码方式动态创建接口参数封装对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class ByteBodyUtils {
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();

    public static Class<?> createDynamicModelClass(String name, List<ResolvedMethodParameter> parameters) {
        String clazzName = Consts.BASE_PACKAGE_PREFIX + name;
        try {
            CtClass tmp = CLASS_POOL.getCtClass(clazzName);
            if (tmp != null) {
                tmp.detach();
            }
        } catch (NotFoundException e) {
            // 需要吃掉这个异常。
        }
        CtClass ctClass = CLASS_POOL.makeClass(clazzName);
        try {
            int fieldCount = 0;
            for (ResolvedMethodParameter dynamicParameter : parameters) {
                // 因为在调用这个方法之前，这些参数都包含MyRequestBody注解。
                MyRequestBody myRequestBody =
                        dynamicParameter.findAnnotation(MyRequestBody.class).orNull();
                Assert.notNull(myRequestBody);
                String fieldName = dynamicParameter.defaultName().isPresent()
                        ? dynamicParameter.defaultName().get() : "parameter";
                if (StringUtils.isNotBlank(myRequestBody.value())) {
                    fieldName = myRequestBody.value();
                }
                ctClass.addField(createField(dynamicParameter, fieldName, ctClass));
                fieldCount++;
            }
            if (fieldCount > 0) {
                return ctClass.toClass();
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private static CtField createField(ResolvedMethodParameter parameter, String parameterName, CtClass ctClass)
            throws NotFoundException, CannotCompileException {
        CtField field = new CtField(getFieldType(parameter.getParameterType().getErasedType()), parameterName, ctClass);
        field.setModifiers(Modifier.PUBLIC);
        return field;
    }

    private static CtClass getFieldType(Class<?> propetyType) {
        CtClass fieldType = null;
        try {
            if (!propetyType.isAssignableFrom(Void.class)) {
                fieldType = CLASS_POOL.get(propetyType.getName());
            } else {
                fieldType = CLASS_POOL.get(String.class.getName());
            }
        } catch (NotFoundException e) {
            //抛异常
            ClassClassPath path = new ClassClassPath(propetyType);
            CLASS_POOL.insertClassPath(path);
            try {
                fieldType = CLASS_POOL.get(propetyType.getName());
            } catch (NotFoundException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
        return fieldType;
    }
}
