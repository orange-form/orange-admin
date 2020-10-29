package com.orange.demo.common.sequence.generator;

/**
 * 分布式Id生成器的统一接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface MyIdGenerator {

    /**
     * 获取数值型分布式Id。
     *
     * @return 生成后的Id。
     */
    long nextLongId();

    /**
     * 获取字符型分布式Id。
     *
     * @return 生成后的Id。
     */
    String nextStringId();
}
