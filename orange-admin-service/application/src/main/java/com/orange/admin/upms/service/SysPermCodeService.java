package com.orange.admin.upms.service;

import com.alibaba.fastjson.JSONObject;
import com.orange.admin.common.biz.util.BasicIdGenerator;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.base.service.BaseService;
import com.orange.admin.common.core.constant.GlobalDeletedFlag;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.upms.dao.SysMenuPermCodeMapper;
import com.orange.admin.upms.dao.SysPermCodeMapper;
import com.orange.admin.upms.dao.SysPermCodePermMapper;
import com.orange.admin.upms.model.SysMenuPermCode;
import com.orange.admin.upms.model.SysPermCode;
import com.orange.admin.upms.model.SysPermCodePerm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限字数据服务类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Service
public class SysPermCodeService extends BaseService<SysPermCode, Long> {

    @Autowired
    private SysPermCodeMapper sysPermCodeMapper;
    @Autowired
    private SysPermCodePermMapper sysPermCodePermMapper;
    @Autowired
    private SysMenuPermCodeMapper sysMenuPermCodeMapper;
    @Autowired
    private SysPermService sysPermService;
    @Autowired
    private BasicIdGenerator idGenerator;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysPermCode> mapper() {
        return sysPermCodeMapper;
    }

    /**
     * 获取指定用户的权限字列表。
     *
     * @param userId 用户主键Id。
     * @return 用户关联的权限字列表。
     */
    public List<String> getPermCodeListByUserId(Long userId) {
        return sysPermCodeMapper.getPermCodeListByUserId(userId);
    }

    /**
     * 保存新增的权限字对象。
     *
     * @param sysPermCode 新增的权限字对象。
     * @param permIdSet 权限资源Id列表。
     * @return 新增后的权限字对象。
     */
    @Transactional
    public SysPermCode saveNew(SysPermCode sysPermCode, Set<Long> permIdSet) {
        sysPermCode.setPermCodeId(idGenerator.nextLongId());
        sysPermCode.setCreateTime(new Date());
        sysPermCode.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        sysPermCodeMapper.insert(sysPermCode);
        if (permIdSet != null) {
            List<SysPermCodePerm> sysPermCodePermList = new LinkedList<>();
            for (Long permId : permIdSet) {
                SysPermCodePerm permCodePerm = new SysPermCodePerm();
                permCodePerm.setPermCodeId(sysPermCode.getPermCodeId());
                permCodePerm.setPermId(permId);
                sysPermCodePermList.add(permCodePerm);
            }
            sysPermCodePermMapper.insertList(sysPermCodePermList);
        }
        return sysPermCode;
    }

    /**
     * 更新权限字对象。
     *
     * @param sysPermCode 更新的权限字对象。
     * @param originalSysPermCode 原有的权限字对象。
     * @param permIdSet 权限资源Id列表。
     * @return 更新成功返回true，否则false。
     */
    @Transactional
    public boolean update(SysPermCode sysPermCode, SysPermCode originalSysPermCode, Set<Long> permIdSet) {
        sysPermCode.setCreateTime(originalSysPermCode.getCreateTime());
        sysPermCode.setParentId(originalSysPermCode.getParentId());
        sysPermCode.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        if (sysPermCodeMapper.updateByPrimaryKey(sysPermCode) != 1) {
            return false;
        }
        Example e = new Example(SysPermCodePerm.class);
        e.createCriteria().andEqualTo("permCodeId", sysPermCode.getPermCodeId());
        sysPermCodePermMapper.deleteByExample(e);
        if (permIdSet != null) {
            List<SysPermCodePerm> sysPermCodePermList = new LinkedList<>();
            for (Long permId : permIdSet) {
                SysPermCodePerm permCodePerm = new SysPermCodePerm();
                permCodePerm.setPermCodeId(sysPermCode.getPermCodeId());
                permCodePerm.setPermId(permId);
                sysPermCodePermList.add(permCodePerm);
            }
            sysPermCodePermMapper.insertList(sysPermCodePermList);
        }
        return true;
    }

    /**
     * 删除指定的权限字。
     *
     * @param permCodeId 权限字主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional
    public boolean remove(Long permCodeId) {
        SysPermCode permCode = new SysPermCode();
        permCode.setPermCodeId(permCodeId);
        permCode.setDeletedFlag(GlobalDeletedFlag.DELETED);
        if (sysPermCodeMapper.updateByPrimaryKeySelective(permCode) != 1) {
            return false;
        }
        Example menuPermCodeExample = new Example(SysMenuPermCode.class);
        menuPermCodeExample.createCriteria().andEqualTo("permCodeId", permCodeId);
        sysMenuPermCodeMapper.deleteByExample(menuPermCodeExample);
        Example permCodePermExample = new Example(SysPermCodePerm.class);
        permCodePermExample.createCriteria().andEqualTo("permCodeId", permCodeId);
        sysPermCodePermMapper.deleteByExample(permCodePermExample);
        return true;
    }

    /**
     * 获取指定权限字对象数据，该对象将包含其关联数据。
     *
     * @param permCodeId 权限字主键Id。
     * @return 权限字对象。
     */
    public SysPermCode getSysPermCodeWithRelation(Long permCodeId) {
        SysPermCode sysPermCode = this.getById(permCodeId);
        if (sysPermCode != null) {
            Example e = new Example(SysPermCodePerm.class);
            e.createCriteria().andEqualTo("permCodeId", permCodeId);
            List<SysPermCodePerm> sysPermCodePermList = sysPermCodePermMapper.selectByExample(e);
            if (sysPermCodePermList.size() > 0) {
                List<Long> permIdList =
                        sysPermCodePermList.stream().map(SysPermCodePerm::getPermId).collect(Collectors.toList());
                sysPermCode.setPermIdList(permIdList);
            }
        }
        return sysPermCode;
    }

    /**
     * 获取指定用户的权限字列表。
     *
     * @param loginName 精确匹配用户登录名。
     * @param permCode 模糊匹配的权限字名，LIKE %permCode%。
     * @return 权限字列表。
     */
    public List<SysPermCode> getUserPermCodeListByFilter(String loginName, String permCode) {
        return sysPermCodeMapper.getUserPermCodeListByFilter(loginName, permCode);
    }

    /**
     * 获取该菜单的权限字，及其权限字关联的权限资源列表。
     *
     * @param menuId 菜单Id。
     * @return 关联了权限资源的权限字列表。
     */
    public List<Map<String, Object>> getPermCodeListByMenuId(Long menuId) {
        return sysPermCodeMapper.getPermCodeListByMenuId(menuId);
    }

    /**
     * 判断当前权限字是否存在下级权限字对象。
     *
     * @param permCodeId 权限字主键Id。
     * @return 存在返回true，否则false。
     */
    public boolean hasChildren(Long permCodeId) {
        SysPermCode permCode = new SysPermCode();
        permCode.setParentId(permCodeId);
        return this.getCountByFilter(permCode) > 0;
    }

    /**
     * 验证权限字对象关联的数据是否都合法。
     *
     * @param sysPermCode 当前操作的对象。
     * @param originalSysPermCode 原有对象。
     * @param permIdListString 逗号分隔的权限资源Id列表。
     * @return 验证结果。
     */
    public VerifyResult verifyRelatedData(
            SysPermCode sysPermCode, SysPermCode originalSysPermCode, String permIdListString) {
        String errorMessage = null;
        JSONObject jsonObject = null;
        Map<String, Object> resultMap = new HashMap<>(2);
        do {
            if (this.needToVerify(sysPermCode, originalSysPermCode, SysPermCode::getParentId)) {
                if (getById(sysPermCode.getParentId()) == null) {
                    errorMessage = "数据验证失败，关联的上级权限字并不存在，请刷新后重试！";
                    break;
                }
            }
            if (StringUtils.isNotBlank(permIdListString)) {
                Set<Long> permIdSet = Arrays.stream(
                        permIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
                if (!sysPermService.existUniqueKeyList("permId", permIdSet)) {
                    errorMessage = "数据验证失败，存在不合法的权限资源，请刷新后重试！";
                    break;
                }
                jsonObject = new JSONObject();
                jsonObject.put("permIdSet", permIdSet);
            }
        } while (false);
        return VerifyResult.create(errorMessage, jsonObject);
    }
}
