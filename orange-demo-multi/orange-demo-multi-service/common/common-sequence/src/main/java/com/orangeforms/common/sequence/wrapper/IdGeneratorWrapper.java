package com.orangeforms.common.sequence.wrapper;

import com.orangeforms.common.sequence.config.IdGeneratorProperties;
import com.orangeforms.common.sequence.generator.BasicIdGenerator;
import com.orangeforms.common.sequence.generator.MyIdGenerator;
import com.orangeforms.common.sequence.generator.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 分布式Id生成器的封装类。该对象可根据配置选择不同的生成器实现类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Component
public class IdGeneratorWrapper {

    @Autowired
    private IdGeneratorProperties properties;
    /**
     * Id生成器接口对象。
     */
    private MyIdGenerator idGenerator;

    @PostConstruct
    public void init() {
        if (properties.getAdvanceIdGenerator()) {
            idGenerator = new SnowflakeIdGenerator(
                    properties.getZkAddress(), properties.getIdPort(), properties.getZkPath());
        } else {
            idGenerator = new BasicIdGenerator(properties.getSnowflakeWorkNode());
        }
    }

    /**
     * 获取基于Snowflake算法的数值型Id。
     * 由于底层实现为synchronized方法，因此计算过程串行化，且线程安全。
     *
     * @return 计算后的全局唯一Id。
     */
    public long nextLongId() {
        return idGenerator.nextLongId();
    }

    /**
     * 获取基于Snowflake算法的字符串Id。
     * 由于底层实现为synchronized方法，因此计算过程串行化，且线程安全。
     *
     * @return 计算后的全局唯一Id。
     */
    public String nextStringId() {
        return idGenerator.nextStringId();
    }
}
