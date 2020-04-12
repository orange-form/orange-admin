package com.orange.admin.common.core.cache;

import java.util.*;
import java.util.function.Function;

/**
 * 字典数据内存缓存对象。
 *
 * @param <K> 字典表主键类型。
 * @param <V> 字典表对象类型。
 * @author Stephen.Liu
 * @date 2020-04-11
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
     * @param <K> 字典主键类型。
     * @param <V> 字典对象类型
     * @return 实例化后的字典内存缓存对象。
     */
    public static <K, V> MapDictionaryCache<K, V> create(Function<V, K> idGetter) {
        if (idGetter == null) {
            throw new IllegalArgumentException("IdGetter can't be NULL.");
        }
        return new MapDictionaryCache<>(idGetter);
    }

    public MapDictionaryCache(Function<V, K> idGetter) {
        this.idGetter = idGetter;
    }

    @Override
    public synchronized List<V> getAll() {
        List<V> resultList = new LinkedList<>();
        for (Map.Entry<K, V> entry : dataMap.entrySet()) {
            resultList.add(entry.getValue());
        }
        return resultList;
    }

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

    @Override
    public synchronized void reload(List<V> dataList) {
        this.invalidateAll();
        this.putAll(dataList);
    }

    @Override
    public synchronized V get(K id) {
        return id == null ? null : dataMap.get(id);
    }

    @Override
    public synchronized void put(K id, V object) {
        dataMap.put(id, object);
    }

    @Override
    public synchronized int getCount() {
        return dataMap.size();
    }

    @Override
    public synchronized V invalidate(K id) {
        return id == null ? null : dataMap.remove(id);
    }

    @Override
    public synchronized void invalidateSet(Set<K> keys) {
        keys.forEach(id -> {
            if (id != null) {
                dataMap.remove(id);
            }
        });
    }

    @Override
    public synchronized void invalidateAll() {
        dataMap.clear();
    }
}
