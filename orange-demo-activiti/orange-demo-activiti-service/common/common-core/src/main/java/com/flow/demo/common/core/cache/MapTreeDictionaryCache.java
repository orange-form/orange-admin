package com.flow.demo.common.core.cache;

import com.flow.demo.common.core.exception.MapCacheAccessException;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * 树形字典数据内存缓存对象。
 *
 * @param <K> 字典表主键类型。
 * @param <V> 字典表对象类型。
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class MapTreeDictionaryCache<K, V> extends MapDictionaryCache<K, V> {

    /**
     * 树形数据存储对象。
     */
    private final Multimap<K, V> allTreeMap = LinkedHashMultimap.create();
    /**
     * 获取字典父主键数据的函数对象。
     */
    protected final Function<V, K> parentIdGetter;

    /**
     * 当前对象的构造器函数。
     *
     * @param idGetter       获取当前类主键字段值的函数对象。
     * @param parentIdGetter 获取当前类父主键字段值的函数对象。
     * @param <K>            字典主键类型。
     * @param <V>            字典对象类型
     * @return 实例化后的树形字典内存缓存对象。
     */
    public static <K, V> MapTreeDictionaryCache<K, V> create(Function<V, K> idGetter, Function<V, K> parentIdGetter) {
        if (idGetter == null) {
            throw new IllegalArgumentException("IdGetter can't be NULL.");
        }
        if (parentIdGetter == null) {
            throw new IllegalArgumentException("ParentIdGetter can't be NULL.");
        }
        return new MapTreeDictionaryCache<>(idGetter, parentIdGetter);
    }

    /**
     * 构造函数。
     *
     * @param idGetter       获取当前类主键字段值的函数对象。
     * @param parentIdGetter 获取当前类父主键字段值的函数对象。
     */
    public MapTreeDictionaryCache(Function<V, K> idGetter, Function<V, K> parentIdGetter) {
        super(idGetter);
        this.parentIdGetter = parentIdGetter;
    }


    /**
     * 重新加载，先清空原有数据，在执行putAll的操作。
     *
     * @param dataList 待缓存的数据列表。
     * @param force    true则强制刷新，如果false，当缓存中存在数据时不刷新。
     */
    @Override
    public void reload(List<V> dataList, boolean force) {
        if (!force && this.getCount() > 0) {
            return;
        }
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataMap.clear();
                    allTreeMap.clear();
                    dataList.forEach(data -> {
                        K id = idGetter.apply(data);
                        dataMap.put(id, data);
                        K parentId = parentIdGetter.apply(data);
                        allTreeMap.put(parentId, data);
                    });
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [MapDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT.",
                    e.getClass().getSimpleName());
            log.warn(exceptionMessage);
            throw new MapCacheAccessException(exceptionMessage, e);
        }
    }

    /**
     * 获取该父主键的子数据列表。
     *
     * @param parentId 父主键Id。
     * @return 子数据列表。
     */
    public List<V> getListByParentId(K parentId) {
        List<V> resultList = new LinkedList<>();
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    resultList.addAll(allTreeMap.get(parentId));
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [MapDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT.",
                    e.getClass().getSimpleName());
            log.warn(exceptionMessage);
            throw new MapCacheAccessException(exceptionMessage, e);
        }
        return resultList;
    }

    /**
     * 将参数List中的数据保存到缓存中，同时保证getAll返回的数据列表，与参数列表中数据项的顺序保持一致。
     *
     * @param dataList 待缓存的数据列表。
     */
    @Override
    public void putAll(List<V> dataList) {
        if (dataList == null) {
            return;
        }
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataList.forEach(data -> {
                        K id = idGetter.apply(data);
                        dataMap.put(id, data);
                        K parentId = parentIdGetter.apply(data);
                        allTreeMap.remove(parentId, data);
                        allTreeMap.put(parentId, data);
                    });
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [MapDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT.",
                    e.getClass().getSimpleName());
            log.warn(exceptionMessage);
            throw new MapCacheAccessException(exceptionMessage, e);
        }
    }

    /**
     * 将数据存入缓存。
     *
     * @param id   通常为字典数据的主键。
     * @param data 字典数据对象。
     */
    @Override
    public void put(K id, V data) {
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataMap.put(id, data);
                    K parentId = parentIdGetter.apply(data);
                    allTreeMap.remove(parentId, data);
                    allTreeMap.put(parentId, data);
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [MapDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT.",
                    e.getClass().getSimpleName());
            log.warn(exceptionMessage);
            throw new MapCacheAccessException(exceptionMessage, e);
        }
    }

    /**
     * 删除缓存中指定的键。
     *
     * @param id 待删除数据的主键。
     * @return 返回被删除的对象，如果主键不存在，返回null。
     */
    @Override
    public V invalidate(K id) {
        V v;
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    v = dataMap.remove(id);
                    if (v != null) {
                        K parentId = parentIdGetter.apply(v);
                        allTreeMap.remove(parentId, v);
                    }
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [MapDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT.",
                    e.getClass().getSimpleName());
            log.warn(exceptionMessage);
            throw new MapCacheAccessException(exceptionMessage, e);
        }
        return v;
    }

    /**
     * 删除缓存中，参数列表中包含的键。
     *
     * @param keys 待删除数据的主键集合。
     */
    @Override
    public void invalidateSet(Set<K> keys) {
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    keys.forEach(id -> {
                        if (id != null) {
                            V data = dataMap.remove(id);
                            if (data != null) {
                                K parentId = parentIdGetter.apply(data);
                                allTreeMap.remove(parentId, data);
                            }
                        }
                    });
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [MapDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT.",
                    e.getClass().getSimpleName());
            log.warn(exceptionMessage);
            throw new MapCacheAccessException(exceptionMessage, e);
        }
    }

    /**
     * 清空缓存。
     */
    @Override
    public void invalidateAll() {
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataMap.clear();
                    allTreeMap.clear();
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [MapDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT.",
                    e.getClass().getSimpleName());
            log.warn(exceptionMessage);
            throw new MapCacheAccessException(exceptionMessage, e);
        }
    }
}
