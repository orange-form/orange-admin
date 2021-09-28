package com.orange.demo.common.core.constant;

/**
 * 在跨服务调用时，需要传递的聚合分类常量对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class AggregationKind {

    /**
     * 一对多关联中，聚合从表中指定字段，将多条记录汇总到主表的指定字段上。
     */
    public static final int ONE_TO_MANY = 1;
    /**
     * 多对多关联中，聚合从表中指定字段，将多条记录汇总到主表的指定字段上。
     */
    public static final int MANY_TO_MANY = 2;

    /**
     * 判断参数是否为当前常量字典的合法值。
     *
     * @param aggregationKind 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(Integer aggregationKind) {
        return aggregationKind != null &&
                (aggregationKind == ONE_TO_MANY || aggregationKind == MANY_TO_MANY);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private AggregationKind() {
    }
}
