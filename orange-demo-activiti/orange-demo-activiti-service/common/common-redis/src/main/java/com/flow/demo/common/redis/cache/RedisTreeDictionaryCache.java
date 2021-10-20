package com.flow.demo.common.redis.cache;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import com.flow.demo.common.core.constant.ApplicationConstant;
import com.flow.demo.common.core.exception.RedisCacheAccessException;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RListMultimap;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树形字典数据Redis缓存对象。
 *
 * @param <K> 字典表主键类型。
 * @param <V> 字典表对象类型。
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class RedisTreeDictionaryCache<K, V> extends RedisDictionaryCache<K, V> {

    /**
     * 树形数据存储对象。
     */
    private final RListMultimap<K, String> allTreeMap;
    /**
     * 获取字典父主键数据的函数对象。
     */
    protected final Function<V, K> parentIdGetter;

    /**
     * 当前对象的构造器函数。
     *
     * @param redissonClient Redisson的客户端对象。
     * @param dictionaryName 字典表的名称。等同于redis hash对象的key。
     * @param valueClazz     值对象的Class对象。
     * @param idGetter       获取当前类主键字段值的函数对象。
     * @param parentIdGetter 获取当前类父主键字段值的函数对象。
     * @param <K>            字典主键类型。
     * @param <V>            字典对象类型
     * @return 实例化后的树形字典内存缓存对象。
     */
    public static <K, V> RedisTreeDictionaryCache<K, V> create(
            RedissonClient redissonClient,
            String dictionaryName,
            Class<V> valueClazz,
            Function<V, K> idGetter,
            Function<V, K> parentIdGetter) {
        if (idGetter == null) {
            throw new IllegalArgumentException("IdGetter can't be NULL.");
        }
        if (parentIdGetter == null) {
            throw new IllegalArgumentException("ParentIdGetter can't be NULL.");
        }
        return new RedisTreeDictionaryCache<>(
                redissonClient, dictionaryName, valueClazz, idGetter, parentIdGetter);
    }

    /**
     * 构造函数。
     *
     * @param redissonClient Redisson的客户端对象。
     * @param dictionaryName 字典表的名称。等同于redis hash对象的key。
     * @param valueClazz     值对象的Class对象。
     * @param idGetter       获取当前类主键字段值的函数对象。
     * @param parentIdGetter 获取当前类父主键字段值的函数对象。
     */
    public RedisTreeDictionaryCache(
            RedissonClient redissonClient,
            String dictionaryName,
            Class<V> valueClazz,
            Function<V, K> idGetter,
            Function<V, K> parentIdGetter) {
        super(redissonClient, dictionaryName, valueClazz, idGetter);
        this.allTreeMap = redissonClient.getListMultimap(
                dictionaryName + ApplicationConstant.TREE_DICT_CACHE_NAME_SUFFIX);
        this.parentIdGetter = parentIdGetter;
    }

    /**
     * 获取该父主键的子数据列表。
     *
     * @param parentId 父主键Id。如果parentId为null，则返回所有一级节点数据。
     * @return 子数据列表。
     */
    public List<V> getListByParentId(K parentId) {
        List<String> dataList;
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataList = allTreeMap.get(parentId);
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisTreeDictionaryCache::getListByParentId] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
        if (CollectionUtils.isEmpty(dataList)) {
            return new LinkedList<>();
        }
        List<V> resultList = new LinkedList<>();
        dataList.forEach(data -> resultList.add(JSON.parseObject(data, valueClazz)));
        return resultList;
    }

    /**
     * 将参数List中的数据保存到缓存中，同时保证getAll返回的数据列表，与参数列表中数据项的顺序保持一致。
     *
     * @param dataList 待缓存的数据列表。
     */
    @Override
    public void putAll(List<V> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 锁外执行数据结构组装，降低锁的粒度，提高并发性。
        Map<K, String> map = dataList.stream()
                .collect(Collectors.toMap(idGetter, JSON::toJSONString));
        Multimap<K, String> treeMap = LinkedListMultimap.create();
        for (V data : dataList) {
            treeMap.put(parentIdGetter.apply(data), JSON.toJSONString(data));
        }
        Set<Map.Entry<K, Collection<String>>> entries = treeMap.asMap().entrySet();
        String exceptionMessage;
        try {
            if (this.lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataMap.putAll(map, 1000);
                    for (Map.Entry<K, Collection<String>> entry : entries) {
                        allTreeMap.removeAll(entry.getKey());
                        allTreeMap.putAll(entry.getKey(), entry.getValue());
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisTreeDictionaryCache::putAll] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
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
        if (id == null || data == null) {
            return;
        }
        String stringData = JSON.toJSONString(data);
        K parentId = parentIdGetter.apply(data);
        String exceptionMessage;
        try {
            if (this.lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    String oldData = dataMap.put(id, stringData);
                    if (oldData != null) {
                        allTreeMap.remove(parentId, oldData);
                    }
                    allTreeMap.put(parentId, stringData);
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisTreeDictionaryCache::put] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
    }

    /**
     * 行为等同于接口中的描述。这里之所以重写，是因为不确定redisson的读写锁，
     * 是否为可重入锁。
     *
     * @param dataList 待缓存的数据列表。
     * @param force    true则强制刷新，如果false，当缓存中存在数据时不刷新。
     */
    @Override
    public void reload(List<V> dataList, boolean force) {
        // 锁外执行数据结构组装，降低锁的粒度，提高并发性。
        Map<K, String> map = null;
        Set<Map.Entry<K, Collection<String>>> entries = null;
        if (CollectionUtils.isNotEmpty(dataList)) {
            map = dataList.stream().collect(Collectors.toMap(idGetter, JSON::toJSONString));
            Multimap<K, String> treeMap = LinkedListMultimap.create();
            for (V data : dataList) {
                treeMap.put(parentIdGetter.apply(data), JSON.toJSONString(data));
            }
            entries = treeMap.asMap().entrySet();
        }
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    // 如果不强制刷新，需要先判断缓存中是否存在数据。
                    if (!force && this.getCount() > 0) {
                        return;
                    }
                    dataMap.clear();
                    allTreeMap.clear();
                    if (map != null) {
                        dataMap.putAll(map, 1000);
                        for (Map.Entry<K, Collection<String>> entry : entries) {
                            allTreeMap.removeAll(entry.getKey());
                            allTreeMap.putAll(entry.getKey(), entry.getValue());
                        }
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::reload] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
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
        if (id == null) {
            return null;
        }
        V data = null;
        String exceptionMessage;
        try {
            if (this.lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    String stringData = dataMap.remove(id);
                    if (stringData != null) {
                        data = JSON.parseObject(stringData, valueClazz);
                        K parentId = parentIdGetter.apply(data);
                        allTreeMap.remove(parentId, stringData);
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisTreeDictionaryCache::invalidate] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
        return data;
    }

    /**
     * 删除缓存中，参数列表中包含的键。
     *
     * @param keys 待删除数据的主键集合。
     */
    @Override
    public void invalidateSet(Set<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    keys.forEach(id -> {
                        if (id != null) {
                            String stringData = dataMap.remove(id);
                            if (stringData != null) {
                                K parentId = parentIdGetter.apply(JSON.parseObject(stringData, valueClazz));
                                allTreeMap.remove(parentId, stringData);
                            }
                        }
                    });
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisTreeDictionaryCache::invalidateSet] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
    }

    /**
     * 清空缓存。
     */
    @Override
    public void invalidateAll() {
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataMap.clear();
                    allTreeMap.clear();
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisTreeDictionaryCache::invalidateAll] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
    }
}
