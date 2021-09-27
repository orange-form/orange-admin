<template>
  <Draggable class="flex-box" draggable=".draggable-item" :list="list" group="componentsGroup">
    <slot />
    <div v-for="item in tempDomCount" :key="item" :style="{width: tempDomWidth}" />
    <el-row type="flex" :justify="operatorPosition" :style="getMenuBoxStyle">
      <slot name="operator" />
    </el-row>
  </Draggable>
</template>

<script>
import $ from 'jquery';
import Draggable from 'vuedraggable';

export default {
  name: 'FilterBox',
  components: {
    Draggable
  },
  props: {
    list: {
      type: Array,
      default: () => []
    },
    /**
     * 每一个过滤项宽度（包含标题和输入框宽度总和）
     */
    itemWidth: {
      type: Number,
      required: true
    },
    /**
     * 每一项下间距
     */
    marginBottom: {
      type: String,
      default: '18px'
    },
    /**
     * 按钮块最小宽度默认350，当每一行剩余空间大于此值，按钮块将不会折行
     */
    minMenuWidth: {
      type: Number,
      default: 350
    },
    /**
     * 按钮位置，默认为end，可选值为start/end/center/space-around/space-between
     */
    operatorPosition: {
      type: String,
      default: 'end'
    }
  },
  data () {
    return {
      tempDomCount: 0,
      tempDomWidth: undefined,
      operatorWidth: undefined,
      oldFilterItemCount: 0,
      oldHasOperator: false,
      oldWidth: 0,
      rowJustify: 'space-between'
    }
  },
  computed: {
    getMenuBoxStyle () {
      return {
        'width': this.operatorWidth,
        'margin-bottom': this.marginBottom,
        'flex-grow': this.operatorWidth ? undefined : '1'
      }
    }
  },
  methods: {
    onUpdate () {
      setTimeout(() => {
        let filterItemCount = Array.isArray(this.$slots.default) ? this.$slots.default.filter(item => item.context).length : 0;
        let hasOperator = Array.isArray(this.$slots.operator) && this.$slots.operator.length > 0;
        let width = $(this.$el).width();
        if (filterItemCount === this.oldFilterItemCount && hasOperator === this.oldHasOperator && width === this.oldWidth) {
          return;
        }
        let lineCount = this.itemWidth > 0 ? parseInt(width / this.itemWidth) : 1;
        lineCount = Math.max(1, lineCount);
        let residueCount = filterItemCount % lineCount;

        this.tempDomCount = 0;
        this.tempDomWidth = undefined;
        this.rowJustify = 'space-between';
        let tempCount = residueCount === 0 ? 0 : (lineCount - residueCount);
        if (hasOperator) {
          let residueWidth = width - ((Math.min(lineCount, filterItemCount) - residueCount) * this.itemWidth) - ((tempCount >= 1) ? 20 : 0);
          // 判断剩余的空间是否够放下操作按钮
          if (residueWidth >= this.minMenuWidth && residueCount === 0) {
            this.rowJustify = 'start';
            this.operatorWidth = undefined;
          } else {
            // 剩余空位数大于1，需要占位dom
            if (tempCount >= 1) {
              if (residueWidth >= this.minMenuWidth) {
                this.tempDomCount = tempCount - 1;
                this.tempDomWidth = this.tempDomCount > 0 ? (20 / this.tempDomCount) + 'px' : undefined;
                this.operatorWidth = this.tempDomCount > 0 ? (((tempCount * this.itemWidth) - 20) + 'px') : (this.itemWidth + 'px');
              } else {
                this.tempDomCount = tempCount;
                this.tempDomWidth = (residueWidth / this.tempDomCount) + 'px';
                this.operatorWidth = '100%';
              }
            } else {
              this.operatorWidth = '100%';
            }
          }
        } else {
          this.tempDomCount = tempCount;
          this.tempDomWidth = this.itemWidth + 'px';
        }

        this.oldFilterItemCount = filterItemCount;
        this.oldHasOperator = hasOperator;
        this.oldWidth = width;
      });
    }
  },
  beforeUpdate () {
    this.onUpdate();
  },
  mounted () {
    setTimeout(() => {
      this.onUpdate();
    });
  },
  created () {
    window.addEventListener('resize', this.onUpdate);
  },
  beforeDestroy () {
    window.removeEventListener('resize', this.onUpdate);
  }
}
</script>

<style>
</style>
