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
        document.title = '橙单工程';
        if (newValue.meta && newValue.meta.title) document.title += ' - ' + newValue.meta.title;
      },
      immediate: true
    }
  }
}
</script>
