package com.orangeforms.common.core.base.service;

import java.io.Serializable;
import java.util.List;

/**
 * 带有缓存功能的字典Service接口。
 *
 * @param <M> Model实体对象的类型。
 * @param <K> Model对象主键的类型。
 * @author Jerry
 * @date 2021-06-06
 */
public interface IBaseDictService<M, K extends Serializable> extends IBaseService<M, K> {

    /**
     * 重新加载数据库中所有当前表数据到系统内存。
     *
     * @param force true则强制刷新，如果false，当缓存中存在数据时不刷新。
     */
    void reloadCachedData(boolean force);

    /**
     * 保存新增对象。
     *
     * @param data 新增对象。
     * @return 返回新增对象。
     */
    M saveNew(M data);

    /**
     * 更新数据对象。
     *
     * @param data         更新的对象。
     * @param originalData 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(M data, M originalData);

    /**
     * 删除指定数据。
     *
     * @param id 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(K id);

    /**
     * 直接从缓存池中获取所有数据。
     *
     * @return 返回所有数据。
     */
    List<M> getAllListFromCache();

    /**
     * 存入缓存。
     *
     * @param data 新增或更新数据。
     */
    void putDictionaryCache(M data);

    /**
     * 根据字典主键将数据从缓存中删除。
     *
     * @param id 字典主键。
     */
    void removeDictionaryCache(K id);

    /**
     * 根据字典对象将数据从缓存中删除。
     *
     * @param data 字典数据。
     */
    void removeDictionaryCacheByModel(M data);

    /**
     * 获取缓存中的数据数量。
     *
     * @return 缓存中的数据总量。
     */
    int getCachedCount();
}
