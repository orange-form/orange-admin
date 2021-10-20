const projectConfig = require('../config/' + process.env.NODE_ENV);

export const globalConfig = {
  httpOption: {
    // 调用的时候是否显示蒙版
    showMask: true,
    // 是否显示公共的错误提示
    showError: true,
    // 是否开启节流功能，同一个url不能频繁重复调用
    throttleFlag: false,
    // 节流间隔
    throttleTimeout: 50
  },
  axiosOption: {
  }
};

export default projectConfig;
