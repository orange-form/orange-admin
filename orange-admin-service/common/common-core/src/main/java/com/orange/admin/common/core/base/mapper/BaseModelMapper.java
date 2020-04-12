package com.orange.admin.common.core.base.mapper;

import cn.hutool.core.bean.BeanUtil;

import java.util.List;
import java.util.Map;

/**
 * Model对象到Domain类型对象的相互转换。实现类通常声明在Model实体类中。
 *
 * @param <D> Domain域对象类型。
 * @param <M> Model实体对象类型。
 * @author Stephen.Liu
 * @date 2020-04-11
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
     * @param bean bean对象。
     * @param ignoreNullValue 值为null的字段是否转换到Map。
     * @param <T> bean类型。
     * @return 转换后的map对象。
     */
    default <T> Map<String, Object> beanToMap(T bean, boolean ignoreNullValue) {
        return BeanUtil.beanToMap(bean);
    }

    /**
     * 转换map到bean。
     *
     * @param map map对象。
     * @param beanClazz bean的Class对象。
     * @param <T> bean类型。
     * @return 转换后的bean对象。
     */
    default <T> T mapToBean(Map<String, Object> map, Class<T> beanClazz) {
        return BeanUtil.mapToBean(map, beanClazz, true);
    }

    /**
     * 对于Map字段到Map字段的映射场景，MapStruct会根据方法签名自动选择该函数
     * 作为对象copy的函数。由于该函数是直接返回的，因此没有对象copy，效率更高。
     * 如果没有该函数，MapStruct会生成如下代码：
     *      Map<String, Object> map = courseDto.getTeacherIdDictMap();
     *      if ( map != null ) {
     *         course.setTeacherIdDictMap( new HashMap<String, Object>( map ) );
     *      }
     *
     * @param map map对象。
     * @return 直接返回的map。
     */
    default Map<String, Object> mapToMap(Map<String, Object> map) {
        return map;
    }
}
