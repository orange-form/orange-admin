package com.orangeforms.uaaadmin.service.impl;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.uaaadmin.service.AuthClientDetailsService;
import com.orangeforms.uaaadmin.dao.AuthClientDetailsMapper;
import com.orangeforms.uaaadmin.model.AuthClientDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 应用客户端数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service("authClientDetailsService")
public class AuthClientDetailsServiceImpl extends BaseService<AuthClientDetails, String> implements AuthClientDetailsService {

    @Autowired
    private AuthClientDetailsMapper authClientDetailsMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<AuthClientDetails> mapper() {
        return authClientDetailsMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param authClientDetails 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthClientDetails saveNew(AuthClientDetails authClientDetails) {
        authClientDetails.setClientSecret(passwordEncoder.encode(authClientDetails.getClientSecretPlain()));
        MyModelUtil.fillCommonsForInsert(authClientDetails);
        authClientDetailsMapper.insert(authClientDetails);
        return authClientDetails;
    }

    /**
     * 更新数据对象。
     *
     * @param authClientDetails         更新的对象。
     * @param originalAuthClientDetails 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(AuthClientDetails authClientDetails, AuthClientDetails originalAuthClientDetails) {
        authClientDetails.setClientSecret(originalAuthClientDetails.getClientSecret());
        authClientDetails.setClientSecretPlain(originalAuthClientDetails.getClientSecretPlain());
        MyModelUtil.fillCommonsForUpdate(authClientDetails, originalAuthClientDetails);
        return authClientDetailsMapper.updateById(authClientDetails) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param clientId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(String clientId) {
        return authClientDetailsMapper.deleteById(clientId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getAuthClientDetailsListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<AuthClientDetails> getAuthClientDetailsList(AuthClientDetails filter, String orderBy) {
        return authClientDetailsMapper.getAuthClientDetailsList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getAuthClientDetailsList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<AuthClientDetails> getAuthClientDetailsListWithRelation(AuthClientDetails filter, String orderBy) {
        List<AuthClientDetails> resultList = authClientDetailsMapper.getAuthClientDetailsList(filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.normal());
        return resultList;
    }
}
