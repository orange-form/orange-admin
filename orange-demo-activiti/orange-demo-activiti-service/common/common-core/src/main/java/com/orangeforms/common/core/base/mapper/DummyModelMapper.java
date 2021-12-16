package com.orangeforms.common.core.base.mapper;

import java.util.List;

/**
 * 哑元占位对象。Model实体对象和Domain域对象相同的场景下使用。
 * 由于没有实际的数据转换，因此同时保证了代码统一和执行效率。
 *
 * @param <M> 数据类型。
 * @author Jerry
 * @date 2021-06-06
 */
public class DummyModelMapper<M> implements BaseModelMapper<M, M> {

    /**
     * 不转换直接返回。
     *
     * @param model Model实体对象。
     * @return Domain域对象。
     */
    @Override
    public M fromModel(M model) {
        return model;
    }

    /**
     * 不转换直接返回。
     *
     * @param modelList Model实体对象列表。
     * @return Domain域对象列表。
     */
    @Override
    public List<M> fromModelList(List<M> modelList) {
        return modelList;
    }

    /**
     * 不转换直接返回。
     *
     * @param domain Domain域对象。
     * @return Model实体对象。
     */
    @Override
    public M toModel(M domain) {
        return domain;
    }

    /**
     * 不转换直接返回。
     *
     * @param domainList Domain域对象列表。
     * @return Model实体对象列表。
     */
    @Override
    public List<M> toModelList(List<M> domainList) {
        return domainList;
    }
}
