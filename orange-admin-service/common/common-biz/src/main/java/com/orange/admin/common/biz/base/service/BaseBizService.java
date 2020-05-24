package com.orange.admin.common.biz.base.service;

import com.orange.admin.common.core.base.service.BaseService;
import lombok.extern.slf4j.Slf4j;

/**
 * 所有业务Service的共同基类。由于BaseService位于common-core模块内，该模块很少涉及spring bean的集成，
 * 因此该类位于业务服务类和BaseService之间，主要提供一些通用方法，特别是与spring bean相关的业务代码。
 * NOTE: 目前该类实现为空，主要是为了便于用户自行扩展，同时也能方便今后向微服务的升级，
 *
 * @param <M> Model对象的类型。
 * @param <K> Model对象主键的类型。
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Slf4j
public abstract class BaseBizService<M, K> extends BaseService<M, K> {

}
