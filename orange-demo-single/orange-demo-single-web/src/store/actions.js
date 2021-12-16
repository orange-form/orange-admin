// 获得消息列表数据
function loadMessage (context, owner) {
  // TODO: 获取消息列表
}

export default {
  startMessage: (context, owner) => {
    // TODO: 开始消息获取轮询
  },
  stopMessage: (context) => {
    // TODO: 结束消息获取轮询
  },
  reloadMessage: (context, owner) => {
    loadMessage(context, owner);
  }
}
