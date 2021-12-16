package com.orangeforms.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.orangeforms.common.core.constant.AppDeviceType;
import com.orangeforms.common.core.validator.AddGroup;
import com.orangeforms.common.core.validator.UpdateGroup;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 脚手架中常用的基本工具方法集合，一般而言工程内部使用的方法。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class MyCommonUtil {

    private static final Validator VALIDATOR;

    static {
        VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 创建uuid。
     *
     * @return 返回uuid。
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 对用户密码进行加盐后加密。
     *
     * @param password     明文密码。
     * @param passwordSalt 盐值。
     * @return 加密后的密码。
     */
    public static String encrptedPassword(String password, String passwordSalt) {
        return DigestUtil.md5Hex(password + passwordSalt);
    }

    /**
     * 这个方法一般用于Controller对于入口参数的基本验证。
     * 对于字符串，如果为空字符串，也将视为Blank，同时返回true。
     *
     * @param objs 一组参数。
     * @return 返回是否存在null或空字符串的参数。
     */
    public static boolean existBlankArgument(Object...objs) {
        for (Object obj : objs) {
            if (MyCommonUtil.isBlankOrNull(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结果和 existBlankArgument 相反。
     *
     * @param objs 一组参数。
     * @return 返回是否存在null或空字符串的参数。
     */
    public static boolean existNotBlankArgument(Object...objs) {
        for (Object obj : objs) {
            if (!MyCommonUtil.isBlankOrNull(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证参数是否为空。
     *
     * @param obj 待判断的参数。
     * @return 空或者null返回true，否则false。
     */
    public static boolean isBlankOrNull(Object obj) {
        if (obj instanceof Collection) {
            return CollUtil.isEmpty((Collection<?>) obj);
        }
        return obj == null || (obj instanceof CharSequence && StrUtil.isBlank((CharSequence) obj));
    }

    /**
     * 验证参数是否为非空。
     *
     * @param obj 待判断的参数。
     * @return 空或者null返回false，否则true。
     */
    public static boolean isNotBlankOrNull(Object obj) {
        return !isBlankOrNull(obj);
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param model  带校验的model。
     * @param groups Validate绑定的校验组。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(T model, Class<?>...groups) {
        if (model != null) {
            Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(model, groups);
            if (!constraintViolations.isEmpty()) {
                Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
                ConstraintViolation<T> constraint = it.next();
                return constraint.getMessage();
            }
        }
        return null;
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param model     带校验的model。
     * @param forUpdate 是否为更新。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(T model, boolean forUpdate) {
        if (model != null) {
            Set<ConstraintViolation<T>> constraintViolations;
            if (forUpdate) {
                constraintViolations = VALIDATOR.validate(model, Default.class, UpdateGroup.class);
            } else {
                constraintViolations = VALIDATOR.validate(model, Default.class, AddGroup.class);
            }
            if (!constraintViolations.isEmpty()) {
                Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
                ConstraintViolation<T> constraint = it.next();
                return constraint.getMessage();
            }
        }
        return null;
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param modelList 带校验的model列表。
     * @param groups    Validate绑定的校验组。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(List<T> modelList, Class<?>... groups) {
        if (CollUtil.isNotEmpty(modelList)) {
            for (T model : modelList) {
                String errorMessage = getModelValidationError(model, groups);
                if (StrUtil.isNotBlank(errorMessage)) {
                    return errorMessage;
                }
            }
        }
        return null;
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param modelList 带校验的model列表。
     * @param forUpdate 是否为更新。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(List<T> modelList, boolean forUpdate) {
        if (CollUtil.isNotEmpty(modelList)) {
            for (T model : modelList) {
                String errorMessage = getModelValidationError(model, forUpdate);
                if (StrUtil.isNotBlank(errorMessage)) {
                    return errorMessage;
                }
            }
        }
        return null;
    }

    /**
     * 拼接参数中的字符串列表，用指定分隔符进行分割，同时每个字符串对象用单引号括起来。
     *
     * @param dataList  字符串集合。
     * @param separator 分隔符。
     * @return 拼接后的字符串。
     */
    public static String joinString(Collection<String> dataList, final char separator) {
        int index = 0;
        StringBuilder sb = new StringBuilder(128);
        for (String data : dataList) {
            sb.append("'").append(data).append("'");
            if (index++ != dataList.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 将SQL Like中的通配符替换为字符本身的含义，以便于比较。
     *
     * @param str 待替换的字符串。
     * @return 替换后的字符串。
     */
    public static String replaceSqlWildcard(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        return StrUtil.replaceChars(StrUtil.replaceChars(str, "_", "\\_"), "%", "\\%");
    }

    /**
     * 获取对象中，非空字段的名字列表。
     *
     * @param object 数据对象。
     * @param clazz  数据对象的class类型。
     * @param <T>    数据对象类型。
     * @return 数据对象中，值不为NULL的字段数组。
     */
    public static <T> String[] getNotNullFieldNames(T object, Class<T> clazz) {
        Field[] fields = ReflectUtil.getFields(clazz);
        List<String> fieldNameList = Arrays.stream(fields)
                .filter(f -> ReflectUtil.getFieldValue(object, f) != null)
                .map(Field::getName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(fieldNameList)) {
            return fieldNameList.toArray(new String[]{});
        }
        return new String[]{};
    }

    /**
     * 获取请求头中的设备信息。
     *
     * @return 设备类型，具体值可参考AppDeviceType常量类。
     */
    public static int getDeviceType() {
        // 缺省都按照Web登录方式设置，如果前端header中的值为不合法值，这里也不会报错，而是使用Web缺省方式。
        int deviceType = AppDeviceType.WEB;
        String deviceTypeString = ContextUtil.getHttpRequest().getHeader("deviceType");
        if (StrUtil.isNotBlank(deviceTypeString)) {
            Integer type = Integer.valueOf(deviceTypeString);
            if (AppDeviceType.isValid(type)) {
                deviceType = type;
            }
        }
        return deviceType;
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private MyCommonUtil() {
    }
}
