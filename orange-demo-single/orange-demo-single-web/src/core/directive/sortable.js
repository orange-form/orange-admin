import Vue from 'vue'
import { SortableData } from './sortableData';

/**
 * 拖拽排序指令
 */
Vue.directive('sortable', {
  inserted: function (el, binding, vnode) {
    let sortableData = binding.value;
    if (sortableData == null || !(sortableData instanceof SortableData)) return;
    
    sortableData.init(vnode.elm);
  }
});
