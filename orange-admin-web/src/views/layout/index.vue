<template>
  <el-container :style="getMainStyle">
    <el-aside width='200px' class="sidebar">
      <side-bar style="overflow: hidden" :multi-tag="false"></side-bar>
    </el-aside>
    <el-container style="background-color: rgb(235,235,235)">
      <el-header class="header">
        <breadcrumb class="breadcrumb-container" style="float: left"/>
        <div class="header-menu">
          <el-dropdown class="user-dropdown" trigger="click" @command="handleCommand">
            <span class="el-dropdown-link">{{getUserInfo.showName}}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item class="user-dropdown-item" command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <img :src="header" class="header-img" />
        </div>
      </el-header>
      <el-main style="padding: 15px 0px;">
        <el-scrollbar :style="getContextStyle" wrap-class="scrollbar_dropdown__wrap">
          <transition name="fade" mode="out-in">
            <keep-alive :include="getCachePages">
              <router-view style="margin: 0px 15px; background-color: white; overflow: hidden; padding: 20px;" :style="getRouterViewStyle" />
            </keep-alive>
          </transition>
        </el-scrollbar>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import SideBar from './components/sidebar/sidebar.vue';
import { mapGetters, mapMutations } from 'vuex';
import Breadcrumb from './components/breadcrumb';
import { SystemController } from '@/api';

export default {
  data () {
    return {
      header: require('../../assets/img/default-header.jpg')
    };
  },
  components: {
    'side-bar': SideBar,
    'breadcrumb': Breadcrumb
  },
  methods: {
    toggleSideBar () {
      this.setCollapse(!this.getCollapse);
    },
    resetDocumentClientHeight () {
      let timerID;
      let _this = this;
      return function () {
        clearTimeout(timerID);
        timerID = setTimeout(() => {
          var h = document.documentElement['clientHeight'];
          _this.setClientHeight(h);
        }, 50);
      }
    },
    handleCommand (command) {
      if (command === 'logout') {
        this.$confirm('是否退出登录？', '', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let options = {
            headers: {
              Authorization: window.sessionStorage.getItem('token')
            },
            showMask: false
          }

          SystemController.logout(this, {}, options).catch(e => {});
          this.clearAllTags();
          this.$router.replace({name: 'login'});
        }).catch(e => {});
      }
    },
    ...mapMutations(['setClientHeight', 'clearAllTags'])
  },
  computed: {
    getMainStyle () {
      return [
        {'height': this.getClientHeight + 'px'}
      ]
    },
    getContextStyle () {
      return [
        {'height': (this.getClientHeight - 90) + 'px'}
      ]
    },
    getRouterViewStyle () {
      return [
        {'min-height': (this.getClientHeight - 90) + 'px'}
      ]
    },
    ...mapGetters(['getClientHeight', 'getInputParams', 'getUserInfo', 'getCollapse', 'getCachePages', 'getMenuItem'])
  },
  mounted () {
    let resetHeight = this.resetDocumentClientHeight();
    resetHeight();
    
    window.onresize = () => {
      resetHeight();
    }
  },
  watch: {
    getMenuItem: {
      handler (newValue) {
        if (newValue == null) {
          if (this.$route.name !== 'welcome') {
            this.$router.replace({
              name: 'welcome'
            });
          }
        } else {
          this.$router.replace({
            name: newValue.formRouterName
          });
        }
      },
      immediate: true
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
