<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <el-breadcrumb-item :to="{name: 'welcome'}" :replace="true">
      <i class="el-icon-s-home" style="margin-right: 5px;" />主页
    </el-breadcrumb-item>
    <el-breadcrumb-item v-for="item  in menuPathList" :key="item.menuId">
      {{item.menuName}}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex';

export default {
  created () {
    this.getBreadcrumb();
  },
  data () {
    return {
      menuPathList: null
    };
  },
  watch: {
    $route (newValue) {
      if (newValue.name === 'welcome') this.setCurrentMenuId(null);
      this.getBreadcrumb();
    }
  },
  methods: {
    getBreadcrumb () {
      this.menuPathList = this.getMultiColumn ? null : this.getCurrentMenuPath;
    },
    ...mapMutations(['setCurrentMenuId'])
  },
  computed: {
    ...mapGetters(['getCurrentMenuPath', 'getMultiColumn'])
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .app-breadcrumb.el-breadcrumb {
    display: inline-block;
    font-size: 14px;
    line-height: 60px;
    margin-left: 10px;
    .no-redirect {
      color: #97a8be;
      cursor: text;
    }
  }
</style>
