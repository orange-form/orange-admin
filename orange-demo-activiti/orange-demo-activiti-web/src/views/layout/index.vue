<template>
  <el-container :style="getMainStyle">
    <el-aside width='250px' class="sidebar">
      <side-bar style="overflow: hidden"></side-bar>
    </el-aside>
    <el-container style="background-color: #F5F8F9">
      <el-header class="header" style="box-shadow: 0px 2px 4px 0px rgba(206, 206, 206, 0.5);">
        <breadcrumb class="breadcrumb-container" />
        <div class="menu-column" v-if="getMultiColumn" style="margin-left: 20px;">
          <el-menu mode="horizontal" :default-active="getCurrentColumnId" @select="onColumnChange">
            <el-menu-item v-for="column in getColumnList" :key="column.columnId" :index="column.columnId">{{column.columnName}}</el-menu-item>
          </el-menu>
        </div>
        <div class="header-menu" style="flex-grow: 1;">
          <el-dropdown class="user-dropdown" trigger="click" @command="handleCommand">
            <span class="el-dropdown-link">{{(getUserInfo || {}).showName}}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item class="user-dropdown-item" command="modifyPassword">修改密码</el-dropdown-item>
              <el-dropdown-item class="user-dropdown-item" command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <img :src="header" class="header-img" />
        </div>
      </el-header>
      <el-main :style="{'padding-bottom': '15px', 'padding-top': (getMultiTags ? '0px' : '15px')}">
        <tag-panel v-if="getMultiTags" :tagList="getTagList" style="margin: 0px 20px;" />
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
import '@/staticDict/onlineStaticDict.js';
import SideBar from './components/sidebar/sidebar.vue';
import { mapGetters, mapMutations } from 'vuex';
import Breadcrumb from './components/breadcrumb';
import TagPanel from './components/tags/tagPanel.vue';
import formModifyPassword from './components/formModifyPassword/index.vue';
import { SystemController } from '@/api';
import { getToken, setToken } from '@/utils';

export default {
  data () {
    return {
      header: require('../../assets/img/default-header.jpg')
    };
  },
  components: {
    'side-bar': SideBar,
    'breadcrumb': Breadcrumb,
    'tag-panel': TagPanel
  },
  methods: {
    toggleSideBar () {
      this.setCollapse(!this.getCollapse);
    },
    onColumnChange (columnId) {
      this.setCurrentColumnId(columnId);
      this.clearCachePage();
      this.$router.replace({
        name: 'welcome'
      });
    },
    resetDocumentClientHeight () {
      let timerID;
      let _this = this;
      return function () {
        clearTimeout(timerID);
        timerID = setTimeout(() => {
          var h = document.documentElement['clientHeight'];
          var w = document.documentElement['clientWidth'];
          _this.setClientHeight(h);
          _this.setClientWidth(w);
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
              Authorization: getToken()
            },
            showMask: false
          }
          SystemController.logout(this, {}, options).catch(e => {});
          this.clearAllTags();
          setToken();
          window.sessionStorage.removeItem('isUaaLogin');
          this.$router.replace({name: 'login'});
        }).catch(e => {});
      } else if (command === 'modifyPassword') {
        this.$dialog.show('修改密码', formModifyPassword, {
          area: ['500px']
        }, {}).catch(e => {});
      }
    },
    ...mapMutations([
      'setClientHeight',
      'setClientWidth',
      'setCurrentColumnId',
      'clearCachePage',
      'clearAllTags',
      'setUserInfo',
      'clearOnlineFormCache',
      'setMenuList'
    ])
  },
  computed: {
    getMainStyle () {
      return [
        {'height': this.getClientHeight + 'px'}
      ]
    },
    getContextStyle () {
      return [
        {'height': this.getMainContextHeight + 'px'}
      ]
    },
    getRouterViewStyle () {
      return [
        {'min-height': this.getMainContextHeight + 'px'}
      ]
    },
    ...mapGetters([
      'getMultiTags',
      'getClientHeight',
      'getUserInfo',
      'getCollapse',
      'getCachePages',
      'getTagList',
      'getMultiColumn',
      'getCurrentColumnId',
      'getColumnList',
      'getMenuItem',
      'getMainContextHeight'
    ])
  },
  mounted () {
    let resetHeight = this.resetDocumentClientHeight();
    resetHeight();
    
    window.onresize = () => {
      resetHeight();
    }

    // 重新获取登录信息
    if (getToken() != null && getToken() !== '' && this.getUserInfo == null) {
      SystemController.getLoginInfo(this, {}).then(data => {
        this.setMenuList(data.data.menuList);
        delete data.data.menuList;
        this.setUserInfo(data.data);
      }).catch(e => {});
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
          if (newValue.onlineFormId == null) {
            this.$router.replace({
              name: newValue.formRouterName
            });
          } else {
            this.clearOnlineFormCache();
            if (newValue.onlineFlowEntryId == null) {
              this.$router.replace({
                name: 'onlineForm',
                query: {
                  formId: newValue.onlineFormId,
                  formType: this.SysOnlineFormType.QUERY
                }
              });
            } else {
              this.$router.replace({
                name: 'onlineForm',
                query: {
                  formId: newValue.onlineFormId,
                  entryId: newValue.onlineFlowEntryId,
                  formType: this.SysOnlineFormType.WORK_ORDER
                }
              });
            }
          }
        }
      },
      immediate: true
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
