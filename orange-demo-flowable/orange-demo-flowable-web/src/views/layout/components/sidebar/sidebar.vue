<template>
  <div style="height: 100%; position: relative;" class="sidebar-bg">
    <div class="sidebar-title">
      <img :src="logoImage" style="width: 32px; height: 32px; border-radius: 50%;" />
      <p class="sidebar-title-text">{{getProjectName()}}</p>
    </div>
    <div class="left-menu" style="height: 100%; padding-bottom: 60px;">
      <el-scrollbar wrap-class="scrollbar_dropdown__wrap" style="height: 100%;">
        <el-menu ref="menu" mode="vertical" :default-active="getCurrentMenuId" :unique-opened="true" @select="selectMenu"
          :active-text-color="activeTextColor" :collapse="getCollapse" >
          <template v-for="menu in getMenuList">
            <menu-item :menu="menu" :key="menu.menuId" />
          </template>
        </el-menu>
      </el-scrollbar>
    </div>
  </div>
</template>

<script>
import menuItem from './menu-item.vue';
import { mapGetters, mapMutations } from 'vuex';
import projectConfig from '@/core/config';

export default {
  data () {
    return {
      isCollapse: false,
      collapseLeft: '200px',
      showCollapseBtn: true,
      logoImage: require('../../../../assets/img/logo.png')
    }
  },
  components: {
    'menu-item': menuItem
  },
  computed: {
    activeTextColor () {
      return undefined;
    },
    getCollapseStyle () {
      return [{
        left: this.collapseLeft
      }]
    },
    ...mapGetters(['getMultiTags', 'getMenuList', 'getCollapse', 'getCurrentMenuPath', 'getCurrentMenuId', 'getMultiColumn'])
  },
  methods: {
    onCollapseChange () {
      this.showCollapseBtn = false;
      setTimeout(() => {
        this.setCollapse(!this.getCollapse);
        this.collapseLeft = this.getCollapse ? '65px' : '200px';
        this.showCollapseBtn = true;
      }, 100);
    },
    getProjectName () {
      if (this.getCollapse) {
        return projectConfig.projectName.substr(0, 1);
      } else {
        return projectConfig.projectName;
      }
    },
    selectMenu (index, path) {
      if (this.getCurrentMenuId === index) return;
      // 单页面清空所有tags和cachePage
      if (!this.getMultiTags) {
        this.clearAllTags();
      }
      this.setCurrentMenuId(index);
    },
    ...mapMutations(['setCollapse', 'clearAllTags', 'setCurrentMenuId'])
  }
};
</script>

<style scoped>
  .sidebar-title-text {
    font-size: 18px;
    font-weight: bold;
  }
</style>
