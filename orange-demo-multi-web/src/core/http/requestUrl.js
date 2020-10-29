import projectConfig from '@/core/config';
import { objectToQueryString } from '@/utils';
console.log(process.env.NODE_ENV, projectConfig);

/**
 * 请求地址统一处理／组装
 * @param actionName action方法名称
 */
export default function (actionName) {
  if (actionName != null && actionName !== '') {
    if (actionName.substr(0, 1) === '/') actionName = actionName.substr(1);
  }
  return projectConfig.baseUrl + actionName;
}

export function buildGetUrl (actionName, params) {
  let queryString = objectToQueryString(params);
  if (actionName != null && actionName !== '') {
    if (actionName.substr(0, 1) === '/') actionName = actionName.substr(1);
  }

  return projectConfig.baseUrl + actionName + (queryString == null ? '' : ('?' + queryString));
}

export {
  projectConfig
}
