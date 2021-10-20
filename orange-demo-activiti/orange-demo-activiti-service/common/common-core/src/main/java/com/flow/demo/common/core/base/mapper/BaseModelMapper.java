package com.flow.demo.common.core.base.mapper;

import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Model对象到Domain类型对象的相互转换。实现类通常声明在Model实体类中。
 *
 * @param <D> Domain域对象类型。
 * @param <M> Model实体对象类型。
 * @author Jerry
 * @date 2021-06-06
 */
public interface BaseModelMapper<D, M> {

    /**
     * 转换Model实体对象到Domain域对象。
     *
     * @param model Model实体对象。
     * @return Domain域对象。
     */
    D fromModel(M model);

    /**
     * 转换Model实体对象列表到Domain域对象列表。
     *
     * @param modelList Model实体对象列表。
     * @return Domain域对象列表。
     */
    List<D> fromModelList(List<M> modelList);

    /**
     * 转换Domain域对象到Model实体对象。
     *
     * @param domain Domain域对象。
     * @return Model实体对象。
     */
    M toModel(D domain);

    /**
     * 转换Domain域对象列表到Model实体对象列表。
     *
     * @param domainList Domain域对象列表。
     * @return Model实体对象列表。
     */
    List<M> toModelList(List<D> domainList);

    /**
     * 转换bean到map
     *
     * @param bean            bean对象。
     * @param ignoreNullValue 值为null的字段是否转换到Map。
     * @param <T>             bean类型。
     * @return 转换后的map对象。
     */
    default <T> Map<String, Object> beanToMap(T bean, boolean ignoreNullValue) {
        return BeanUtil.beanToMap(bean, false, ignoreNullValue);
    }

    /**
     * 转换bean集合到map集合
     *
     * @param dataList        bean对象集合。
     * @param ignoreNullValue 值为null的字段是否转换到Map。
     * @param <T>             bean类型。
     * @return 转换后的map对象集合。
     */
    default <T> List<Map<String, Object>> beanToMap(List<T> dataList, boolean ignoreNullValue) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new LinkedList<>();
        }
        return dataList.stream()
                .map(o -> BeanUtil.beanToMap(o, false, ignoreNullValue))
                .collect(Collectors.toList());
    }

    /**
     * 转换map到bean。
     *
     * @param map       map对象。
     * @param beanClazz bean的Class对象。
     * @param <T>       bean类型。
     * @return 转换后的bean对象。
     */
    default <T> T mapToBean(Map<String, Object> map, Class<T> beanClazz) {
        return BeanUtil.toBeanIgnoreError(map, beanClazz);
    }

    /**
     * 转换map集合到bean集合。
     *
     * @param mapList   map对象集合。
     * @param beanClazz bean的Class对象。
     * @param <T>       bean类型。
     * @return 转换后的bean对象集合。
     */
    default <T> List<T> mapToBean(List<Map<String, Object>> mapList, Class<T> beanClazz) {
        if (CollectionUtils.isEmpty(mapList)) {
            return new LinkedList<>();
        }
        return mapList.stream()
                .map(m -> BeanUtil.toBeanIgnoreError(m, beanClazz))
                .collect(Collectors.toList());
    }

    /**
     * 对于Map字段到Map字段的映射场景，MapStruct会根据方法签名自动选择该函数
     * 作为对象copy的函数。由于该函数是直接返回的，因此没有对象copy，效率更高。
     * 如果没有该函数，MapStruct会生成如下代码：
     * Map<String, Object> map = courseDto.getTeacherIdDictMap();
     * if ( map != null ) {
     *     course.setTeacherIdDictMap( new HashMap<String, Object>( map ) );
     * }
     *
     * @param map map对象。
     * @return 直接返回的map。
     */
    default Map<String, Object> mapToMap(Map<String, Object> map) {
        return map;
    }
}
