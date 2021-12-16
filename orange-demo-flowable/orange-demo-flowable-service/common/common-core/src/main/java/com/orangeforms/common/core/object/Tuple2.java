package com.orangeforms.common.core.object;

/**
 * 二元组对象。主要用于可以一次返回多个结果的场景，同时还能避免强制转换。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class Tuple2<T1, T2> {

    /**
     * 第一个变量。
     */
    private final T1 first;
    /**
     * 第二个变量。
     */
    private final T2 second;

    /**
     * 构造函数。
     *
     * @param first 第一个变量。
     * @param second 第二个变量。
     */
    public Tuple2(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * 获取第一个变量。
     *
     * @return 返回第一个变量。
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * 获取第二个变量。
     *
     * @return 返回第二个变量。
     */
    public T2 getSecond() {
        return second;
    }

}

