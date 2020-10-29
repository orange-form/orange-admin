<template>
  <div class="tags-item">
    <div style="height: 3px;" class="top" />
    <span class="title" :style="{'padding-right': supportClose ? '5px' : '10px'}">{{title}}</span>
    <i :class="{'el-icon-close close': !enterClose, 'el-icon-error': enterClose}" v-show="supportClose"
      :style="{color: enterClose ? '#F56C6C' : '#333'}" @click.stop="onClose"
      @mouseenter="() => enterClose = true" @mouseleave="() => enterClose = false" />
  </div>
</template>

<script>
export default {
  props: {
    title: String,
    supportClose: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      enterClose: false
    }
  },
  methods: {
    onClose () {
      console.log('close-tag-item');
      this.$emit('close');
    }
  }
}
</script>

<style lang="scss" scoped>
  @import '@/assets/style/element-color.scss';
  .tags-item {
    height: 40px;
    line-height: 37px;
    box-sizing: border-box;
    display: inline-block;
    border-right: 1px solid #DCDFE6;
    cursor: pointer;
  }

  .tags-item .title {
    font-size: 13px;
    color: #666
  }

  .close {
    height: 28px;
    line-height: 28px;
  }
  .tags-item:hover {
    color: $--color-primary;
  }
  .tags-item.active {
    color: $--color-primary;
  }
  .tags-item span {
    margin-left: 10px;
  }
  .tags-item i {
    margin-right: 10px;
  }
  .tags-item.active .top {
    background: $--color-primary;
  }

</style>
