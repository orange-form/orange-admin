package com.orangeforms.webadmin.upms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.datafilter.constant.DataPermRuleType;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.RedisKeyUtil;
import com.orangeforms.webadmin.config.ApplicationConfig;
import com.orangeforms.webadmin.upms.dao.SysDataPermDeptMapper;
import com.orangeforms.webadmin.upms.dao.SysDataPermMapper;
import com.orangeforms.webadmin.upms.dao.SysDataPermUserMapper;
import com.orangeforms.webadmin.upms.model.*;
import com.orangeforms.webadmin.upms.service.SysDataPermService;
import com.orangeforms.webadmin.upms.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据权限数据服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("sysDataPermService")
public class SysDataPermServiceImpl extends BaseService<SysDataPerm, Long> implements SysDataPermService {

    @Autowired
    private SysDataPermMapper sysDataPermMapper;
    @Autowired
    private SysDataPermDeptMapper sysDataPermDeptMapper;
    @Autowired
    private SysDataPermUserMapper sysDataPermUserMapper;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysDataPerm> mapper() {
        return sysDataPermMapper;
    }

    /**
     * 保存新增的数据权限对象。
     *
     * @param dataPerm         新增的数据权限对象。
     * @param deptIdSet        关联的部门Id列表。
     * @return 新增后的数据权限对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysDataPerm saveNew(SysDataPerm dataPerm, Set<Long> deptIdSet) {
        dataPerm.setDataPermId(idGenerator.nextLongId());
        MyModelUtil.fillCommonsForInsert(dataPerm);
        dataPerm.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        sysDataPermMapper.insert(dataPerm);
        this.insertRelationData(dataPerm, deptIdSet);
        return dataPerm;
    }

    /**
     * 更新数据权限对象。
     *
     * @param dataPerm         更新的数据权限对象。
     * @param originalDataPerm 原有的数据权限对象。
     * @param deptIdSet        关联的部门Id列表。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysDataPerm dataPerm, SysDataPerm originalDataPerm, Set<Long> deptIdSet) {
        MyModelUtil.fillCommonsForUpdate(dataPerm, originalDataPerm);
        UpdateWrapper<SysDataPerm> uw = this.createUpdateQueryForNullValue(dataPerm, dataPerm.getDataPermId());
        if (sysDataPermMapper.update(dataPerm, uw) != 1) {
            return false;
        }
        SysDataPermDept dataPermDept = new SysDataPermDept();
        dataPermDept.setDataPermId(dataPerm.getDataPermId());
        sysDataPermDeptMapper.delete(new QueryWrapper<>(dataPermDept));
        this.insertRelationData(dataPerm, deptIdSet);
        return true;
    }

    /**
     * 删除指定数据权限。
     *
     * @param dataPermId 数据权限主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long dataPermId) {
        if (sysDataPermMapper.deleteById(dataPermId) != 1) {
            return false;
        }
        SysDataPermDept dataPermDept = new SysDataPermDept();
        dataPermDept.setDataPermId(dataPermId);
        sysDataPermDeptMapper.delete(new QueryWrapper<>(dataPermDept));
        SysDataPermUser dataPermUser = new SysDataPermUser();
        dataPermUser.setDataPermId(dataPermId);
        sysDataPermUserMapper.delete(new QueryWrapper<>(dataPermUser));
        return true;
    }

    /**
     * 获取数据权限列表。
     *
     * @param filter  数据权限过滤对象。
     * @param orderBy 排序参数。
     * @return 数据权限查询列表。
     */
    @Override
    public List<SysDataPerm> getSysDataPermList(SysDataPerm filter, String orderBy) {
        return sysDataPermMapper.getSysDataPermList(filter, orderBy);
    }

    /**
     * 将指定用户的指定会话的数据权限集合存入缓存。
     *
     * @param sessionId 会话Id。
     * @param userId    用户主键Id。
     * @param deptId    用户所属部门主键Id。
     * @return 查询并缓存后的数据权限集合。返回格式为，Map<RuleType, DeptIdString>。
     */
    @Override
    public Map<Integer, String> putDataPermCache(String sessionId, Long userId, Long deptId) {
        Map<Integer, String> dataPermMap = this.getSysDataPermListByUserId(userId, deptId);
        if (dataPermMap.size() > 0) {
            String dataPermSessionKey = RedisKeyUtil.makeSessionDataPermIdKey(sessionId);
            RBucket<String> bucket = redissonClient.getBucket(dataPermSessionKey);
            bucket.set(JSON.toJSONString(dataPermMap),
                    applicationConfig.getSessionExpiredSeconds(), TimeUnit.SECONDS);
        }
        return dataPermMap;
    }

    /**
     * 将指定会话的数据权限集合从缓存中移除。
     *
     * @param sessionId 会话Id。
     */
    @Override
    public void removeDataPermCache(String sessionId) {
        String sessionPermKey = RedisKeyUtil.makeSessionDataPermIdKey(sessionId);
        redissonClient.getBucket(sessionPermKey).deleteAsync();
    }

    /**
     * 获取指定用户Id的数据权限列表。并基于权限规则类型进行了一级分组。
     *
     * @param userId 指定的用户Id。
     * @param deptId 用户所属部门主键Id。
     * @return 合并优化后的数据权限列表。返回格式为，Map<RuleType, DeptIdString>。
     */
    @Override
    public Map<Integer, String> getSysDataPermListByUserId(Long userId, Long deptId) {
        List<SysDataPerm> dataPermList = sysDataPermMapper.getSysDataPermListByUserId(userId);
        dataPermList.forEach(dataPerm -> {
            if (CollectionUtils.isNotEmpty(dataPerm.getDataPermDeptList())) {
                Set<Long> deptIdSet = dataPerm.getDataPermDeptList().stream()
                        .map(SysDataPermDept::getDeptId).collect(Collectors.toSet());
                dataPerm.setDeptIdListString(StringUtils.join(deptIdSet, ","));
            }
        });
        // 为了更方便进行后续的合并优化处理，这里再基于规则类型进行分组。ruleMap的key是规则类型。
        Map<Integer, List<SysDataPerm>> ruleMap =
                dataPermList.stream().collect(Collectors.groupingBy(SysDataPerm::getRuleType));
        Map<Integer, String> resultMap = new HashMap<>(ruleMap.size());
        // 如有有ALL存在，就可以直接退出了，没有必要在处理后续的规则了。
        if (ruleMap.containsKey(DataPermRuleType.TYPE_ALL)) {
            resultMap.put(DataPermRuleType.TYPE_ALL, "null");
            return resultMap;
        }
        // 这里优先合并最复杂的多部门及子部门场景。
        String deptIds = processMultiDeptAndChildren(ruleMap, deptId);
        if (deptIds != null) {
            resultMap.put(DataPermRuleType.TYPE_MULTI_DEPT_AND_CHILD_DEPT, deptIds);
        }
        // 合并当前部门及子部门的优化
        if (ruleMap.get(DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT) != null) {
            // 需要与仅仅当前部门规则进行合并。
            ruleMap.remove(DataPermRuleType.TYPE_DEPT_ONLY);
            resultMap.put(DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT, "null");
        }
        // 合并自定义部门了。
        deptIds = processMultiDept(ruleMap, deptId);
        if (deptIds != null) {
            resultMap.put(DataPermRuleType.TYPE_CUSTOM_DEPT_LIST, deptIds);
        }
        // 最后处理当前部门和当前用户。
        if (ruleMap.get(DataPermRuleType.TYPE_DEPT_ONLY) != null) {
            resultMap.put(DataPermRuleType.TYPE_DEPT_ONLY, "null");
        }
        if (ruleMap.get(DataPermRuleType.TYPE_USER_ONLY) != null) {
            resultMap.put(DataPermRuleType.TYPE_USER_ONLY, "null");
        }
        return resultMap;
    }

    private String processMultiDeptAndChildren(Map<Integer, List<SysDataPerm>> ruleMap, Long deptId) {
        List<SysDataPerm> parentDeptList = ruleMap.get(DataPermRuleType.TYPE_MULTI_DEPT_AND_CHILD_DEPT);
        if (parentDeptList == null) {
            return null;
        }
        Set<Long> deptIdSet = new HashSet<>();
        for (SysDataPerm parentDept : parentDeptList) {
            deptIdSet.addAll(Arrays.stream(StringUtils.split(
                    parentDept.getDeptIdListString(), ",")).map(Long::valueOf).collect(Collectors.toSet()));
        }
        // 在合并所有的多父部门Id之后，需要判断是否有本部门及子部门的规则。如果有，就继续合并。
        if (ruleMap.containsKey(DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT)) {
            // 如果多父部门列表中包含当前部门，那么可以直接删除该规则了，如果没包含，就加入到多部门的DEPT_ID的IN LIST中。
            deptIdSet.add(deptId);
            ruleMap.remove(DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT);
        }
        // 需要与仅仅当前部门规则进行合并。
        if (ruleMap.containsKey(DataPermRuleType.TYPE_DEPT_ONLY)) {
            if (deptIdSet.contains(deptId)) {
                ruleMap.remove(DataPermRuleType.TYPE_DEPT_ONLY);
            }
        }
        return StringUtils.join(deptIdSet, ',');
    }

    private String processMultiDept(Map<Integer, List<SysDataPerm>> ruleMap, Long deptId) {
        List<SysDataPerm> customDeptList = ruleMap.get(DataPermRuleType.TYPE_CUSTOM_DEPT_LIST);
        if (customDeptList == null) {
            return null;
        }
        Set<Long> deptIdSet = new HashSet<>();
        for (SysDataPerm customDept : customDeptList) {
            deptIdSet.addAll(Arrays.stream(StringUtils.split(
                    customDept.getDeptIdListString(), ",")).map(Long::valueOf).collect(Collectors.toSet()));
        }
        if (ruleMap.containsKey(DataPermRuleType.TYPE_DEPT_ONLY)) {
            deptIdSet.add(deptId);
            ruleMap.remove(DataPermRuleType.TYPE_DEPT_ONLY);
        }
        return StringUtils.join(deptIdSet, ',');
    }

    /**
     * 添加用户和数据权限之间的多对多关联关系。
     *
     * @param dataPermId 数据权限Id。
     * @param userIdSet  关联的用户Id列表。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addDataPermUserList(Long dataPermId, Set<Long> userIdSet) {
        for (Long userId : userIdSet) {
            SysDataPermUser dataPermUser = new SysDataPermUser();
            dataPermUser.setDataPermId(dataPermId);
            dataPermUser.setUserId(userId);
            sysDataPermUserMapper.insert(dataPermUser);
        }
    }

    /**
     * 移除用户和数据权限之间的多对多关联关系。
     *
     * @param dataPermId 数据权限主键Id。
     * @param userId     用户主键Id。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeDataPermUser(Long dataPermId, Long userId) {
        SysDataPermUser dataPermUser = new SysDataPermUser();
        dataPermUser.setDataPermId(dataPermId);
        dataPermUser.setUserId(userId);
        return sysDataPermUserMapper.delete(new QueryWrapper<>(dataPermUser)) == 1;
    }

    /**
     * 验证数据权限对象关联菜单数据是否都合法。
     *
     * @param dataPerm         数据权限关对象。
     * @param deptIdListString 与数据权限关联的部门Id列表。
     * @return 验证结果。
     */
    @Override
    public CallResult verifyRelatedData(SysDataPerm dataPerm, String deptIdListString) {
        JSONObject jsonObject = new JSONObject();
        if (dataPerm.getRuleType() == DataPermRuleType.TYPE_MULTI_DEPT_AND_CHILD_DEPT
                || dataPerm.getRuleType() == DataPermRuleType.TYPE_CUSTOM_DEPT_LIST) {
            if (StringUtils.isBlank(deptIdListString)) {
                return CallResult.error("数据验证失败，部门列表不能为空！");
            }
            Set<Long> deptIdSet = Arrays.stream(StringUtils.split(
                    deptIdListString, ",")).map(Long::valueOf).collect(Collectors.toSet());
            if (!sysDeptService.existAllPrimaryKeys(deptIdSet)) {
                return CallResult.error("数据验证失败，存在不合法的部门数据，请刷新后重试！");
            }
            jsonObject.put("deptIdSet", deptIdSet);
        }
        return CallResult.ok(jsonObject);
    }

    private void insertRelationData(SysDataPerm dataPerm, Set<Long> deptIdSet) {
        if (CollectionUtils.isNotEmpty(deptIdSet)) {
            for (Long deptId : deptIdSet) {
                SysDataPermDept dataPermDept = new SysDataPermDept();
                dataPermDept.setDataPermId(dataPerm.getDataPermId());
                dataPermDept.setDeptId(deptId);
                sysDataPermDeptMapper.insert(dataPermDept);
            }
        }
    }
}
