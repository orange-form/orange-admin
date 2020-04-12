<template>
  <div>
    <router-view></router-view>
  </div>
</template>

<script>
import { mapMutations } from 'vuex';

export default {
  methods: {
    ...mapMutations(['setUserRules'])
  },
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
