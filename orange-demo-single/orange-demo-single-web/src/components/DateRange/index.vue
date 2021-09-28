<template>
  <div class="date-range">
    <!--<year-range-panel />-->
    <el-select v-model="dateType" :size="size"
      style="max-width: 100px; min-width: 100px; margin-right: 10px;"
      v-if="!hideTypeOnlyOne || validTypeList.length > 1">
      <el-option v-for="type in validTypeList" :key="type.value" :value="type.value" :label="type.label" />
    </el-select>
    <el-date-picker style="flex-grow: 1;" v-model="currentDates"
      :size="size" :placeholder="placeholder" :type="innerDateType"
      :disabled="disabled" :format="innerDateFormat" :readonly="readonly" :editable="editable"
      :clearable="clearable" :start-placeholder="startPlaceholder" :end-placeholder="endPlaceholder"
      :align="align" :range-separator="rangeSeparator" :value-format="valueFormat"
      :prefix-icon="prefixIcon" :clear-icon="clearIcon" @change="onValueChange" />
  </div>
</template>

<script>
import { formatDate, parseDate } from 'element-ui/src/utils/date-util';

const allTypeList = [{
  value: 'day',
  label: '自然日'
}, {
  value: 'month',
  label: '自然月'
}, {
  value: 'year',
  label: '自然年'
}];

export default {
  name: 'DateRange',
  props: {
    /**
     * 绑定的数据
     */
    value: {
      type: [Array],
      default: () => {
        return []
      }
    },
    /**
     * 默认显示的数据选择方式，如果不存在与allowTypes中则显示allowTypes中的第一个
     */
    defaultDateType: {
      type: String,
      default: 'day'
    },
    /**
     * 组件大小（medium / small / mini）
     */
    size: {
      type: String
    },
    /**
     * 数据选择方式只有一个的时候是否隐藏数据选择方式下拉
     */
    hideTypeOnlyOne: {
      type: Boolean,
      default: true
    },
    /**
     * 允许的数据选择方式（day, month, year）
     * 默认值['day', 'month', 'year']
     */
    allowTypes: {
      type: Array,
      default: () => {
        return allTypeList.map((item) => {
          return item.value;
        });
      }
    },
    /**
     * 是否范围选择
     */
    isRange: {
      type: Boolean,
      default: true
    },
    /**
     * 输出字符串的format
     */
    outputFormat: {
      type: String,
      default: 'yyyy-MM-dd HH:mm:ss'
    },
    /**
     * 非范围选择时的占位内容
     */
    placeholder: String,
    /**
     * 范围选择时开始日期的占位内容
     */
    startPlaceholder: String,
    /**
     * 范围选择时结束日期的占位内容
     */
    endPlaceholder: String,
    /**
     * 完全只读
     */
    readonly: Boolean,
    /**
     * 禁用
     */
    disabled: Boolean,
    /**
     * 文本框可输入
     */
    editable: {
      type: Boolean,
      default: true
    },
    /**
     * 是否显示清除按钮
     */
    clearable: {
      type: Boolean,
      default: true
    },
    /**
     * 对齐方式（left, center, right）
     */
    align: {
      type: String,
      default: 'left'
    },
    /**
     * 选择范围时的分隔符
     */
    rangeSeparator: {
      type: String,
      default: '-'
    },
    /**
     * 可选，绑定值的格式。不指定则绑定值为 Date 对象
     */
    valueFormat: {
      type: String,
      default: 'yyyy-MM-dd'
    },
    /**
     * 自定义头部图标的类名
     */
    prefixIcon: {
      type: String,
      default: 'el-icon-date'
    },
    /**
     * 自定义清空图标的类名
     */
    clearIcon: {
      type: String,
      default: 'el-icon-circle-close'
    }
  },
  data () {
    return {
      dateType: this.defaultDateType,
      currentDates: undefined
    }
  },
  methods: {
    onValueChange (values) {
      this.$nextTick(() => {
        this.emitChange();
      });
    },
    emitChange () {
      let outputDate = [];
      if (this.currentDates != null) {
        if (!this.isRange) {
          outputDate[0] = new Date(this.currentDates);
          outputDate[1] = new Date(this.currentDates);
        } else {
          if (Array.isArray(this.currentDates) && this.currentDates.length === 2) {
            outputDate[0] = new Date(this.currentDates[0]);
            outputDate[1] = new Date(this.currentDates[1]);
          }
        }

        if (outputDate[0] != null && outputDate[1] != null) {
          outputDate[0].setHours(0, 0, 0, 0);
          outputDate[1].setHours(0, 0, 0, 0);
          switch (this.dateType) {
            case 'day':
              outputDate[1].setDate(outputDate[1].getDate() + 1);
              break;
            case 'month':
              outputDate[1].setDate(1);
              outputDate[0].setDate(1);
              outputDate[1].setMonth(outputDate[1].getMonth() + 1);
              break;
            case 'year':
              outputDate[1].setMonth(0);
              outputDate[1].setDate(1);
              outputDate[0].setMonth(0);
              outputDate[0].setDate(1);
              outputDate[1].setFullYear(outputDate[1].getFullYear() + 1);
              break;
          }
          outputDate[1] = new Date(outputDate[1].getTime() - 1);

          outputDate[0] = formatDate(outputDate[0], this.outputFormat);
          outputDate[1] = formatDate(outputDate[1], this.outputFormat);
        }
      }
      this.$emit('input', outputDate);
      this.$emit('change', outputDate);
    },
    getCurrentStatsDateType () {
      return this.dateType;
    }
  },
  computed: {
    validTypeList () {
      return allTypeList.filter((item) => {
        return this.allowTypes.indexOf(item.value) !== -1;
      });
    },
    /**
     * el-date-picker使用的type
     */
    innerDateType () {
      switch (this.dateType) {
        case 'day': return this.isRange ? 'daterange' : 'date';
        case 'month': return this.isRange ? 'monthrange' : 'month';
        case 'year': return this.isRange ? 'monthrange' : 'year';
        default: return this.isRange ? 'daterange' : 'date';
      }
    },
    /**
     * el-date-picker使用的format
     */
    innerDateFormat () {
      switch (this.dateType) {
        case 'day': return 'yyyy-MM-dd';
        case 'month': return 'yyyy-MM';
        case 'year': return 'yyyy';
        default: return 'yyyy-MM-dd';
      }
    }
  },
  watch: {
    value: {
      handler: function (newValue, oldValue) {
        if (newValue == null || newValue.length < 2) {
          this.currentDates = this.isRange ? [] : undefined;
        } else {
          if (this.currentDates == null) this.currentDates = [];
          if (this.isRange) {
            this.currentDates = [
              parseDate(newValue[0], this.valueFormat),
              parseDate(newValue[1], this.valueFormat)
            ];
          } else {
            this.currentDates = parseDate(newValue[0], this.valueFormat);
          }
        }
      },
      immediate: true,
      deep: true
    },
    dateType: {
      handler: function (newValue, oldValue) {
        if (this.allowTypes.indexOf(this.dateType) === -1) {
          this.dateType = this.allowTypes[0] || 'day';
        }
        this.emitChange();
      },
      immediate: true
    },
    defaultDateType: {
      handler: function (newValue, oldValue) {
        if (this.allowTypes.indexOf(newValue) !== -1) {
          this.dateType = newValue;
        } else {
          this.dateType = this.allowTypes[0];
        }
      }
    },
    isRange: {
      handler: function (newValue, oldValue) {
        let temp;
        if (newValue) {
          temp = [this.currentDates, this.currentDates];
        } else {
          temp = this.currentDates[0];
        }

        this.currentDates = temp;
      }
    }
  }
}
</script>

<style scoped>
  .date-range {
    display: flex;
  }
</style>
