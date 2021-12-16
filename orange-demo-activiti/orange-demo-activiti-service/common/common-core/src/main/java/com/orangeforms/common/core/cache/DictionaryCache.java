package com.orangeforms.common.core.cache;

import java.util.List;
import java.util.Set;

/**
 * 主要用于完整缓存字典表数据的接口对象。
 *
 * @param <K> 字典表主键类型。
 * @param <V> 字典表对象类型。
 * @author Jerry
 * @date 2021-06-06
 */
public interface DictionaryCache<K, V> {

    /**
     * 按照数据插入的顺序返回全部字典对象的列表。
     *
     * @return 全部字段数据列表。
     */
    List<V> getAll();

    /**
     * 获取缓存中与键列表对应的对象列表。
     *
     * @param keys 主键集合。
     * @return 对象列表。
     */
    List<V> getInList(Set<K> keys);

    /**
     * 将参数List中的数据保存到缓存中，同时保证getAll返回的数据列表，与参数列表中数据项的顺序保持一致。
     *
     * @param dataList 待缓存的数据列表。
     */
    void putAll(List<V> dataList);

    /**
     * 重新加载，先清空原有数据，在执行putAll的操作。
     *
     * @param dataList 待缓存的数据列表。
     * @param force    true则强制刷新，如果false，当缓存中存在数据时不刷新。
     */
    void reload(List<V> dataList, boolean force);

    /**
     * 从缓存中获取指定的数据。
     *
     * @param key 数据的key。
     * @return 获取到的数据，如果没有返回null。
     */
    V get(K key);

    /**
     * 将数据存入缓存。
     *
     * @param key    通常为字典数据的主键。
     * @param object 字典数据对象。
     */
    void put(K key, V object);

    /**
     * 获取缓存中数据条目的数量。
     *
     * @return 返回缓存的数据数量。
     */
    int getCount();

    /**
     * 删除缓存中指定的键。
     *
     * @param key 待删除数据的主键。
     * @return 返回被删除的对象，如果主键不存在，返回null。
     */
    V invalidate(K key);

    /**
     * 删除缓存中，参数列表中包含的键。
     *
     * @param keys 待删除数据的主键集合。
     */
    void invalidateSet(Set<K> keys);

    /**
     * 清空缓存。
     */
    void invalidateAll();
}
