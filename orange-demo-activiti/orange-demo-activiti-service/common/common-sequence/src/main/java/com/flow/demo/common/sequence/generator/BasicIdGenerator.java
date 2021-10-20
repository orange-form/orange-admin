package com.flow.demo.common.sequence.generator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 基础版snowflake计算工具类。
 * 和SnowflakeIdGenerator相比，相同点是均为基于Snowflake算法的生成器。不同点在于当前类的
 * WorkNodeId是通过配置文件静态指定的。而SnowflakeIdGenerator的WorkNodeId是由zk生成的。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class BasicIdGenerator implements MyIdGenerator {

    private final Snowflake snowflake;

    /**
     * 构造函数。
     *
     * @param workNode 工作节点。
     */
    public BasicIdGenerator(Integer workNode) {
        snowflake = IdUtil.createSnowflake(workNode, 0);
    }

    /**
     * 获取基于Snowflake算法的数值型Id。
     * 由于底层实现为synchronized方法，因此计算过程串行化，且线程安全。
     *
     * @return 计算后的全局唯一Id。
     */
    @Override
    public long nextLongId() {
        return this.snowflake.nextId();
    }

    /**
     * 获取基于Snowflake算法的字符串Id。
     * 由于底层实现为synchronized方法，因此计算过程串行化，且线程安全。
     *
     * @return 计算后的全局唯一Id。
     */
    @Override
    public String nextStringId() {
        return this.snowflake.nextIdStr();
    }
}
