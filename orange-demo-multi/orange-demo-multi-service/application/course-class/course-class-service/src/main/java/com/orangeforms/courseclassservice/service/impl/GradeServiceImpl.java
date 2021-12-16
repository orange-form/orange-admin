package com.orangeforms.courseclassservice.service.impl;

import com.orangeforms.common.redis.cache.RedisDictionaryCache;
import com.orangeforms.common.core.base.service.BaseDictService;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.courseclassservice.service.GradeService;
import com.orangeforms.courseclassservice.dao.GradeMapper;
import com.orangeforms.courseclassservice.model.Grade;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 年级字典数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@Service("gradeService")
public class GradeServiceImpl extends BaseDictService<Grade, Integer> implements GradeService {

    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private RedissonClient redissonClient;

    public GradeServiceImpl() {
        super();
    }

    @PostConstruct
    public void init() {
        this.dictionaryCache = RedisDictionaryCache.create(
                redissonClient, "Grade", Grade.class, Grade::getGradeId);
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
}
