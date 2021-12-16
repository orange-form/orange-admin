package com.orangeforms.common.log.service.impl;

import com.orangeforms.common.core.annotation.MyDataSource;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.constant.ApplicationConstant;
import com.orangeforms.common.log.dao.SysOperationLogMapper;
import com.orangeforms.common.log.model.SysOperationLog;
import com.orangeforms.common.log.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志服务实现类。
 * 这里需要重点解释下MyDataSource注解。在单数据源服务中，由于没有DataSourceAspect的切面类，所以该注解不会
 * 有任何作用和影响。然而在多数据源情况下，由于每个服务都有自己的DataSourceType常量对象，表示不同的数据源。
 * 而common-log在公用模块中，不能去依赖业务服务，因此这里给出了一个固定值。我们在业务的DataSourceType中，也要
 * 使用该值ApplicationConstant.OPERATION_LOG_DATASOURCE_TYPE，去关联操作日志所需的数据源配置。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@MyDataSource(ApplicationConstant.OPERATION_LOG_DATASOURCE_TYPE)
@Service
public class SysOperationLogServiceImpl extends BaseService<SysOperationLog, Long> implements SysOperationLogService {

    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    @Override
    protected BaseDaoMapper<SysOperationLog> mapper() {
        return sysOperationLogMapper;
    }

    /**
     * 异步插入一条新操作日志。通常用于在橙单中创建的单体工程服务。
     *
     * @param operationLog 操作日志对象。
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewAsync(SysOperationLog operationLog) {
        sysOperationLogMapper.insert(operationLog);
    }

    /**
     *  插入一条新操作日志。
     *
     * @param operationLog 操作日志对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNew(SysOperationLog operationLog) {
        sysOperationLogMapper.insert(operationLog);
    }

    /**
     * 批量插入。通常用于在橙单中创建的微服务工程服务。
     *
     * @param sysOperationLogList 操作日志列表。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchSave(List<SysOperationLog> sysOperationLogList) {
        sysOperationLogMapper.insertList(sysOperationLogList);
    }

    /**
     * 根据过滤条件和排序规则，查询操作日志。
     *
     * @param filter  操作日志的过滤对象。
     * @param orderBy 排序规则。
     * @return 查询列表。
     */
    @Override
    public List<SysOperationLog> getSysOperationLogList(SysOperationLog filter, String orderBy) {
        return sysOperationLogMapper.getSysOperationLogList(filter, orderBy);
    }
}
