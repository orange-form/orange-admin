package com.orangeforms.common.redis.cache;

import com.alibaba.fastjson.JSON;
import com.orangeforms.common.core.cache.DictionaryCache;
import com.orangeforms.common.core.constant.ApplicationConstant;
import com.orangeforms.common.core.exception.RedisCacheAccessException;
import com.orangeforms.common.core.object.TokenData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 租户字典数据Redis缓存对象。
 *
 * @param <K> 字典表主键类型。
 * @param <V> 字典表对象类型。
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class RedisTenantDictionaryCache<K, V> implements DictionaryCache<K, V> {

    /**
     * redisson客户端。
     */
    protected final RedissonClient redissonClient;
    /**
     * 字典名称。
     */
    protected final String dictionaryName;
    /**
     * 字典值对象类型。
     */
    protected final Class<V> valueClazz;
    /**
     * 由于大部分场景是读取操作，所以使用读写锁提高并发的伸缩性。
     */
    protected final ReadWriteLock lock;
    /**
     * 获取字典主键数据的函数对象。
     */
    protected final Function<V, K> idGetter;
    /**
     * 超时时长。单位毫秒。
     */
    protected static final long TIMEOUT = 2000L;

    /**
     * 当前对象的构造器函数。
     *
     * @param redissonClient Redisson的客户端对象。
     * @param valueClazz     值对象的Class对象。
     * @param idGetter       获取当前类主键字段值的函数对象。
     * @param <K>            字典主键类型。
     * @param <V>            字典对象类型
     * @return 实例化后的字典内存缓存对象。
     */
    public static <K, V> RedisTenantDictionaryCache<K, V> create(
            RedissonClient redissonClient,
            String dictionaryName,
            Class<V> valueClazz,
            Function<V, K> idGetter) {
        if (idGetter == null) {
            throw new IllegalArgumentException("IdGetter can't be NULL.");
        }
        return new RedisTenantDictionaryCache<>(redissonClient, dictionaryName, valueClazz, idGetter);
    }

    /**
     * 构造函数。
     *
     * @param redissonClient Redisson的客户端对象。
     * @param valueClazz     值对象的Class对象。
     * @param idGetter       获取当前类主键字段值的函数对象。
     */
    public RedisTenantDictionaryCache(
            RedissonClient redissonClient,
            String dictionaryName,
            Class<V> valueClazz,
            Function<V, K> idGetter) {
        this.redissonClient = redissonClient;
        this.lock = new ReentrantReadWriteLock();
        this.dictionaryName = dictionaryName;
        this.valueClazz = valueClazz;
        this.idGetter = idGetter;
    }

    /**
     * 获取租户的字典数据Map。
     *
     * @return 当前租户的字典数据Map。
     */
    protected RMap<K, String> getTenantDataMap() {
        Long tenantId = TokenData.takeFromRequest().getTenantId();
        StringBuilder s = new StringBuilder(64);
        s.append(dictionaryName).append("-")
                .append(tenantId).append(ApplicationConstant.TREE_DICT_CACHE_NAME_SUFFIX);
        return redissonClient.getMap(s.toString());
    }

    /**
     * 按照数据插入的顺序返回全部字典对象的列表。
     *
     * @return 全部字段数据列表。
     */
    @Override
    public List<V> getAll() {
        Collection<String> dataList;
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataList = this.getTenantDataMap().readAllValues();
                } finally {
                    // 如果上面的操作时间超过redisson.lockWatchdogTimeout的时长，
                    // redis会将与该锁关联的键删除，此后调用unlock的时候，就会抛出运行时异常。
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::getAll] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
        if (CollectionUtils.isEmpty(dataList)) {
            return new LinkedList<>();
        }
        return dataList.stream()
                .map(data -> JSON.parseObject(data, valueClazz))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 获取缓存中与键列表对应的对象列表。
     *
     * @param keys 主键集合。
     * @return 对象列表。
     */
    @Override
    public List<V> getInList(Set<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new LinkedList<>();
        }
        Collection<String> dataList = null;
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    Map<K, String> m = this.getTenantDataMap().getAll(keys);
                    if (m != null) {
                        dataList = m.values();
                    }
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::getInList] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
        if (dataList == null) {
            return new LinkedList<>();
        }
        return dataList.stream()
                .map(data -> JSON.parseObject(data, valueClazz))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 从缓存中获取指定的数据。
     *
     * @param id 数据的key。
     * @return 获取到的数据，如果没有返回null。
     */
    @Override
    public V get(K id) {
        if (id == null) {
            return null;
        }
        String data;
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    data = this.getTenantDataMap().get(id);
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::get] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
        if (data == null) {
            return null;
        }
        return JSON.parseObject(data, valueClazz);
    }

    /**
     * 获取缓存中数据条目的数量。
     *
     * @return 返回缓存的数据数量。
     */
    @Override
    public int getCount() {
        return this.getTenantDataMap().size();
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
        Map<K, String> map = dataList.stream()
                .collect(Collectors.toMap(idGetter, JSON::toJSONString));
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    this.getTenantDataMap().putAll(map, 1000);
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::putAll] encountered EXCEPTION [%s] for DICT [%s].",
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
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    this.getTenantDataMap().fastPut(id, JSON.toJSONString(data));
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::put] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
    }

    /**
     * 重新加载，先清空原有数据，在执行putAll的操作。
     *
     * @param dataList 待缓存的数据列表。
     * @param force    true则强制刷新，如果false，当缓存中存在数据时不刷新。
     */
    @Override
    public void reload(List<V> dataList, boolean force) {
        Map<K, String> map = null;
        if (CollectionUtils.isNotEmpty(dataList)) {
            map = dataList.stream().collect(Collectors.toMap(idGetter, JSON::toJSONString));
        }
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    // 如果不强制刷新，需要先判断缓存中是否存在数据。
                    if (!force && this.getCount() > 0) {
                        return;
                    }
                    RMap<K, String> tenantDataMap = this.getTenantDataMap();
                    tenantDataMap.clear();
                    if (map != null) {
                        tenantDataMap.putAll(map, 1000);
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
        String data = null;
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    this.getTenantDataMap().remove(id);
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::invalidate] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
        if (data == null) {
            return null;
        }
        return JSON.parseObject(data, valueClazz);
    }

    /**
     * 删除缓存中，参数列表中包含的键。
     *
     * @param keys 待删除数据的主键集合。
     */
    @SuppressWarnings("unchecked")
    @Override
    public void invalidateSet(Set<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        Object[] keyArray = keys.toArray(new Object[]{});
        String exceptionMessage;
        try {
            if (lock.writeLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    this.getTenantDataMap().fastRemove((K[]) keyArray);
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::invalidateSet] encountered EXCEPTION [%s] for DICT [%s].",
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
                    this.getTenantDataMap().clear();
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            exceptionMessage = String.format(
                    "LOCK Operation of [RedisDictionaryCache::invalidateAll] encountered EXCEPTION [%s] for DICT [%s].",
                    e.getClass().getSimpleName(), valueClazz.getSimpleName());
            log.warn(exceptionMessage);
            throw new RedisCacheAccessException(exceptionMessage, e);
        }
    }
}
