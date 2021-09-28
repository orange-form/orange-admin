<template>
  <div>
    <router-view></router-view>
  </div>
</template>

<script>
import projectConfig from '@/core/config';

export default {
  watch: {
    $route: {
      handler (newValue) {
        document.title = projectConfig.projectName;
        if (newValue.meta && newValue.meta.title) document.title += ' - ' + newValue.meta.title;
      },
      immediate: true
    }
  }
}
</script>
