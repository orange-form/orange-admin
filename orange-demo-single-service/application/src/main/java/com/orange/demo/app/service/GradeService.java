package com.orange.demo.app.service;

import com.orange.demo.common.core.base.service.BaseDictService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.cache.MapDictionaryCache;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.app.dao.GradeMapper;
import com.orange.demo.app.model.Grade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 年级数据操作服务类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Service
public class GradeService extends BaseDictService<Grade, Integer> {

    @Autowired
    private GradeMapper gradeMapper;

    public GradeService() {
        super();
        this.dictionaryCache = MapDictionaryCache.create(Grade::getGradeId);
    }

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<Grade> mapper() {
        return gradeMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param grade 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    public Grade saveNew(Grade grade) {
        grade.setStatus(GlobalDeletedFlag.NORMAL);
        gradeMapper.insert(grade);
        dictionaryCache.put(grade.getGradeId(), grade);
        return grade;
    }

    /**
     * 更新数据对象。
     *
     * @param grade         更新的对象。
     * @param originalGrade 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Grade grade, Grade originalGrade) {
        grade.setStatus(GlobalDeletedFlag.NORMAL);
        if (gradeMapper.updateByPrimaryKey(grade) != 1) {
            return false;
        }
        dictionaryCache.put(grade.getGradeId(), grade);
        return true;
    }

    /**
     * 删除指定数据。
     *
     * @param gradeId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Integer gradeId) {
        Grade deletedObject = new Grade();
        deletedObject.setGradeId(gradeId);
        deletedObject.setStatus(GlobalDeletedFlag.DELETED);
        if (gradeMapper.updateByPrimaryKeySelective(deletedObject) != 1) {
            return false;
        }
        dictionaryCache.invalidate(gradeId);
        return true;
    }
}
