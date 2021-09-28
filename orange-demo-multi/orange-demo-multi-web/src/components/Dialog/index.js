import $ from 'jquery';
import Vue from 'vue';
import router from '@/router';
import store from '@/store';

window.jQuery = $;
const layer = require('layui-layer');

class Dialog {
  /**
   * 关闭弹窗
   * @param {*} index 要关闭的弹窗的index
   */
  static close (index) {
    layer.close(index);
  }
  /**
   * 关闭所有弹窗
   */
  static closeAll () {
    layer.closeAll();
  }
  /**
   * 打开弹窗
   * @param {*} title 弹窗标题
   * @param {*} component 弹窗内容的组件
   * @param {*} options 弹窗设置（详情请见layui官网）
   * @param {*} params 弹窗组件参数
   */
  static show (title, component, options, params) {
    return new Promise((resolve, reject) => {
      let layerOptions = {
        title: title,
        type: 1,
        skin: 'layer-dialog',
        resize: false,
        offset: 'auto',
        zIndex: 1000,
        index: 0,
        contentDom: null
      };

      layerOptions = {...layerOptions, ...options};
      layerOptions.end = () => {
        if (layerOptions.contentDom) document.body.removeChild(layerOptions.contentDom);
      }

      let observer = {
        cancel: function (isSuccess = false, data = undefined) {
          layer.close(this.index);
          if (isSuccess) {
            resolve(data);
          } else {
            reject();
          }
        },
        index: -1
      }
      layerOptions.cancel = () => {
        reject();
      }
      let dom = document.createElement('div');
      document.body.appendChild(dom);
      let Content = Vue.extend(component);
      let vueObj = new Content({router: router, store: store, propsData: params});
      vueObj.observer = observer;
      vueObj.$mount(dom);
      layerOptions.contentDom = vueObj.$el;
      layerOptions.content = $(layerOptions.contentDom);
      observer.index = layer.open(layerOptions);
    });
  }
}

Vue.prototype.$dialog = Dialog;

export default Dialog;
