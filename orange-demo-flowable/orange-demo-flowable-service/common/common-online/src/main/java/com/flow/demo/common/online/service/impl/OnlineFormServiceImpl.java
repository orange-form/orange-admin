package com.flow.demo.common.online.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.core.base.service.BaseService;
import com.flow.demo.common.core.object.CallResult;
import com.flow.demo.common.core.object.MyRelationParam;
import com.flow.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.flow.demo.common.online.dao.OnlineFormDatasourceMapper;
import com.flow.demo.common.online.dao.OnlineFormMapper;
import com.flow.demo.common.online.model.OnlineForm;
import com.flow.demo.common.online.model.OnlineFormDatasource;
import com.flow.demo.common.online.service.OnlineFormService;
import com.flow.demo.common.online.service.OnlinePageService;
import com.flow.demo.common.online.service.OnlineTableService;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线表单数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("onlineFormService")
public class OnlineFormServiceImpl extends BaseService<OnlineForm, Long> implements OnlineFormService {

    @Autowired
    private OnlineFormMapper onlineFormMapper;
    @Autowired
    private OnlineFormDatasourceMapper onlineFormDatasourceMapper;
    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlinePageService onlinePageService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<OnlineForm> mapper() {
        return onlineFormMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param onlineForm      新增对象。
     * @param datasourceIdSet 在线表单关联的数据源Id集合。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OnlineForm saveNew(OnlineForm onlineForm, Set<Long> datasourceIdSet) {
        onlineForm.setFormId(idGenerator.nextLongId());
        Date now = new Date();
        onlineForm.setUpdateTime(now);
        onlineForm.setCreateTime(now);
        onlineFormMapper.insert(onlineForm);
        if (CollUtil.isNotEmpty(datasourceIdSet)) {
            for (Long datasourceId : datasourceIdSet) {
                OnlineFormDatasource onlineFormDatasource = new OnlineFormDatasource();
                onlineFormDatasource.setId(idGenerator.nextLongId());
                onlineFormDatasource.setFormId(onlineForm.getFormId());
                onlineFormDatasource.setDatasourceId(datasourceId);
                onlineFormDatasourceMapper.insert(onlineFormDatasource);
            }
        }
        return onlineForm;
    }

    /**
     * 更新数据对象。
     *
     * @param onlineForm         更新的对象。
     * @param originalOnlineForm 原有数据对象。
     * @param datasourceIdSet    在线表单关联的数据源Id集合。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(OnlineForm onlineForm, OnlineForm originalOnlineForm, Set<Long> datasourceIdSet) {
        onlineForm.setUpdateTime(new Date());
        onlineForm.setCreateTime(originalOnlineForm.getCreateTime());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<OnlineForm> uw = this.createUpdateQueryForNullValue(onlineForm, onlineForm.getFormId());
        if (onlineFormMapper.update(onlineForm, uw) != 1) {
            return false;
        }
        OnlineFormDatasource formDatasourceFilter = new OnlineFormDatasource();
        formDatasourceFilter.setFormId(onlineForm.getFormId());
        onlineFormDatasourceMapper.delete(new QueryWrapper<>(formDatasourceFilter));
        if (CollUtil.isNotEmpty(datasourceIdSet)) {
            for (Long datasourceId : datasourceIdSet) {
                OnlineFormDatasource onlineFormDatasource = new OnlineFormDatasource();
                onlineFormDatasource.setId(idGenerator.nextLongId());
                onlineFormDatasource.setFormId(onlineForm.getFormId());
                onlineFormDatasource.setDatasourceId(datasourceId);
                onlineFormDatasourceMapper.insert(onlineFormDatasource);
            }
        }
        return true;
    }

    /**
     * 删除指定数据。
     *
     * @param formId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long formId) {
        if (onlineFormMapper.deleteById(formId) != 1) {
            return false;
        }
        OnlineFormDatasource formDatasourceFilter = new OnlineFormDatasource();
        formDatasourceFilter.setFormId(formId);
        onlineFormDatasourceMapper.delete(new QueryWrapper<>(formDatasourceFilter));
        return true;
    }

    /**
     * 根据PageId，删除其所属的所有表单，以及表单关联的数据源数据。
     *
     * @param pageId 指定的pageId。
     * @return 删除数量。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByPageId(Long pageId) {
        OnlineForm filter = new OnlineForm();
        filter.setPageId(pageId);
        List<OnlineForm> formList = onlineFormMapper.selectList(new QueryWrapper<>(filter));
        Set<Long> formIdSet = formList.stream().map(OnlineForm::getFormId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(formIdSet)) {
            onlineFormDatasourceMapper.delete(
                    new QueryWrapper<OnlineFormDatasource>().lambda().in(OnlineFormDatasource::getFormId, formIdSet));
        }
        return onlineFormMapper.delete(new QueryWrapper<>(filter));
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineFormListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineForm> getOnlineFormList(OnlineForm filter, String orderBy) {
        return onlineFormMapper.getOnlineFormList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineFormList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineForm> getOnlineFormListWithRelation(OnlineForm filter, String orderBy) {
        List<OnlineForm> resultList = onlineFormMapper.getOnlineFormList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 获取使用指定数据表的表单列表。
     *
     * @param tableId 数据表Id。
     * @return 使用该数据表的表单列表。
     */
    @Override
    public List<OnlineForm> getOnlineFormListByTableId(Long tableId) {
        OnlineForm filter = new OnlineForm();
        filter.setMasterTableId(tableId);
        return onlineFormMapper.selectList(new QueryWrapper<>(filter));
    }

    /**
     * 获取指定表单的数据源列表。
     *
     * @param formId 指定的表单。
     * @return 表单和数据源的多对多关联对象列表。
     */
    @Override
    public List<OnlineFormDatasource> getFormDatasourceListByFormId(Long formId) {
        return onlineFormDatasourceMapper.selectList(
                new QueryWrapper<OnlineFormDatasource>().lambda().eq(OnlineFormDatasource::getFormId, formId));
    }

    /**
     * 查询正在使用当前数据源的表单。
     *
     * @param datasourceId 数据源Id。
     * @return 正在使用当前数据源的表单列表。
     */
    @Override
    public List<OnlineForm> getOnlineFormListByDatasourceId(Long datasourceId) {
        List<OnlineFormDatasource> formDatasourceList = onlineFormDatasourceMapper.selectList(
                new QueryWrapper<OnlineFormDatasource>().lambda().eq(OnlineFormDatasource::getDatasourceId, datasourceId));
        if (CollUtil.isEmpty(formDatasourceList)) {
            return new LinkedList<>();
        }
        Collection<Long> formIdSet = formDatasourceList.stream()
                .map(OnlineFormDatasource::getFormId).collect(Collectors.toSet());
        return onlineFormMapper.selectList(
                new QueryWrapper<OnlineForm>().lambda().in(OnlineForm::getFormId, formIdSet));
    }

    @Override
    public List<OnlineForm> getOnlineFormListByPageIds(Set<Long> pageIdSet) {
        LambdaQueryWrapper<OnlineForm> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OnlineForm::getPageId, pageIdSet);
        return onlineFormMapper.selectList(queryWrapper);
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param onlineForm         最新数据对象。
     * @param originalOnlineForm 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    @Override
    public CallResult verifyRelatedData(OnlineForm onlineForm, OnlineForm originalOnlineForm) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(onlineForm, originalOnlineForm, OnlineForm::getMasterTableId)
                && !onlineTableService.existId(onlineForm.getMasterTableId())) {
            return CallResult.error(String.format(errorMessageFormat, "表单主表id"));
        }
        //这里是一对多的验证
        if (this.needToVerify(onlineForm, originalOnlineForm, OnlineForm::getPageId)
                && !onlinePageService.existId(onlineForm.getPageId())) {
            return CallResult.error(String.format(errorMessageFormat, "页面id"));
        }
        return CallResult.ok();
    }
}
