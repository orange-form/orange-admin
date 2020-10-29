package com.orange.demo.common.core.cache;

import java.util.*;
import java.util.function.Function;

/**
 * 字典数据内存缓存对象。
 *
 * @param <K> 字典表主键类型。
 * @param <V> 字典表对象类型。
 * @author Jerry
 * @date 2020-08-08
 */
public class MapDictionaryCache<K, V> implements DictionaryCache<K, V> {

    /**
     * 存储字典数据的Map。
     */
    protected LinkedHashMap<K, V> dataMap = new LinkedHashMap<>();
    /**
     * 获取字典主键数据的函数对象。
     */
    protected Function<V, K> idGetter;

    /**
     * 当前对象的构造器函数。
     *
     * @param idGetter 获取当前类主键字段值的函数对象。
     * @param <K>      字典主键类型。
     * @param <V>      字典对象类型
     * @return 实例化后的字典内存缓存对象。
     */
    public static <K, V> MapDictionaryCache<K, V> create(Function<V, K> idGetter) {
        if (idGetter == null) {
            throw new IllegalArgumentException("IdGetter can't be NULL.");
        }
        return new MapDictionaryCache<>(idGetter);
    }

    /**
     * 构造函数。
     *
     * @param idGetter 主键Id的获取函数对象。
     */
    public MapDictionaryCache(Function<V, K> idGetter) {
        this.idGetter = idGetter;
    }

    /**
     * 按照数据插入的顺序返回全部字典对象的列表。
     *
     * @return 全部字段数据列表。
     */
    @Override
    public synchronized List<V> getAll() {
        List<V> resultList = new LinkedList<>();
        for (Map.Entry<K, V> entry : dataMap.entrySet()) {
            resultList.add(entry.getValue());
        }
        return resultList;
    }

    /**
     * 获取缓存中与键列表对应的对象列表。
     *
     * @param keys 主键集合。
     * @return 对象列表。
     */
    @Override
    public synchronized List<V> getInList(Set<K> keys) {
        List<V> resultList = new LinkedList<>();
        keys.forEach(key -> {
            V object = dataMap.get(key);
            if (object != null) {
                resultList.add(object);
            }
        });
        return resultList;
    }

    /**
     * 将参数List中的数据保存到缓存中，同时保证getAll返回的数据列表，与参数列表中数据项的顺序保持一致。
     *
     * @param dataList 待缓存的数据列表。
     */
    @Override
    public synchronized void putAll(List<V> dataList) {
        if (dataList == null) {
            return;
        }
        dataList.forEach(dataObj -> {
            K id = idGetter.apply(dataObj);
            dataMap.put(id, dataObj);
        });
    }

    /**
     * 重新加载，先清空原有数据，在执行putAll的操作。
     *
     * @param dataList 待缓存的数据列表。
     * @param force    true则强制刷新，如果false，当缓存中存在数据时不刷新。
     */
    @Override
    public synchronized void reload(List<V> dataList, boolean force) {
        if (!force && this.getCount() > 0) {
            return;
        }
        this.invalidateAll();
        this.putAll(dataList);
    }

    /**
     * 从缓存中获取指定的数据。
     *
     * @param 数据的key。
     * @return 获取到的数据，如果没有返回null。
     */
    @Override
    public synchronized V get(K id) {
        return id == null ? null : dataMap.get(id);
    }

    /**
     * 将数据存入缓存。
     *
     * @param id     通常为字典数据的主键。
     * @param object 字典数据对象。
     */
    @Override
    public synchronized void put(K id, V object) {
        dataMap.put(id, object);
    }

    /**
     * 获取缓存中数据条目的数量。
     *
     * @return 返回缓存的数据数量。
     */
    @Override
    public synchronized int getCount() {
        return dataMap.size();
    }

    /**
     * 删除缓存中指定的键。
     *
     * @param id 待删除数据的主键。
     * @return 返回被删除的对象，如果主键不存在，返回null。
     */
    @Override
    public synchronized V invalidate(K id) {
        return id == null ? null : dataMap.remove(id);
    }

    /**
     * 删除缓存中，参数列表中包含的键。
     *
     * @param keys 待删除数据的主键集合。
     */
    @Override
    public synchronized void invalidateSet(Set<K> keys) {
        keys.forEach(id -> {
            if (id != null) {
                dataMap.remove(id);
            }
        });
    }

    /**
     * 清空缓存。
     */
    @Override
    public synchronized void invalidateAll() {
        dataMap.clear();
    }
}
