package com.flow.demo.common.core.cache;

import com.flow.demo.common.core.exception.MapCacheAccessException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * 字典数据内存缓存对象。
 *
 * @param <K> 字典表主键类型。
 * @param <V> 字典表对象类型。
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class MapDictionaryCache<K, V> implements DictionaryCache<K, V> {

    /**
     * 存储字典数据的Map。
     */
    protected final LinkedHashMap<K, V> dataMap = new LinkedHashMap<>();
    /**
     * 获取字典主键数据的函数对象。
     */
    protected final Function<V, K> idGetter;
    /**
     * 由于大部分场景是读取操作，所以使用读写锁提高并发的伸缩性。
     */
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * 超时时长。单位毫秒。
     */
    protected static final long TIMEOUT = 2000L;

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
    public List<V> getAll() {
        List<V> resultList = new LinkedList<>();
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    for (Map.Entry<K, V> entry : dataMap.entrySet()) {
                        resultList.add(entry.getValue());
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
        return resultList;
    }

    /**
     * 获取缓存中与键列表对应的对象列表。
     *
     * @param keys 主键集合。
     * @return 对象列表。
     */
    @Override
    public List<V> getInList(Set<K> keys) {
        List<V> resultList = new LinkedList<>();
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    keys.forEach(key -> {
                        V object = dataMap.get(key);
                        if (object != null) {
                            resultList.add(object);
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
                    dataList.forEach(dataObj -> {
                        K id = idGetter.apply(dataObj);
                        dataMap.put(id, dataObj);
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
                    dataList.forEach(dataObj -> {
                        K id = idGetter.apply(dataObj);
                        dataMap.put(id, dataObj);
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
        V data;
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    data = dataMap.get(id);
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
        return data;
    }

    /**
     * 将数据存入缓存。
     *
     * @param id     通常为字典数据的主键。
     * @param object 字典数据对象。
     */
    @Override
    public void put(K id, V object) {
        String exceptionMessage;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    dataMap.put(id, object);
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
     * 获取缓存中数据条目的数量。
     *
     * @return 返回缓存的数据数量。
     */
    @Override
    public int getCount() {
        return dataMap.size();
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
        String exceptionMessage;
        V data;
        try {
            if (lock.readLock().tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    data = dataMap.remove(id);
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
        return data;
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
                            dataMap.remove(id);
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
