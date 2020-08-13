const projectConfig = require('../config/' + process.env.NODE_ENV);

export const globalConfig = {
  httpOption: {
    showMask: true,
    showError: true
  },
  axiosOption: {
  }
};

export default projectConfig;
