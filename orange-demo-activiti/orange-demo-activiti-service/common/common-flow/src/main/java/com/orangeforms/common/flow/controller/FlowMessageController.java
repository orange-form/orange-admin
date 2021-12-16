package com.orangeforms.common.flow.controller;

import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.flow.model.FlowMessage;
import com.orangeforms.common.flow.service.FlowMessageService;
import com.orangeforms.common.flow.vo.FlowMessageVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流消息操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-flow.urlPrefix}/flowMessage")
public class FlowMessageController {

    @Autowired
    private FlowMessageService flowMessageService;

    /**
     * 获取当前用户的催办消息列表。
     * 不仅仅包含，其中包括当前用户所属角色、部门和岗位的候选组催办消息。
     *
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/listRemindingTask")
    public ResponseResult<MyPageData<FlowMessageVo>> listRemindingTask(@MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<FlowMessage> flowMessageList = flowMessageService.getRemindingMessageListByUser();
        return ResponseResult.success(MyPageUtil.makeResponseData(flowMessageList, FlowMessage.INSTANCE));
    }
}
