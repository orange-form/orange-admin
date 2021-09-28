import sortable from 'sortablejs'
/**
 * 拖拽排序对象
 * expample
 * <ul v-sortable="new SortableData(data)">
 *   <li>A</li>
 *   <li>B</li>
 *   <li>C</li>
 *   <li>D</li>
 * </ul>
 */
export class SortableData {
  constructor (data, group) {
    this.list = data;
    this.group = group;
    this.ghostClass = 'sortable-ghost';
    this.sortable = null;
    this.disabled = false;
  };

  setData (list) {
    this.list = list;
  }

  getElement (el) {
    return el;
  };

  onEnd (oldIndex, newIndex) {
    if (oldIndex === newIndex || this.list == null) return;
    let targetRow = this.list.splice(oldIndex, 1)[0]
    this.list.splice(newIndex, 0, targetRow);
  };

  init (el) {
    var _this = this;

    var _option = {};
    if (this.ghostClass != null) _option.ghostClass = this.ghostClass;
    if (this.group != null) _option.group = this.group;
    if (this.disabled != null) _option.disabled = this.disabled;
    // 列表中能拖动的dom的选择器（例如：.drag-item）
    if (this.draggable != null) _option.draggable = this.draggable;
    // 列表中拖动项，拖动把柄的选择器，只有点击这个选择出来的dom才可以开始拖动（例如：.drag-handle）
    if (this.handle != null) _option.handle = this.handle;
    _option.setData = function (dataTransfer) {
      dataTransfer.setData('Text', '');
    };
    _option.onEnd = function (evt) {
      _this.onEnd(evt.oldIndex, evt.newIndex);
    };

    this.sortable = sortable.create(_this.getElement(el), _option);
  };

  release () {
    if (this.sortable != null) this.sortable.destroy();
    this.sortable = null;
  }
};
