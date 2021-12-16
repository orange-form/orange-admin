package com.orangeforms.webadmin.upms.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.*;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.webadmin.upms.dto.SysPostDto;
import com.orangeforms.webadmin.upms.model.SysPost;
import com.orangeforms.webadmin.upms.service.SysPostService;
import com.orangeforms.webadmin.upms.vo.SysPostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;

/**
 * 岗位管理操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysPost")
public class SysPostController {

    @Autowired
    private SysPostService sysPostService;

    /**
     * 新增岗位管理数据。
     *
     * @param sysPostDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysPostDto sysPostDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPostDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysPost sysPost = MyModelUtil.copyTo(sysPostDto, SysPost.class);
        sysPost = sysPostService.saveNew(sysPost);
        return ResponseResult.success(sysPost.getPostId());
    }

    /**
     * 更新岗位管理数据。
     *
     * @param sysPostDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SysPostDto sysPostDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPostDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysPost sysPost = MyModelUtil.copyTo(sysPostDto, SysPost.class);
        SysPost originalSysPost = sysPostService.getById(sysPost.getPostId());
        if (originalSysPost == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!sysPostService.update(sysPost, originalSysPost)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除岗位管理数据。
     *
     * @param postId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long postId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(postId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        SysPost originalSysPost = sysPostService.getById(postId);
        if (originalSysPost == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!sysPostService.remove(postId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的岗位管理列表。
     *
     * @param sysPostDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysPostVo>> list(
            @MyRequestBody SysPostDto sysPostDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysPost sysPostFilter = MyModelUtil.copyTo(sysPostDtoFilter, SysPost.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysPost.class);
        List<SysPost> sysPostList = sysPostService.getSysPostListWithRelation(sysPostFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(sysPostList, SysPost.INSTANCE));
    }

    /**
     * 查看指定岗位管理对象详情。
     *
     * @param postId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysPostVo> view(@RequestParam Long postId) {
        if (MyCommonUtil.existBlankArgument(postId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysPost sysPost = sysPostService.getByIdWithRelation(postId, MyRelationParam.full());
        if (sysPost == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysPostVo sysPostVo = SysPost.INSTANCE.fromModel(sysPost);
        return ResponseResult.success(sysPostVo);
    }

    /**
     * 以字典形式返回全部岗位管理数据集合。字典的键值为[postId, postName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(SysPost filter) {
        List<SysPost> resultList = sysPostService.getListByFilter(filter);
        return ResponseResult.success(BeanQuery.select("postId as id", "postName as name").executeFrom(resultList));
    }

    /**
     * 根据字典Id集合，获取查询后的字典数据。
     *
     * @param postIds 字典Id集合。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @PostMapping("/listDictByIds")
    public ResponseResult<List<Map<String, Object>>> listDictByIds(
            @MyRequestBody(elementType = Long.class) List<Long> postIds) {
        List<SysPost> resultList = sysPostService.getInList(new HashSet<>(postIds));
        return ResponseResult.success(BeanQuery.select("postId as id", "postName as name").executeFrom(resultList));
    }
}
