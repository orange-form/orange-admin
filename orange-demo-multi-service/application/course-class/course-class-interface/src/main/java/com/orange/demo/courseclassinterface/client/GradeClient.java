package com.orange.demo.courseclassinterface.client;

import com.orange.demo.common.core.base.client.BaseFallbackFactory;
import com.orange.demo.common.core.config.FeignConfig;
import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.object.*;
import com.orange.demo.courseclassinterface.dto.GradeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 服务远程数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@FeignClient(
        name = "course-class",
        configuration = FeignConfig.class,
        fallbackFactory = GradeClient.GradeClientFallbackFactory.class)
public interface GradeClient extends BaseClient<GradeDto, Integer> {

    /**
     * 基于主键的(in list)条件获取远程数据接口。
     *
     * @param gradeIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象的数据集合。
     */
    @Override
    @PostMapping("/grade/listByIds")
    ResponseResult<List<GradeDto>> listByIds(
            @RequestParam("gradeIds") Set<Integer> gradeIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param gradeId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @Override
    @PostMapping("/grade/getById")
    ResponseResult<GradeDto> getById(
            @RequestParam("gradeId") Integer gradeId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否全部存在。
     *
     * @param gradeIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @PostMapping("/grade/existIds")
    ResponseResult<Boolean> existIds(Set<Integer> gradeIds);

    /**
     * 给定主键Id是否存在。
     *
     * @param gradeId 参数主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @Override
    @PostMapping("/grade/existId")
    ResponseResult<Boolean> existId(@RequestParam("gradeId") Integer gradeId);

    /**
     * 删除主键Id关联的对象。
     *
     * @param gradeId 主键Id。
     * @return 应答结果对象。
     */
    @Override
    @PostMapping("/grade/delete")
    ResponseResult<Void> delete(@RequestParam("gradeId") Integer gradeId);

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含删除数量。
     */
    @Override
    @PostMapping("/grade/deleteBy")
    ResponseResult<Integer> deleteBy(@RequestBody GradeDto filter);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/grade/listBy")
    ResponseResult<List<GradeDto>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/grade/getBy")
    ResponseResult<GradeDto> getBy(@RequestBody MyQueryParam queryParam);

    @Component("CourseClassGradeClientFallbackFactory")
    @Slf4j
    class GradeClientFallbackFactory
            extends BaseFallbackFactory<GradeDto, Integer, GradeClient> implements GradeClient {

        @Override
        public GradeClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new GradeClientFallbackFactory();
        }
    }
}
