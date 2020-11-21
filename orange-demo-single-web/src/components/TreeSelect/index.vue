<template>
  <el-popover ref="popover" placement="bottom-start" trigger="click" popper-class="tree-select-popover"
    :width="width" @show="onShowPopover">
    <el-scrollbar :style="{'height': this.height, 'min-width': this.width}" ref="scrollbar">
      <el-tree ref="dropdownTree" :props="getTreeProps" :highlightCurrent="highlightCurrent" :nodeKey="getDataProps.value"
        :defaultExpandAll="defaultExpandAll" :expandOnClickNode="expandOnClickNode" :checkOnClickNode="checkOnClickNode"
        :autoExpandParent="autoExpandParent" :defaultExpandedKeys="defaultExpandedKeys" :showCheckbox="showCheckbox"
        :checkStrictly="checkStrictly" :defaultCheckedKeys="defaultCheckedKeys" :currentNodeKey="getCurrentNodeKey"
        :accordion="accordion" :indent="indent" :iconClass="iconClass" :load="loadChildrenNodes" lazy :show-checkbox="multiple"
        @node-click="onTreeNodeClick" @check="onTreeNodeCheck">
        <span :style="getNodeStyle(data)" slot-scope="{ node, data }">{{data[getDataProps.label]}}</span>
      </el-tree>
    </el-scrollbar>
    <el-select slot="reference" v-model="selectKeys" :multiple="multiple" :disabled="false" :size="size"
      :clearable="clearable" :collapseTags="collapseTags" :placeholder="placeholder" popper-class="select-tree-popper"
      @clear="onClear" @remove-tag="onClear">
      <el-option v-for="item in selectNodes" :key="item[getDataProps.value]"
        :value="item[getDataProps.value]" :label="item[getDataProps.label]" />
    </el-select>
  </el-popover>
</template>

<script>
import { findTreeNode } from '@/utils';

export default {
  name: 'TreeSelect',
  props: {
    value: {
      type: [String, Number]
    },
    multiple: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    size: {
      type: String
    },
    height: {
      type: String,
      default: '200px'
    },
    width: {
      type: String,
      default: '300px'
    },
    activeColor: {
      type: String,
      default: '#EF5E1C'
    },
    clearable: {
      type: Boolean,
      default: false
    },
    collapseTags: {
      type: Boolean,
      default: true
    },
    placeholder: {
      type: String
    },
    loading: {
      type: Boolean,
      default: false
    },
    loadingText: {
      type: String,
      default: '加载中'
    },
    // 树属性
    data: {
      type: Array
    },
    props: {
      type: Object,
      default: () => {
        return {
          label: 'label',
          value: 'value',
          parentKey: 'parentId',
          children: 'children',
          disabled: 'disabled'
        }
      }
    },
    defaultExpandAll: {
      type: Boolean,
      default: false
    },
    expandOnClickNode: {
      type: Boolean,
      default: true
    },
    checkOnClickNode: {
      type: Boolean,
      default: false
    },
    autoExpandParent: {
      type: Boolean,
      default: true
    },
    defaultExpandedKeys: {
      type: Array
    },
    checkStrictly: {
      type: Boolean,
      default: true
    },
    currentNodeKey: {
      type: [String, Number]
    },
    accordion: {
      type: Boolean,
      default: false
    },
    indent: {
      type: Number,
      default: 16
    },
    iconClass: {
      type: String
    }
  },
  data () {
    return {
      rootNode: undefined,
      rootResolve: undefined,
      allTreeNode: [],
      selectNodes: [],
      scrollTop: 0,
      selectKeys: undefined
    }
  },
  methods: {
    onShowPopover () {
      setTimeout(() => {
        this.$refs.scrollbar.wrap.scrollTop = this.scrollTop;
        // this.$refs.scrollbar.update();
      }, 20);
      if (!this.multiple) {
        this.$refs.dropdownTree.setCurrentKey(this.value);
      }
    },
    onClear () {
      this.$nextTick(() => {
        this.$emit('input', this.selectKeys);
      });
    },
    onTreeNodeClick (data, node) {
      this.$refs.popover.showPopper = false;
      if (!this.multiple) {
        this.scrollTop = this.$refs.scrollbar.wrap.scrollTop;
        this.$emit('input', data[this.getDataProps.value]);
        this.$emit('change', data[this.getDataProps.value]);
      }
    },
    onTreeNodeCheck (data, {checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys}) {
      this.scrollTop = this.$refs.scrollbar.wrap.scrollTop
      this.$emit('input', checkedKeys);
      this.$emit('change', checkedKeys);
    },
    parseNode (node) {
      if (Array.isArray(node) && node.length > 0) {
        node.forEach((item) => {
          item['__node_is_leaf__'] = !(Array.isArray(item[this.getDataProps.children]) && item[this.getDataProps.children].length > 0);
        });
        return node;
      } else {
        return [];
      }
    },
    loadChildrenNodes (node, resolve) {
      if (node.level === 0) {
        this.rootNode = node;
        this.rootResolve = resolve;
        return resolve(this.parseNode(this.allTreeNode));
      } else {
        return resolve(this.parseNode(node.data[this.getDataProps.children]));
      }
    },
    getNodeStyle (data) {
      if (!this.multiple && (this.selectNodes[0] || {})[this.getDataProps.value] === data[this.getDataProps.value]) {
        return {
          color: this.activeColor,
          'font-weight': 700
        }
      }
    }
  },
  computed: {
    showCheckbox () {
      return this.multiple;
    },
    getCurrentNodeKey () {
      if (!this.multiple && Array.isArray(this.selectNodes) && this.selectNodes.length > 0) {
        return this.selectNodes[0][this.getDataProps.value];
      } else {
        return null;
      }
    },
    highlightCurrent () {
      return this.multiple;
    },
    defaultCheckedKeys () {
      return this.multiple ? this.selectNodes : undefined
    },
    getDataProps () {
      return {
        label: this.props.label || 'label',
        value: this.props.value || 'value',
        parentKey: this.props.parentKey || 'parentId',
        children: this.props.children || 'children',
        disabled: this.props.disabled || 'disabled'
      }
    },
    getTreeProps () {
      return {
        label: this.getDataProps.label,
        children: '__children_list__',
        disabled: this.getDataProps.disabled,
        isLeaf: '__node_is_leaf__'
      };
    }
  },
  watch: {
    data: {
      handler (newValue, oldValue) {
        this.allTreeNode = newValue;
        if (this.rootNode != null && this.rootResolve != null) {
          this.rootNode.childNodes = [];
          this.loadChildrenNodes(this.rootNode, this.rootResolve);
        }
        this.selectNodes = [];
        if (this.multiple) {
          if (Array.isArray(this.value)) {
            this.value.forEach((item) => {
              let data = findTreeNode(this.allTreeNode, item, this.getDataProps.value, this.getDataProps.children);
              if (data) this.selectNodes.push(data);
            });
          }
        } else {
          let data = findTreeNode(this.allTreeNode, this.value, this.getDataProps.value, this.getDataProps.children);
          if (data) this.selectNodes.push(data);
        }
      },
      immediate: true
    },
    value: {
      handler (newValue) {
        this.selectNodes = [];
        if (Array.isArray(newValue)) {
          newValue.forEach((item) => {
            let data = findTreeNode(this.allTreeNode, item, this.getDataProps.value, this.getDataProps.children);
            if (data) this.selectNodes.push(data);
          });
          this.selectKeys = newValue;
        } else {
          let data = findTreeNode(this.allTreeNode, newValue, this.getDataProps.value, this.getDataProps.children);
          if (data) this.selectNodes.push(data);
          this.selectKeys = newValue;
        }
        if (this.$refs.dropdownTree) {
          this.multiple ? this.$refs.dropdownTree.setCheckedKeys(newValue) : this.$refs.dropdownTree.setCurrentKey(newValue);
        }
      },
      immediate: true
    }
  }
}
</script>

<style>
  .select-tree-popper {
    display: none;
  }
  .tree-select-popover {
    padding: 6px 0px;
  }
  .tree-select-popover .popper__arrow {
    left: 35px!important;
  }
  .tree-select-popover .el-tree .el-tree-node__content {
    height: 34px;
    line-height: 34px;
  }
</style>
