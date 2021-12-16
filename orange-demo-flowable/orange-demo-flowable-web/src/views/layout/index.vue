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
          <el-popover class="message" style="margin-right: 20px;" width="300" placement="bottom-end" :offset="20" popper-class="message-popover">
            <el-badge slot="reference" is-dot :hidden="(getMessageList || {}).dataList == null || (getMessageList || {}).dataList.length <= 0"
              style="height: 180x; line-height: 18px; cursor: pointer;">
              <i class="el-icon-bell" style="font-size: 18px;" />
            </el-badge>
            <el-table :data="(getMessageList || {}).dataList" size="mini" empty-text="暂无消息" :show-header="false">
              <el-table-column label="流程名称" prop="processDefinitionName" />
              <el-table-column width="80px">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" @click="onSubmit(scope.row)">办理</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button v-if="getMessageList && (getMessageList.dataList || []).length < getMessageList.totalCount"
              size="small" type="text" style="width: 100%;"
              @click="onMoreMessageClick">
              查看更多
            </el-button>
          </el-popover>
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
import { mapGetters, mapMutations, mapActions } from 'vuex';
import Breadcrumb from './components/breadcrumb';
import TagPanel from './components/tags/tagPanel.vue';
import formModifyPassword from './components/formModifyPassword/index.vue';
import { SystemController } from '@/api';
import { FlowOperationController } from '@/api/flowController.js';
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
    // 办理催办任务
    onSubmit (row) {
      console.log(row);
      let params = {
        processInstanceId: row.processInstanceId,
        processDefinitionId: row.processDefinitionId,
        taskId: row.taskId
      }

      FlowOperationController.viewRuntimeTaskInfo(this, params).then(res => {
        if (res.data) {
          this.$router.push({
            name: res.data.routerName || 'handlerFlowTask',
            query: {
              isRuntime: true,
              taskId: row.taskId,
              processDefinitionKey: row.processDefinitionKey,
              processInstanceId: row.processInstanceId,
              processDefinitionId: row.processDefinitionId,
              formId: res.data.formId,
              routerName: res.data.routerName,
              readOnly: res.data.readOnly,
              taskName: row.taskName,
              flowEntryName: row.processDefinitionName,
              processInstanceInitiator: row.processInstanceInitiator,
              // 过滤掉加签和撤销操作，只有在已完成任务里可以操作
              operationList: (res.data.operationList || []).filter(item => {
                return item.type !== this.SysFlowTaskOperationType.CO_SIGN && item.type !== this.SysFlowTaskOperationType.REVOKE;
              }),
              variableList: res.data.variableList
            }
          });
        }
      }).catch(e => {});
    },
    // 更多催办消息
    onMoreMessageClick () {
      this.$router.push({
        name: 'formMessage'
      });
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
    ]),
    ...mapActions([
      'startMessage',
      'stopMessage'
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
      'getMessageList',
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

    // 获取催办消息
    this.startMessage(this);
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
  },
  beforeRouteLeave (to, form, next) {
    this.stopMessage();
    next();
  },
  destoryed () {
    this.stopMessage();
  }
}
</script>

<style lang="scss">
  .message-popover {
    padding: 5px!important;
  }

  .message-popover .el-table::before {
    height: 0px!important;
  }

  .message-popover .el-table td {
    border: none;
  }
</style>
