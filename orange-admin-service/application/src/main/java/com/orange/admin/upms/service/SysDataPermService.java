package com.orange.admin.upms.service;

import com.alibaba.fastjson.JSONObject;
import com.orange.admin.common.biz.base.service.BaseBizService;
import com.orange.admin.common.biz.util.BasicIdGenerator;
import com.orange.admin.common.core.constant.DataPermRuleType;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.constant.GlobalDeletedFlag;
import com.orange.admin.common.core.object.TokenData;
import com.orange.admin.common.core.object.CallResult;
import com.orange.admin.upms.dao.SysDataPermDeptMapper;
import com.orange.admin.upms.dao.SysDataPermMapper;
import com.orange.admin.upms.dao.SysDataPermUserMapper;
import com.orange.admin.upms.dao.SysDataPermMenuMapper;
import com.orange.admin.upms.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限数据服务类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Service
public class SysDataPermService extends BaseBizService<SysDataPerm, Long> {

    @Autowired
    private SysDataPermMapper sysDataPermMapper;
    @Autowired
    private SysDataPermDeptMapper sysDataPermDeptMapper;
    @Autowired
    private SysDataPermMenuMapper sysDataPermMenuMapper;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysDataPermUserMapper sysDataPermUserMapper;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private BasicIdGenerator idGenerator;

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
     * @param dataPermMenuList 关联的菜单对象列表。
     * @return 新增后的数据权限对象。
     */
    @Transactional(rollbackFor = Exception.class)
    public SysDataPerm saveNew(SysDataPerm dataPerm, Set<Long> deptIdSet, List<SysDataPermMenu> dataPermMenuList) {
        dataPerm.setDataPermId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        dataPerm.setCreateUserId(tokenData.getUserId());
        dataPerm.setCreateUsername(tokenData.getShowName());
        dataPerm.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        Date now = new Date();
        dataPerm.setCreateTime(now);
        dataPerm.setUpdateTime(now);
        sysDataPermMapper.insert(dataPerm);
        this.insertRelationData(dataPerm, deptIdSet, dataPermMenuList);
        return dataPerm;
    }

    /**
     * 更新数据权限对象。
     *
     * @param dataPerm         更新的数据权限对象。
     * @param originalDataPerm 原有的数据权限对象。
     * @param deptIdSet        关联的部门Id列表。
     * @param dataPermMenuList 关联的菜单对象列表。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(
            SysDataPerm dataPerm,
            SysDataPerm originalDataPerm,
            Set<Long> deptIdSet,
            List<SysDataPermMenu> dataPermMenuList) {
        dataPerm.setUpdateTime(new Date());
        dataPerm.setCreateTime(originalDataPerm.getCreateTime());
        dataPerm.setCreateUserId(originalDataPerm.getCreateUserId());
        dataPerm.setCreateUsername(originalDataPerm.getCreateUsername());
        dataPerm.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        if (sysDataPermMapper.updateByPrimaryKey(dataPerm) != 1) {
            return false;
        }
        SysDataPermMenu dataPermMenu = new SysDataPermMenu();
        dataPermMenu.setDataPermId(dataPerm.getDataPermId());
        sysDataPermMenuMapper.delete(dataPermMenu);
        SysDataPermDept dataPermDept = new SysDataPermDept();
        dataPermDept.setDataPermId(dataPerm.getDataPermId());
        sysDataPermDeptMapper.delete(dataPermDept);
        this.insertRelationData(dataPerm, deptIdSet, dataPermMenuList);
        return true;
    }

    /**
     * 删除指定数据权限。
     *
     * @param dataPermId 数据权限主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long dataPermId) {
        SysDataPerm dataPerm = new SysDataPerm();
        dataPerm.setDataPermId(dataPermId);
        dataPerm.setDeletedFlag(GlobalDeletedFlag.DELETED);
        if (sysDataPermMapper.updateByPrimaryKeySelective(dataPerm) != 1) {
            return false;
        }
        SysDataPermMenu dataPermMenu = new SysDataPermMenu();
        dataPermMenu.setDataPermId(dataPermId);
        sysDataPermMenuMapper.delete(dataPermMenu);
        SysDataPermDept dataPermDept = new SysDataPermDept();
        dataPermDept.setDataPermId(dataPermId);
        sysDataPermDeptMapper.delete(dataPermDept);
        SysDataPermUser dataPermUser = new SysDataPermUser();
        dataPermUser.setDataPermId(dataPermId);
        sysDataPermUserMapper.delete(dataPermUser);
        return true;
    }

    /**
     * 获取数据权限列表。
     *
     * @param filter  数据权限过滤对象。
     * @param orderBy 排序参数。
     * @return 数据权限查询列表。
     */
    public List<SysDataPerm> getSysDataPermList(SysDataPerm filter, String orderBy) {
        return sysDataPermMapper.getSysDataPermList(filter, orderBy);
    }

    /**
     * 将指定用户的指定会话的数据权限集合存入缓存。
     *
     * @param sessionId 会话Id。
     * @param userId    用户主键Id。
     * @param deptId    用户所属部门主键Id。
     * @param isAdmin   是否是管理员。
     * @return 查询并缓存后的数据权限集合。返回格式为，Map<MenuId, Map<RuleType, DeptIdString>>。
     */
    @CachePut(value = "DATA_PERMISSION_CACHE", key = "#sessionId")
    public Map<Object, Map<Integer, String>> putDataPermCache(
            String sessionId, Long userId, Long deptId, boolean isAdmin) {
        // 管理员账户返回空对象，便于缓存的统一处理。
        return isAdmin ? new HashMap<>(1) : this.getSysDataPermMenuListByUserId(userId, deptId);
    }

    /**
     * 将指定会话的数据权限集合从缓存中移除。
     *
     * @param sessionId 会话Id。
     */
    @CacheEvict(value = "DATA_PERMISSION_CACHE", key = "#sessionId")
    public void removeDataPermCache(String sessionId) {
        // 空实现即可，只是通过注解将当前sessionId从cache中删除。
    }

    /**
     * 获取指定用户Id的数据权限列表。并基于菜单Id和权限规则类型进行了一级和二级的分组。
     *
     * @param userId 指定的用户Id。
     * @param deptId 用户所属部门主键Id。
     * @return 合并优化后的数据权限列表。返回格式为，Map<MenuId, Map<RuleType, DeptIdString>>。
     */
    public Map<Object, Map<Integer, String>> getSysDataPermMenuListByUserId(Long userId, Long deptId) {
        List<SysDataPermMenu> dataPermMenuList = sysDataPermMenuMapper.getSysDataPermMenuListByUserId(userId);
        if (dataPermMenuList.isEmpty()) {
            return new HashMap<>(1);
        }
        // 这里用代码的方式把deptId组装到SysDataPermMenu中。
        Set<Long> dataPermIdSet = dataPermMenuList.stream()
                .map(SysDataPermMenu::getDataPermId).collect(Collectors.toSet());
        Example e = new Example(SysDataPermDept.class);
        e.createCriteria().andIn("dataPermId", dataPermIdSet);
        List<SysDataPermDept> dataPermDeptList = sysDataPermDeptMapper.selectByExample(e);
        Map<Long, List<SysDataPermDept>> deptMap =
                dataPermDeptList.stream().collect(Collectors.groupingBy(SysDataPermDept::getDataPermId));
        for (SysDataPermMenu dataPermMenu : dataPermMenuList) {
            List<SysDataPermDept> deptList = deptMap.get(dataPermMenu.getDataPermId());
            if (CollectionUtils.isNotEmpty(deptList)) {
                Set<Long> deptIdSet = deptList.stream().map(SysDataPermDept::getDeptId).collect(Collectors.toSet());
                dataPermMenu.setDeptIdListString(StringUtils.join(deptIdSet, ","));
            }
        }
        // 由于同一用户可能属于多个数据权限，所以需要进行基于menuId的权限合并。
        return mergeDataPermMenuList(dataPermMenuList, deptId);
    }

    private Map<Object, Map<Integer, String>> mergeDataPermMenuList(
            List<SysDataPermMenu> dataPermMenuList, Long deptId) {
        Map<Object, List<SysDataPermMenu>> menuMap =
                dataPermMenuList.stream().collect(Collectors.groupingBy(SysDataPermMenu::getMenuId));
        Map<Object, Map<Integer, String>> resultMap = new HashMap<>(menuMap.size());
        // 这里menuMap的key是menuId
        for (Map.Entry<Object, List<SysDataPermMenu>> entry : menuMap.entrySet()) {
            Object menuId = entry.getKey();
            // 为了更方便进行后续的合并优化处理，这里再基于规则类型进行分组。ruleMap的key是规则类型。
            Map<Integer, List<SysDataPermMenu>> ruleMap =
                    entry.getValue().stream().collect(Collectors.groupingBy(SysDataPermMenu::getRuleType));
            Map<Integer, String> m = new HashMap<>(1);
            // 如有有ALL存在，就可以直接退出了，没有必要在处理后续的规则了。
            if (ruleMap.containsKey(DataPermRuleType.TYPE_ALL)) {
                m.put(DataPermRuleType.TYPE_ALL, "null");
                resultMap.put(menuId, m);
                continue;
            }
            // 合并自定义部门了。
            String deptIds = processMultiDept(ruleMap, deptId);
            if (deptIds != null) {
                m.put(DataPermRuleType.TYPE_CUSTOM_DETP_LIST, deptIds);
            }
            // 最后处理当前部门和当前用户。
            if (ruleMap.get(DataPermRuleType.TYPE_DEPT_ONLY) != null) {
                m.put(DataPermRuleType.TYPE_DEPT_ONLY, "null");
            }
            if (ruleMap.get(DataPermRuleType.TYPE_USER_ONLY) != null) {
                m.put(DataPermRuleType.TYPE_USER_ONLY, "null");
            }
            resultMap.put(menuId, m);
        }
        return resultMap;
    }

    private String processMultiDept(Map<Integer, List<SysDataPermMenu>> ruleMap, Long deptId) {
        List<SysDataPermMenu> customDeptList = ruleMap.get(DataPermRuleType.TYPE_CUSTOM_DETP_LIST);
        if (customDeptList == null) {
            return null;
        }
        Set<Long> deptIdSet = new HashSet<>();
        for (SysDataPermMenu customDept : customDeptList) {
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
    public void addDataPermUserList(Long dataPermId, Set<Long> userIdSet) {
        List<SysDataPermUser> dataPermUserList = new LinkedList<>();
        for (Long userId : userIdSet) {
            SysDataPermUser dataPermUser = new SysDataPermUser();
            dataPermUser.setDataPermId(dataPermId);
            dataPermUser.setUserId(userId);
            dataPermUserList.add(dataPermUser);
        }
        sysDataPermUserMapper.addDataPermUserList(dataPermUserList);
    }

    /**
     * 移除用户和数据权限之间的多对多关联关系。
     *
     * @param dataPermId 数据权限主键Id。
     * @param userId     用户主键Id。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeDataPermUser(Long dataPermId, Long userId) {
        SysDataPermUser dataPermUser = new SysDataPermUser();
        dataPermUser.setDataPermId(dataPermId);
        dataPermUser.setUserId(userId);
        return sysDataPermUserMapper.delete(dataPermUser) == 1;
    }

    /**
     * 验证数据权限对象关联菜单数据是否都合法。
     *
     * @param dataPerm         数据权限关对象。
     * @param deptIdListString 与数据权限关联的部门Id列表。
     * @param menuIdListString 与数据权限关联的菜单Id列表。
     * @return 验证结果。
     */
    public CallResult verifyRelatedData(SysDataPerm dataPerm, String deptIdListString, String menuIdListString) {
        JSONObject jsonObject = new JSONObject();
        if (dataPerm.getRuleType() == DataPermRuleType.TYPE_CUSTOM_DETP_LIST) {
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
        String[] menuIdArray = StringUtils.split(menuIdListString, ",");
        Set<Long> menuIdSet = Arrays.stream(menuIdArray).map(Long::valueOf).collect(Collectors.toSet());
        // 验证菜单Id的合法性
        if (!sysMenuService.existAllPrimaryKeys(menuIdSet)) {
            return CallResult.error("数据验证失败，存在不合法的菜单，请刷新后重试！");
        }
        List<SysDataPermMenu> dataPermMenuList = new LinkedList<>();
        for (Long menuId : menuIdSet) {
            SysDataPermMenu dataPermMenu = new SysDataPermMenu();
            dataPermMenu.setMenuId(menuId);
            dataPermMenuList.add(dataPermMenu);
        }
        jsonObject.put("dataPermMenuList", dataPermMenuList);
        return CallResult.ok(jsonObject);
    }

    private void insertRelationData(
            SysDataPerm dataPerm, Set<Long> deptIdSet, List<SysDataPermMenu> dataPermMenuList) {
        if (CollectionUtils.isNotEmpty(deptIdSet)) {
            List<SysDataPermDept> dataPermDeptList = new LinkedList<>();
            for (Long deptId : deptIdSet) {
                SysDataPermDept dataPermDept = new SysDataPermDept();
                dataPermDept.setDataPermId(dataPerm.getDataPermId());
                dataPermDept.setDeptId(deptId);
                dataPermDeptList.add(dataPermDept);
            }
            sysDataPermDeptMapper.insertList(dataPermDeptList);
        }
        if (CollectionUtils.isNotEmpty(dataPermMenuList)) {
            for (SysDataPermMenu dataPermMenu : dataPermMenuList) {
                dataPermMenu.setDataPermId(dataPerm.getDataPermId());
            }
            sysDataPermMenuMapper.insertList(dataPermMenuList);
        }
    }
}
