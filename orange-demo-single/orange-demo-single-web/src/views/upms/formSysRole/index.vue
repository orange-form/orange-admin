<template>
  <div class="tab-dialog-box" style="position: relative;">
    <el-tabs v-model="activeFragmentId" :before-leave="onFragmentChange">
      <el-tab-pane label="角色管理" name="fragmentSysRole" style="width: 100%;"
        v-if="checkPermCodeExist('formSysRole:fragmentSysRole')">
        <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="角色名称">
              <el-input class="filter-item" v-model="fragmentSysRole.formFilter.sysRoleName"
                :clearable="true" placeholder="角色名称" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysRole(true)">查询</el-button>
            <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formSysRole:fragmentSysRole:add')"
              @click="onAddSysRoleClick()">
              新建
            </el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysRole.SysRole.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysRole.SysRole.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="50px"
                :index="fragmentSysRole.SysRole.impl.getTableIndex" />
              <el-table-column label="角色名称" prop="roleName">
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150px">
                <template slot-scope="scope">
                  <el-button @click="onEditSysRoleClick(scope.row)" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formSysRole:fragmentSysRole:update')">
                    编辑
                  </el-button>
                  <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formSysRole:fragmentSysRole:delete')">
                    删除
                  </el-button>
                  <el-button class="btn-table-primary" type="text" size="mini"
                    v-if="checkPermCodeExist('formSysRole:fragmentSysRole:listSysRolePermDetail')"
                    @click="onSysRolePermClick(scope.row)">
                    权限详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-col :span="24">
              <el-row type="flex" justify="end" style="margin-top: 10px;">
                <el-pagination
                  :total="fragmentSysRole.SysRole.impl.totalCount"
                  :current-page="fragmentSysRole.SysRole.impl.currentPage"
                  :page-size="fragmentSysRole.SysRole.impl.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, prev, pager, next, sizes"
                  @current-change="fragmentSysRole.SysRole.impl.onCurrentPageChange"
                  @size-change="fragmentSysRole.SysRole.impl.onPageSizeChange">
                </el-pagination>
              </el-row>
            </el-col>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="用户授权" name="fragmentSysRoleUser" style="width: 100%;"
        v-if="checkPermCodeExist('formSysRole:fragmentSysRoleUser')">
        <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="用户角色">
              <el-select class="filter-item" v-model="fragmentSysRoleUser.formFilter.sysRoleId" clearable
                placeholder="用户角色" :loading="fragmentSysRoleUser.sysRole.impl.loading"
                @visible-change="fragmentSysRoleUser.sysRole.impl.onVisibleChange"
                @change="onRoleChange">
                <el-option v-for="item in fragmentSysRoleUser.sysRole.impl.dropdownList" :key="item.roleId" :value="item.roleId" :label="item.roleName" />
              </el-select>
            </el-form-item>
            <el-form-item label="用户名">
              <el-input class="filter-item" v-model="fragmentSysRoleUser.formFilter.sysUserLoginName"
                :clearable="true" placeholder="输入用户名 / 昵称查询" @change="refreshFragmentSysRoleUser(true)" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysRoleUser(true)">
              查询
            </el-button>
            <el-button slot="operator" type="primary" size="mini" @click="onAddRow()"
              :disabled="!checkPermCodeExist('formSysRole:fragmentSysRoleUser:addUserRole') ||
              fragmentSysRoleUser.formFilter.sysRoleId == null || fragmentSysRoleUser.formFilter.sysRoleId === ''">
              添加用户
            </el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysRoleUser.SysUser.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysRoleUser.SysUser.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="50px" :index="fragmentSysRoleUser.SysUser.impl.getTableIndex" />
              <el-table-column label="用户名" prop="loginName">
              </el-table-column>
              <el-table-column label="昵称" prop="showName">
              </el-table-column>
              <el-table-column label="账号类型">
                <template slot-scope="scope">
                  <span>{{SysUserType.getValue(scope.row.userType)}}</span>
                </template>
              </el-table-column>
              <el-table-column label="状态">
                <template slot-scope="scope">
                  <el-tag :type="getUserStatusType(scope.row.userStatus)" size="mini">{{SysUserStatus.getValue(scope.row.userStatus)}}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="80px">
                <template slot-scope="scope">
                  <el-button class="btn-table-delete" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formSysRole:fragmentSysRoleUser:deleteUserRole')"
                    @click="onDeleteRow(scope.row)">
                    移除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-col :span="24">
              <el-row type="flex" justify="end" style="margin-top: 10px;">
                <el-pagination
                  :total="fragmentSysRoleUser.SysUser.impl.totalCount"
                  :current-page="fragmentSysRoleUser.SysUser.impl.currentPage"
                  :page-size="fragmentSysRoleUser.SysUser.impl.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, prev, pager, next, sizes"
                  @current-change="fragmentSysRoleUser.SysUser.impl.onCurrentPageChange"
                  @size-change="fragmentSysRoleUser.SysUser.impl.onPageSizeChange">
                </el-pagination>
              </el-row>
            </el-col>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SystemController, DictionaryController } from '@/api';
import formEditSysRole from '../formEditSysRole';
import formSetRoleUsers from '@/views/upms/formSetRoleUsers';
import FormSysRolePerm from './formSysRolePerm.vue';

export default {
  name: 'formSysRole',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      activeFragmentId: undefined,
      fragmentSysRole: {
        formFilter: {
          sysRoleName: undefined
        },
        formFilterCopy: {
          sysRoleName: undefined
        },
        SysRole: {
          impl: new TableWidget(this.loadSysRoleData, this.loadSysRoleVerify, true, false)
        },
        isInit: false
      },
      fragmentSysRoleUser: {
        formFilter: {
          sysRoleId: undefined,
          sysUserLoginName: undefined
        },
        sysRole: {
          impl: new DropdownWidget(this.loadSysRoleDropdownList)
        },
        SysUser: {
          impl: new TableWidget(this.loadSysUserData, this.loadSysUserVerify, true, false)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 用户角色数据获取函数，返回Primise
     */
    loadSysRoleData (params) {
      params.sysRoleDtoFilter = {
        roleName: this.fragmentSysRole.formFilterCopy.sysRoleName
      }
      return new Promise((resolve, reject) => {
        SystemController.getRoleList(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户角色数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysRoleVerify () {
      this.fragmentSysRole.formFilterCopy.sysRoleName = this.fragmentSysRole.formFilter.sysRoleName;
      return true;
    },
    /**
     * 更新角色列表
     */
    refreshFragmentSysRole (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysRole.SysRole.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysRole.SysRole.impl.refreshTable();
      }
      this.fragmentSysRole.isInit = true;
    },
    /**
     * 新建
     */
    onAddSysRoleClick () {
      let params = {};

      this.$dialog.show('新建', formEditSysRole, {
        area: ['800px', '500px'],
        offset: '100px'
      }, params).then(res => {
        this.fragmentSysRoleUser.sysRole.impl.dirty = true;
        this.refreshFragmentSysRole();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onEditSysRoleClick (row) {
      this.loadRowData(row).then(rowData => {
        return this.$dialog.show('编辑角色', formEditSysRole, {
          area: ['600px', '500px']
        }, {
          rowData
        });
      }).then(res => {
        if (row.roleId === this.fragmentSysRoleUser.formFilter.sysRoleId) {
          this.fragmentSysRoleUser.formFilter.sysRoleId = undefined;
          this.fragmentSysRoleUser.SysUser.impl.clearTable();
        }
        this.fragmentSysRoleUser.sysRole.impl.dirty = true;
        this.fragmentSysRole.SysRole.impl.refreshTable();
      }).catch((e) => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        roleId: row.roleId
      };

      this.$confirm('是否删除此角色？').then(res => {
        SystemController.deleteRole(this, params).then(res => {
          this.$message.success('删除成功');
          if (row.roleId === this.fragmentSysRoleUser.formFilter.sysRoleId) {
            this.fragmentSysRoleUser.formFilter.sysRoleId = undefined;
            this.fragmentSysRoleUser.SysUser.impl.clearTable();
          }
          this.fragmentSysRoleUser.sysRole.impl.dirty = true;
          this.fragmentSysRole.SysRole.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    loadRowData (row) {
      return new Promise((resolve, reject) => {
        var params = {
          roleId: row.roleId
        }
        SystemController.getRole(this, params).then(res => {
          if (typeof res.data.permsJsonData === 'string') {
            res.data.permsJsonData = JSON.parse(res.data.permsJsonData);
          }
          resolve(res.data);
        }).catch(e => {
          reject(e);
        });
      });
    },
    buildFragmentPermCodeMap () {
      this.permCodeList = [
        {
          key: 'fragmentSysRole',
          permCode: 'formSysRole:fragmentSysRole',
          refresh: this.refreshFragmentSysRole
        },
        {
          key: 'fragmentSysRoleUser',
          permCode: 'formSysRole:fragmentSysRoleUser',
          refresh: this.refreshFragmentSysRoleUser
        }
      ];
    },
    onFragmentChange (fragmentId) {
      for (let i = 0; i < this.permCodeList.length; i++) {
        if (this.permCodeList[i].key === fragmentId) {
          this.activeFragmentId = fragmentId;
          // if (this.permCodeList[i].refresh) this.permCodeList[i].refresh();
          return true;
        }
      }
      return false;
    },
    getActiveFragment () {
      for (let i = 0; i < this.permCodeList.length; i++) {
        if (this.permCodeList[i].key === this.activeFragmentId) {
          return this.permCodeList[i];
        }
      }
    },
    /**
     * 根据权限获取默认显示的fragment
     */
    getDefaultFragment () {
      for (let i = 0; i < this.permCodeList.length; i++) {
        if (this.checkPermCodeExist(this.permCodeList[i].permCode)) {
          this.activeFragmentId = this.permCodeList[i].key;
          return this.permCodeList[i];
        }
      }
      return undefined;
    },
    onRoleChange (value) {
      this.refreshFragmentSysRoleUser(true);
    },
    getUserStatusType (status) {
      if (status === this.SysUserStatus.NORMAL) {
        return 'success';
      } else if (status === this.SysUserStatus.LOCKED) {
        return 'danger';
      } else {
        return 'info';
      }
    },
    onAddRow () {
      if (this.fragmentSysRoleUser.formFilter.sysRoleId == null || this.fragmentSysRoleUser.formFilter.sysRoleId === '') {
        this.$message.error('请选择角色');
        return false;
      }
      this.$dialog.show('角色用户授权', formSetRoleUsers, {
        area: ['1100px', '600px']
      }, {
        roleId: this.fragmentSysRoleUser.formFilter.sysRoleId
      }).catch(e => {
        this.refreshFragmentSysRoleUser(true);
      });
    },
    onDeleteRow (row) {
      this.$confirm('是否移除此用户?').then(res => {
        let params = {
          roleId: this.fragmentSysRoleUser.formFilter.sysRoleId,
          userId: row.userId
        }
        return SystemController.deleteRoleUser(this, params);
      }).then(res => {
        this.$message.success('移除成功');
        this.refreshFragmentSysRoleUser(true);
      }).catch(e => {});
    },
    /**
     * 用户管理数据获取函数，返回Primise
     */
    loadSysUserData (params) {
      return new Promise((resolve, reject) => {
        if (this.fragmentSysRoleUser.formFilter.sysRoleId == null || this.fragmentSysRoleUser.formFilter.sysRoleId === '') {
          this.$message.error('请选择角色');
          resolve({
            dataList: [],
            totalCount: 0
          });
          return;
        }
        params.roleId = this.fragmentSysRoleUser.formFilter.sysRoleId;
        params.sysUserDtoFilter = {
          loginName: this.fragmentSysRoleUser.formFilter.sysUserLoginName
        }
        SystemController.listRoleUser(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户管理数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysUserVerify () {
      if (this.fragmentSysRoleUser.formFilter.sysRoleId == null || this.fragmentSysRoleUser.formFilter.sysRoleId === '') {
        this.$message.error('请选择角色');
        return false;
      }
      return true;
    },
    /**
     * 角色下拉数据获取函数
     */
    loadSysRoleDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        SystemController.getRoleList(this, params).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    onRuleChange (value) {
      this.refreshFragmentSysRoleUser(true);
    },
    /**
     * 权限详情
     */
    onSysRolePermClick (row) {
      this.$dialog.show('权限详情', FormSysRolePerm, {
        area: '1200px',
        offset: '30px'
      }, {
        roleId: row.roleId
      }).then(res => {}).catch(e => {});
    },
    /**
     * 更新用户管理
     */
    refreshFragmentSysRoleUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysRoleUser.SysUser.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysRoleUser.SysUser.impl.refreshTable();
      }
      this.fragmentSysRoleUser.sysRole.impl.onVisibleChange(true).catch(e => {});
      this.fragmentSysRoleUser.isInit = true;
    },
    onResume () {
      this.refreshFragmentSysRole();
    },
    initFormData () {
    },
    formInit () {
      this.buildFragmentPermCodeMap();
      let defaultFragment = this.getDefaultFragment();
      if (defaultFragment == null) {
        this.$message.error('您没有访问这个页面的权限，请与系统管理员联系！');
      } else {
        if (defaultFragment.refresh) defaultFragment.refresh();
      }
    }
  },
  computed: {
    getTableHeight () {
      return (this.getMainContextHeight - 192);
    },
    ...mapGetters(['getMainContextHeight'])
  },
  created () {
    this.formInit();
  }
}
</script>
