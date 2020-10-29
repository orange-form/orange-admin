package com.orange.demo.common.core.base.service;

import com.orange.demo.common.core.cache.DictionaryCache;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Set;
import java.util.List;

/**
 * 带有缓存功能的字典Service基类，需要留意的是，由于缓存基于Key/Value方式存储，
 * 目前仅支持基于主键字段的缓存查找，其他条件的查找仍然从数据源获取。
 *
 * @param <M> Model实体对象的类型。
 * @param <K> Model对象主键的类型。
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
public abstract class BaseDictService<M, K> extends BaseService<M, K> {

    /**
     * 缓存池对象。
     */
    protected DictionaryCache<K, M> dictionaryCache;

    /**
     * 构造函数使用缺省缓存池对象。
     */
    public BaseDictService() {
        super();
    }

    /**
     * 是否在服务启动的时候加载。子类可以重载该方法，并在需要的时候手工调用loadCachedData加载数据。
     *
     * @return true表示启动即可加载数据，false需要手动调用loadCachedData进行加载。
     */
    public boolean loadOnStartup() {
        return true;
    }

    /**
     * 在系统启动时，加载全部数据到内存，缓存的key只能为映射表的主键。
     */
    public void loadCachedData() {
        if (loadOnStartup()) {
            reloadCachedData(false);
        }
    }

    /**
     * 重新加载数据库中所有当前表数据到系统内存。
     *
     * @param force true则强制刷新，如果false，当缓存中存在数据时不刷新。
     */
    public void reloadCachedData(boolean force) {
        // 在非强制刷新情况下。
        // 先行判断缓存中是否存在数据，如果有就不加载了。
        if (!force && dictionaryCache.getCount() > 0) {
            return;
        }
        List<M> allList = super.getAllList();
        dictionaryCache.reload(allList, force);
    }

    /**
     * 直接从缓存池中获取主键Id关联的数据。
     *
     * @param id 主键Id。
     * @return 主键关联的数据，不存在返回null。
     */
    @Override
    public M getById(K id) {
        return dictionaryCache.get(id);
    }

    /**
     * 直接从缓存池中获取所有数据。
     *
     * @return 返回所有数据。
     */
    @Override
    public List<M> getAllList() {
        return dictionaryCache.getAll();
    }

    /**
     * 直接从缓存池中返回符合主键 in (idValues) 条件的所有数据。
     *
     * @param idValues 主键值列表。
     * @return 检索后的数据列表。
     */
    @Override
    public List<M> getInList(Set<K> idValues) {
        return dictionaryCache.getInList(idValues);
    }

    /**
     * 返回符合 inFilterField in (inFilterValues) 条件的所有数据。蜀国property是主键，则从缓存中读取。
     *
     * @param inFilterField  参与(In-list)过滤的Java字段。
     * @param inFilterValues 参与(In-list)过滤的Java字段值集合。
     * @return 检索后的数据列表。
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<M> getInList(String inFilterField, Set<T> inFilterValues) {
        if (inFilterField.equals(this.idFieldName)) {
            return this.getInList((Set<K>) inFilterValues);
        }
        return this.getInList(inFilterField, inFilterValues, (String) null);
    }

    /**
     * 判断参数值列表中的所有数据，是否全部存在。另外，keyName字段在数据表中必须是唯一键值，否则返回结果会出现误判。
     *
     * @param inFilterField  待校验的数据字段，这里使用Java对象中的属性，如courseId，而不是数据字段名course_id。
     * @param inFilterValues 数据值集合。
     * @return 全部存在返回true，否则false。
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean existUniqueKeyList(String inFilterField, Set<T> inFilterValues) {
        if (CollectionUtils.isEmpty(inFilterValues)) {
            return false;
        }
        if (inFilterField.equals(this.idFieldName)) {
            List<M> dataList = dictionaryCache.getInList((Set<K>) inFilterValues);
            return dataList.size() == inFilterValues.size();
        }
        Example e = this.makeDefaultInListExample(inFilterField, inFilterValues, null);
        if (deletedFlagFieldName != null) {
            e.and().andEqualTo(deletedFlagFieldName, GlobalDeletedFlag.NORMAL);
        }
        return mapper().selectCountByExample(e) == inFilterValues.size();
    }

    /**
     * 获取缓存中的数据数量。
     *
     * @return 缓存中的数据总量。
     */
    public int getCachedCount() {
        return dictionaryCache.getCount();
    }
}
