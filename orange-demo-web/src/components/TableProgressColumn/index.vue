<template>
  <el-table-column v-bind="$attrs">
    <template slot-scope="scope">
      <orange-progress :stroke-width="strokeWidth" :type="type" :text-inside="textInside" :status="status" :color="color"
        :width="width" :show-text="showText" :min="getMinValue(scope.row)" :max="getMaxValue(scope.row)"
        :value="getValue(scope.row)" />
    </template>
  </el-table-column>
</template>

<script>
import Progress from '@/components/Progress/index.vue';

export default {
  name: 'TableProgressColumn',
  components: {
    'orange-progress': Progress
  },
  props: {
    /**
     * 固定值最小值
     */
    min: {
      type: Number,
      default: 0
    },
    /**
     * 固定值最大值
     */
    max: {
      type: Number,
      default: 100
    },
    /**
     * 固定值当前值
     */
    value: {
      type: Number,
      default: 0
    },
    /**
     * 表格最小值字段名
     */
    minColumn: {
      type: String
    },
    /**
     * 表格最大值字段名
     */
    maxColumn: {
      type: String
    },
    /**
     * 表格当前值字段名
     */
    valueColumn: {
      type: String
    },
    /**
     * 进度条的宽度，单位 px
     */
    strokeWidth: {
      type: Number,
      default: 16
    },
    /**
     * 进度条类型（line/circle/dashboard）
     */
    type: {
      type: String,
      default: 'line'
    },
    /**
     * 进度条显示文字内置在进度条内（只在 type=line 时可用）
     */
    textInside: {
      type: Boolean,
      default: true
    },
    /**
     * 进度条当前状态(success/exception/warning)
     */
    status: {
      type: String
    },
    /**
     * 进度条背景色（会覆盖 status 状态颜色）
     */
    color: {
      type: [String, Function, Array]
    },
    /**
     * 环形进度条画布宽度（只在 type 为 circle 或 dashboard 时可用）
     */
    width: {
      type: Number,
      default: 126
    },
    /**
     * 是否显示进度条文字内容
     */
    showText: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    getValue (row) {
      return this.valueColumn ? row[this.valueColumn] : this.value;
    },
    getMinValue (row) {
      return this.minColumn ? row[this.minColumn] : this.min;
    },
    getMaxValue (row) {
      return this.maxColumn ? row[this.maxColumn] : this.max;
    }
  }
}
</script>
