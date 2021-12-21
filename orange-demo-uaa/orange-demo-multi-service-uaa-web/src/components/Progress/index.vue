<template>
  <el-progress v-bind="$attrs" :percentage="getPercentage" />
</template>

<script>
export default {
  name: 'Progress',
  props: {
    /**
     * 组件最小值
     */
    min: {
      type: Number,
      default: 0
    },
    /**
     * 组件最大值
     */
    max: {
      type: Number,
      default: 100
    },
    /**
     * 组件当前值
     */
    value: {
      type: Number,
      default: 0
    }
  },
  computed: {
    getPercentage () {
      let value = Math.min(this.max, Math.max(this.min, this.value));
      value = value - this.min;
      if ((this.max - this.min) === 0) {
        value = 0;
      } else {
        value = ((value * 100) / (this.max - this.min));
      }
      return Number.isInteger(value) ? value : parseInt(value);
    }
  }
}
</script>
