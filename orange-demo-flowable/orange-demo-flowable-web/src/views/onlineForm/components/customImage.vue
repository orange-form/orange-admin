<template>
  <el-col :span="widgetConfig.span || 24">
    <img :src="widgetConfig.src" :height="widgetConfig.height" :width="widgetConfig.width"
      style="max-width: 100%;" :style="getImageStyle"
    />
  </el-col>
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
    getImageStyle () {
      return {
        'object-fit': this.widgetConfig.fit
      }
    }
  }
}
</script>

<style>
</style>
