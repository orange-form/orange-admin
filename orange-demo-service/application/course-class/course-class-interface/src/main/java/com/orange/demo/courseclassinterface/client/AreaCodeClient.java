package com.orange.demo.courseclassinterface.client;

import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.MyQueryParam;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.courseclassinterface.dto.AreaCodeDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 行政区划远程访问接口类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@FeignClient(name = "system-service", fallbackFactory = AreaCodeClient.AreaCodeClientFallbackFactory.class)
public interface AreaCodeClient extends BaseClient<AreaCodeDto, Long> {

    /**
     * 根据主键Id集合，返回给定的数据集合。
     *
     * @param areaCodeIds 待获取的主键Id列表。
     * @param withDict    是否包含字典关联。
     * @return 获取的对象列表。
     */
    @Override
    @PostMapping("/areaCode/listByIds")
    ResponseResult<List<AreaCodeDto>> listByIds(
            @RequestParam("areaCodeIds") Set<Long> areaCodeIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 根据主键Id返回给定的数据。
     *
     * @param areaId         待获取的主键Id。
     * @param withDict       是否获取表实体关联的字典数据。
     * @return 获取的对象。
     */
    @Override
    @GetMapping("/areaCode/getById")
    ResponseResult<AreaCodeDto> getById(
            @RequestParam("areaId") Long areaId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否全部存在。
     *
     * @param areaIds 主键Id。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @GetMapping("/areaCode/existIds")
    ResponseResult<Boolean> existIds(@RequestParam("areaIds") Set<Long> areaIds);

    /**
     * 给定主键Id是否存在。
     *
     * @param areaId 参数主键Id。
     * @return 应答结果对象，包含true，否则false。
     */
    @Override
    @GetMapping("/areaCode/existId")
    ResponseResult<Boolean> existId(@RequestParam("areaId") Long areaId);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/areaCode/listBy")
    ResponseResult<List<AreaCodeDto>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/areaCode/getBy")
    ResponseResult<AreaCodeDto> getBy(@RequestBody MyQueryParam queryParam);

    /**
     * 以id、name的形式返回所有字典数据的列表。
     *
     * @return 字典数据列表，返回形式为 List<Map<String, Object>>，map中包含两条记录，分别是id和name。
     */
    @PostMapping("/areaCode/listDictAreaCode")
    ResponseResult<List<Map<String, Object>>> listDictAreaCode();

    /**
     * 以id、name的形式返回所有字典数据的列表。
     *
     * @param parentId 主键的ParentId，用于对主键数据进行过滤。
     * @return 字典数据列表，返回形式为 List<Map<String, Object>>，map中包含两条记录，分别是id和name。
     */
    @PostMapping("/areaCode/listDictAreaCodeByParentId")
    ResponseResult<List<Map<String, Object>>> listDictAreaCodeByParentId(@RequestParam("parentId") Long parentId);

    @Component
    @Slf4j
    class AreaCodeClientFallbackFactory implements FallbackFactory<AreaCodeClient>, AreaCodeClient {

        @Override
        public ResponseResult<List<AreaCodeDto>> listByIds(Set<Long> areaCodeIds, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<AreaCodeDto> getById(Long areaId, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existIds(Set<Long> areaIds) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existId(Long areaId) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<List<AreaCodeDto>> listBy(MyQueryParam queryParam) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<AreaCodeDto> getBy(MyQueryParam queryParam) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<List<Map<String, Object>>> listDictAreaCode() {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<List<Map<String, Object>>> listDictAreaCodeByParentId(Long parentId) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public AreaCodeClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new AreaCodeClientFallbackFactory();
        }
    }
}
