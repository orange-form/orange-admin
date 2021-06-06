package com.orange.demo.courseclassservice.service.impl;

import com.orange.demo.common.redis.cache.RedisDictionaryCache;
import com.orange.demo.common.core.base.service.BaseDictService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.courseclassservice.service.GradeService;
import com.orange.demo.courseclassservice.dao.GradeMapper;
import com.orange.demo.courseclassservice.model.Grade;
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
