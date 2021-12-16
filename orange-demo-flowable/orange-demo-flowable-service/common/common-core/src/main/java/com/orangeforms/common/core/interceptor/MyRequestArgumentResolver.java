package com.orangeforms.common.core.interceptor;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orangeforms.common.core.annotation.MyRequestBody;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

/**
 * MyRequestBody解析器
 * 解决的问题：
 * 1、单个字符串等包装类型都要写一个对象才可以用@RequestBody接收；
 * 2、多个对象需要封装到一个对象里才可以用@RequestBody接收。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class MyRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String JSONBODY_ATTRIBUTE = "MY_REQUEST_BODY_ATTRIBUTE_XX";

    private static final Set<Class<?>> CLASS_SET = new HashSet<>();

    static {
        CLASS_SET.add(Integer.class);
        CLASS_SET.add(Long.class);
        CLASS_SET.add(Short.class);
        CLASS_SET.add(Float.class);
        CLASS_SET.add(Double.class);
        CLASS_SET.add(Boolean.class);
        CLASS_SET.add(Byte.class);
        CLASS_SET.add(BigDecimal.class);
        CLASS_SET.add(Character.class);
    }

    /**
     * 设置支持的方法参数类型。
     *
     * @param parameter 方法参数。
     * @return 支持的类型。
     */
    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MyRequestBody.class);
    }

    /**
     * 参数解析，利用fastjson。
     * 注意：非基本类型返回null会报空指针异常，要通过反射或者JSON工具类创建一个空对象。
     */
    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String contentType = servletRequest.getContentType();
        if (!HttpMethod.POST.name().equals(servletRequest.getMethod())) {
            throw new IllegalArgumentException("Only POST method can be applied @MyRequestBody annotation！");
        }
        if (!StringUtils.containsIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)) {
            throw new IllegalArgumentException(
                    "Only application/json Content-Type can be applied @MyRequestBody annotation！");
        }
        // 根据@MyRequestBody注解value作为json解析的key
        MyRequestBody parameterAnnotation = parameter.getParameterAnnotation(MyRequestBody.class);
        JSONObject jsonObject = getRequestBody(webRequest);
        if (jsonObject == null) {
            if (parameterAnnotation.required()) {
                throw new IllegalArgumentException("Request Body is EMPTY!");
            }
            return null;
        }
        String key = parameterAnnotation.value();
        if (StringUtils.isBlank(key)) {
            key = parameter.getParameterName();
        }
        Object value = jsonObject.get(key);
        if (value == null) {
            if (parameterAnnotation.required()) {
                throw new IllegalArgumentException(String.format("Required parameter %s is not present!", key));
            }
            return null;
        }
        // 获取参数类型。
        Class<?> parameterType = parameter.getParameterType();
        // 基本类型
        if (parameterType.isPrimitive()) {
            return parsePrimitive(parameterType.getName(), value);
        }
        // 基本类型包装类
        if (isBasicDataTypes(parameterType)) {
            return parseBasicTypeWrapper(parameterType, value);
        } else if (parameterType == String.class) {
            // 字符串类型
            return value.toString();
        }
        // 数组类型
        if (value instanceof JSONArray) {
            return parseArray(parameterType, parameterAnnotation.elementType(), key, value);
        }
        // 其他复杂对象
        return JSON.toJavaObject((JSONObject) value, parameterType);
    }

    @SuppressWarnings("unchecked")
    private Object parseArray(Class<?> parameterType, Class<?> elementType, String key, Object value)
            throws IllegalAccessException, InstantiationException {
        Object o;
        if (!parameterType.equals(List.class)) {
            o = parameterType.newInstance();
            parameterType = (Class<?>) ((ParameterizedType)
                    parameterType.getGenericSuperclass()).getActualTypeArguments()[0];
        } else {
            parameterType = elementType;
            if (parameterType.equals(Class.class)) {
                throw new IllegalArgumentException(
                        String.format("List Type parameter %s MUST have elementType!", key));
            }
            o = new LinkedList<>();
        }
        if (!(o instanceof List)) {
            throw new IllegalArgumentException(String.format("Required parameter %s is List!", key));
        }
        ((List<Object>) o).addAll(((JSONArray) value).toJavaList(parameterType));
        return o;
    }

    private Object parsePrimitive(String parameterTypeName, Object value) {
        final String booleanTypeName = "boolean";
        if (booleanTypeName.equals(parameterTypeName)) {
            return Boolean.valueOf(value.toString());
        }
        final String intTypeName = "int";
        if (intTypeName.equals(parameterTypeName)) {
            return Integer.valueOf(value.toString());
        }
        final String charTypeName = "char";
        if (charTypeName.equals(parameterTypeName)) {
            return value.toString().charAt(0);
        }
        final String shortTypeName = "short";
        if (shortTypeName.equals(parameterTypeName)) {
            return Short.valueOf(value.toString());
        }
        final String longTypeName = "long";
        if (longTypeName.equals(parameterTypeName)) {
            return Long.valueOf(value.toString());
        }
        final String floatTypeName = "float";
        if (floatTypeName.equals(parameterTypeName)) {
            return Float.valueOf(value.toString());
        }
        final String doubleTypeName = "double";
        if (doubleTypeName.equals(parameterTypeName)) {
            return Double.valueOf(value.toString());
        }
        final String byteTypeName = "byte";
        if (byteTypeName.equals(parameterTypeName)) {
            return Byte.valueOf(value.toString());
        }
        return null;
    }

    private Object parseBasicTypeWrapper(Class<?> parameterType, Object value) {
        if (Number.class.isAssignableFrom(parameterType)) {
            if (value instanceof String) {
                return Convert.convert(parameterType, value);
            }
            Number number = (Number) value;
            if (parameterType == Integer.class) {
                return number.intValue();
            } else if (parameterType == Short.class) {
                return number.shortValue();
            } else if (parameterType == Long.class) {
                return number.longValue();
            } else if (parameterType == Float.class) {
                return number.floatValue();
            } else if (parameterType == Double.class) {
                return number.doubleValue();
            } else if (parameterType == Byte.class) {
                return number.byteValue();
            } else if (parameterType == BigDecimal.class) {
                if (value instanceof Double || value instanceof Float) {
                    return BigDecimal.valueOf(number.doubleValue());
                } else {
                    return BigDecimal.valueOf(number.longValue());
                }
            }
        } else if (parameterType == Boolean.class) {
            return value;
        } else if (parameterType == Character.class) {
            return value.toString().charAt(0);
        }
        return null;
    }

    private boolean isBasicDataTypes(Class<?> clazz) {
        return CLASS_SET.contains(clazz);
    }

    private JSONObject getRequestBody(NativeWebRequest webRequest) throws IOException {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        // 有就直接获取
        JSONObject jsonObject = (JSONObject) webRequest.getAttribute(JSONBODY_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        // 没有就从请求中读取
        if (jsonObject == null) {
            String jsonBody = IOUtils.toString(servletRequest.getReader());
            jsonObject = JSON.parseObject(jsonBody);
            if (jsonObject != null) {
                webRequest.setAttribute(JSONBODY_ATTRIBUTE, jsonObject, RequestAttributes.SCOPE_REQUEST);
            }
        }
        return jsonObject;
    }
}
