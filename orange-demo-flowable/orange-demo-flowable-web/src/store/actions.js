import { FlowOperationController } from '@/api/flowController.js';

// 催办消息下拉个数
const MESSAGE_SHOW_COUNT = 10;
// 催办消息轮询间隔
const MESSAGE_TIMER_INTERVAL = 10000;

// 获得消息列表数据
function loadMessage (context, owner) {
  let params = {
    pageParam: {
      pageSize: MESSAGE_SHOW_COUNT,
      pageNum: 1
    }
  }
  FlowOperationController.listRemindingTask(owner, params, null, {
    showMask: false,
    showError: false
  }).then(res => {
    context.commit('setMessageList', res.data);
  }).catch(e => {
    console.error(e);
  });
}

export default {
  startMessage: (context, owner) => {
    if (context.state.messageTimer != null) {
      clearInterval(context.state.messageTimer);
    }

    let timer = setInterval(() => {
      loadMessage(context, owner);
    }, MESSAGE_TIMER_INTERVAL);
    context.commit('setMessageTimer', timer);
    loadMessage(context, owner);
  },
  stopMessage: (context) => {
    if (context.state.messageTimer != null) {
      clearInterval(context.state.messageTimer);
    }
    context.commit('setMessageTimer', null);
  },
  reloadMessage: (context, owner) => {
    loadMessage(context, owner);
  }
}
