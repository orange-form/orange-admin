<template>
  <div>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  watch: {
    $route: {
      handler (newValue) {
        document.title = 'UAA应用管理项目';
        if (newValue.meta && newValue.meta.title) document.title += ' - ' + newValue.meta.title;
      },
      immediate: true
    }
  }
}
</script>
