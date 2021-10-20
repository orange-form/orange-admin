<template>
  <el-row>
    <el-col :span="widgetConfig.span || 24">
      <div :style="getTextStyle">{{widgetConfig.showName}}</div>
    </el-col>
  </el-row>
</template>

<script>
export default {
  props: {
    widgetConfig: {
      type: Object,
      required: true
    }
  },
  computed: {
    getTextStyle () {
      return {
        'color': this.widgetConfig.color,
        'background-color': this.widgetConfig.backgroundColor,
        'font-size': this.widgetConfig.fontSize != null ? this.widgetConfig.fontSize + 'px' : undefined,
        'line-height': this.widgetConfig.lineHeight != null ? this.widgetConfig.lineHeight + 'px' : undefined,
        'text-indent': this.widgetConfig.indent != null ? this.widgetConfig.indent + 'em' : undefined,
        'text-decoration': this.widgetConfig.decoration,
        'text-align': this.widgetConfig.align,
        'padding': this.widgetConfig.padding + 'px'
      }
    }
  }
}
</script>

<style>
</style>
