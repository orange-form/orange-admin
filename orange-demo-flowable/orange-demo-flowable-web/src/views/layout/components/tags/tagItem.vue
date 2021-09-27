<template>
  <div class="tags-item">
    <span class="title">{{title}}</span>
    <i :class="{'el-icon-close close': !enterClose, 'el-icon-error close hover-close': enterClose}"
      v-if="supportClose" @click.stop="onClose"
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
      this.$emit('close');
    }
  }
}
</script>

<style lang="scss" scoped>
  @import '@/assets/style/element-variables.scss';
  .tags-item {
    height: 30px;
    line-height: 30px;
    border: 1px solid rgba(0, 0, 0, 0.15);
    border-radius: 3px;
    box-sizing: border-box;
    display: inline-block;
    cursor: pointer;
    background: white;
    padding: 0px 20px;
    color: $--color-text-primary;
  }

  .tags-item .title {
    font-size: 13px;
  }

  .close {
    height: 28px;
    line-height: 28px;
    display: none;
  }

  .tags-item.active .close {
    margin-left: 10px;
    display: inline-block;
  }

  .close.hover-close {
    color: $--color-text-secondary;
  }

  .tags-item:hover {
    color: $--color-primary;
  }
  .tags-item.active {
    color: $--color-primary;
    border-color: $--color-primary-light-5;
    background-color: $--color-primary-light-9;
  }

  .tags-item + .tags-item {
    margin-left: 5px;
  }

</style>
