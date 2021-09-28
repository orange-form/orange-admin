<template>
  <div class="el-input el-date-editor el-range-editor el-input__inner el-input-number-range"
    :class="
    [
      inputSize ? 'el-range-editor--' + inputSize : '',
      focused ? 'is-active' : '',
      {
        'is-disabled': inputDisabled,
        'el-input--prefix': prefixIcon
      }
    ]"
    @mouseenter="showClose = true"
    @mouseleave="showClose = false">
    <div class="el-input__icon el-range__icon" :class="prefixIcon">
      <slot name="prepend"></slot>
    </div>
    <input
      autocomplete="off"
      :placeholder="startPlaceholder"
      :value="userInput && userInput[0]"
      :disabled="inputDisabled"
      :readonly="readonly"
      :name="name && name[0]"
      @input="handleStartInput"
      @change="handleStartChange"
      @focus="focused = true"
      @blur="focused = false"
      class="el-range-input">
    <slot name="range-separator">
      <span class="el-range-separator">{{ rangeSeparator }}</span>
    </slot>
    <input
      autocomplete="off"
      :placeholder="endPlaceholder"
      :value="userInput && userInput[1]"
      :disabled="inputDisabled"
      :readonly="readonly"
      :name="name && name[1]"
      @input="handleEndInput"
      @change="handleEndChange"
      @focus="focused = true"
      @blur="focused = false"
      class="el-range-input">
    <i class="el-input__icon el-range__close-icon"
      :class="[showClear ? 'el-icon-circle-close' : '']"
      @click="handleClickClear">
    </i>
  </div>
</template>

<script>
import emitter from 'element-ui/src/mixins/emitter';

function isNumber (val) {
  var regPos = /^\d+(\.\d+)?$/; // 非负浮点数
  var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; // 负浮点数
  if (regPos.test(val) || regNeg.test(val)) {
    return true;
  } else {
    return false;
  }
}
export default {
  name: 'InputNumberRange',
  componentName: 'InputNumberRange',
  mixins: [emitter],
  props: {
    /**
     * 绑定字段
     */
    value: {
      type: Array,
      default: function () {
        return [];
      }
    },
    /**
     * 组件大小（medium / small / mini）
     */
    size: String,
    /**
     * 禁用
     */
    disabled: Boolean,
    /**
     * 完全只读
     */
    readonly: Boolean,
    /**
     * 是否显示清除按钮
     */
    clearable: {
      type: Boolean,
      default: false
    },
    /**
     * 自定义头部图标的类名
     */
    prefixIcon: String,
    /**
     * 范围选择时最小值的占位内容
     */
    startPlaceholder: String,
    /**
     * 范围选择时最大值的占位内容
     */
    endPlaceholder: String,
    /**
     * 原生属性
     */
    name: {
      default: ''
    },
    /**
     * 选择范围时的分隔符
     */
    rangeSeparator: {
      type: String,
      default: '-'
    },
    validateEvent: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      hovering: false,
      focused: false,
      userInput: this.value,
      showClose: false
    };
  },
  inject: {
    elForm: {
      default: ''
    },
    elFormItem: {
      default: ''
    }
  },
  computed: {
    _elFormItemSize () {
      return (this.elFormItem || {}).elFormItemSize;
    },
    inputSize () {
      let temp = this.size || this._elFormItemSize || (this.$ELEMENT || {}).size;
      return temp;
    },
    inputDisabled () {
      return this.disabled || (this.elForm || {}).disabled;
    },
    showClear () {
      let temp = this.clearable && !this.inputDisabled && !this.readonly && this.showClose &&
        this.userInput != null && Array.isArray(this.userInput) && this.userInput.length > 0 &&
        (this.userInput[0] != null || this.userInput[1] != null);
      return temp;
    }
  },
  methods: {
    handleStartInput (event) {
      if (this.userInput) {
        this.userInput = [event.target.value, this.userInput[1]];
      } else {
        this.userInput = [event.target.value, null];
      }
    },

    handleEndInput (event) {
      if (this.userInput) {
        this.userInput = [this.userInput[0], event.target.value];
      } else {
        this.userInput = [null, event.target.value];
      }
    },
    handleStartChange (event) {
      let value = this.userInput && this.userInput[0];
      value = isNumber(value) ? value : null;
      value = value ? Number.parseFloat(value) : null;
      if (this.userInput) {
        this.userInput[0] = value;
      } else {
        this.userInput = [value, null];
      }
      event.srcElement.value = value;
      this.emitInput(this.userInput);
    },
    handleEndChange (event) {
      let value = this.userInput && this.userInput[1];
      value = isNumber(value) ? value : null;
      value = value ? Number.parseFloat(value) : null;
      if (this.userInput) {
        this.userInput[1] = value;
      } else {
        this.userInput = [null, value];
      }
      event.srcElement.value = value;
      this.emitInput(this.userInput);
    },
    handleClickClear () {
      this.userInput = undefined;
      this.emitInput(this.userInput);
    },
    valueEquals (val, oldVal) {
      return JSON.stringify(val) === JSON.stringify(oldVal);
    },
    emitInput (values) {
      this.$emit('input', values);
      this.$emit('change', values);
    }
  },
  watch: {
    value: {
      handler: function (val, oldVal) {
        if (!this.valueEquals(val, oldVal) && this.validateEvent) {
          this.dispatch('ElFormItem', 'el.form.change', val);
        }
      },
      deep: true
    }
  }
}
</script>

<style>
</style>
