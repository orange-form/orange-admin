package com.orange.admin.common.biz.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.orange.admin.common.biz.config.CommonBizConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 全局共享的snowflake计算工具类。
 * 基于Hutool中的对应工具类实现，同时读取系统的配置参数并初始化该对象。如果今后
 * 升级为基于分布式Id生成服务的实现时，仅需修改内部实现，外部业务方法不会受到任何影响。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Component
public class BasicIdGenerator {

    @Autowired
    private CommonBizConfig config;

    private Snowflake snowflake;

    @PostConstruct
    private void init() {
        snowflake = IdUtil.createSnowflake(config.getSnowflakeWorkNode(), 0);
    }

    /**
     * 获取基于Snowflake算法的数值型Id。
     * 由于底层实现为synchronized方法，因此计算过程串行化，且线程安全。
     *
     * @return 计算后的全局唯一Id。
     */
    public long nextLongId() {
        return this.snowflake.nextId();
    }

    /**
     * 获取基于Snowflake算法的字符串Id。
     * 由于底层实现为synchronized方法，因此计算过程串行化，且线程安全。
     *
     * @return 计算后的全局唯一Id。
     */
    public String nextStringId() {
        return this.snowflake.nextIdStr();
    }
}
