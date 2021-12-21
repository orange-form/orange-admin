import Vue from 'vue';
import { Loading, Message } from 'element-ui';
import request from './request';
import requestUrl from './requestUrl';
import { globalConfig } from '@/core/config';

/**
 * 遮罩管理，多次调用支持引用计数
 */
class LoadingManager {
  constructor (options) {
    this.options = options;
    this.refCount = 0;
    this.loading = undefined;
  }

  showMask () {
    this.loading = Loading.service(this.options);
    this.refCount++;
  }

  hideMask () {
    if (this.refCount <= 1 && this.loading != null) {
      this.loading.close();
      this.loading = null;
    }
    this.refCount--;
    this.refCount = Math.max(0, this.refCount);
  }
}

const loadingManager = new LoadingManager({
  fullscreen: true,
  background: 'rgba(0, 0, 0, 0.1)'
});

/**
 * post请求
 * @param {String} url 请求的url
 * @param {Object} params 请求参数
 * @param {Object} options axios设置项
 * @returns {Promise}
 */
const fetchPost = function (url, params, options) {
  if (options == null) return {};
  let tempOptions = {
    ...options,
    method: 'post',
    url: requestUrl(url),
    data: params
  };

  return request(tempOptions);
};
/**
 * get请求
 * @param {String} url 请求的url
 * @param {Object} params 请求参数
 * @param {Object} options axios设置项
 * @returns {Promise}
 */
const fetchGet = function (url, params, options) {
  if (options == null) return {};
  let tempOptions = {
    ...options,
    method: 'get',
    url: requestUrl(url),
    params
  };
  return request(tempOptions);
};
/**
 * 下载请求
 * @param {String} url 请求的url
 * @param {Object} params 请求参数
 * @param {String} fileName 下载后保存的文件名
 * @returns {Promise}
 */
const fetchDownload = function (url, params, fileName) {
  return new Promise((resolve, reject) => {
    request({
      url: requestUrl(url),
      method: 'post',
      data: params,
      responseType: 'blob',
      transformResponse: function (data) {
        return (data instanceof Blob && data.size > 0) ? data : undefined;
      }
    }).then(res => {
      if (res.data == null) {
        reject(new Error('下载文件失败'));
      } else {
        let blobData = new Blob([res.data], { type: 'application/octet-stream' });
        let blobUrl = window.URL.createObjectURL(blobData);
        let linkDom = document.createElement('a');
        linkDom.style.display = 'none';
        linkDom.href = blobUrl;
        linkDom.setAttribute('download', fileName);
        if (typeof linkDom.download === 'undefined') {
          linkDom.setAttribute('target', '_blank');
        }
        document.body.appendChild(linkDom);
        linkDom.click();
        document.body.removeChild(linkDom);
        window.URL.revokeObjectURL(blobData);
        resolve();
      }
    }).catch(e => {
      if (e instanceof Blob) {
        let reader = new FileReader();
        reader.onload = function () {
          let jsonObj = JSON.parse(reader.result);
          reject((jsonObj || {}).errorMessage || '下载文件失败');
        }
        reader.readAsText(e);
      } else {
        reject(e);
      }
    });
  });
}

// url调用节流Set
const ajaxThrottleSet = new Set();
/**
 * 数据请求
 * @param {String} url 请求的url
 * @param {String} type 请求类型 (get，post)
 * @param {Object} params 请求参数
 * @param {Object} axiosOption axios设置
 * @param {Object} options 显示设置
 */
const doUrl = function (url, type, params, axiosOption, options) {
  let finalOption = {
    ...globalConfig.httpOption,
    ...options
  };
  let { showMask, showError, throttleFlag, throttleTimeout } = finalOption;
  let finalAxiosOption = {
    ...globalConfig.axiosOption,
    ...axiosOption
  }
  if (type == null || type === '') type = 'post';
  if (ajaxThrottleSet.has(url) && throttleFlag) {
    return Promise.resolve();
  } else {
    if (throttleFlag) {
      ajaxThrottleSet.add(url);
      setTimeout(() => {
        ajaxThrottleSet.delete(url);
      }, throttleTimeout || 50);
    }
    return new Promise((resolve, reject) => {
      if (showMask) loadingManager.showMask();
      let ajaxCall = null;
      if (type.toLowerCase() === 'get') {
        ajaxCall = fetchGet(url, params, finalAxiosOption);
      } else if (type.toLowerCase() === 'post') {
        ajaxCall = fetchPost(url, params, finalAxiosOption);
      }
  
      if (ajaxCall != null) {
        ajaxCall.then(res => {
          if (showMask) loadingManager.hideMask();
          if (res.data && res.data.success) {
            resolve(res.data);
          } else {
            if (showError) {
              Message.error({
                showClose: true,
                message: res.data.errorMessage ? res.data.errorMessage : '数据请求失败'
              });
            }
            reject(res.data);
          }
        }).catch(e => {
          if (showMask) loadingManager.hideMask();
          if (showError) {
            Message.error({
              showClose: true,
              message: e.errorMessage ? e.errorMessage : '网络请求错误'
            });
          }
          reject(e);
        });
      } else {
        if (showMask) loadingManager.hideMask();
        reject(new Error('错误的请求类型 - ' + type));
      }
    });
  }
};

Vue.prototype.download = fetchDownload;
Vue.prototype.doUrl = doUrl;
Vue.prototype.loadingManager = loadingManager;

export default {
  doUrl,
  fetchPost,
  fetchGet,
  fetchDownload
}
