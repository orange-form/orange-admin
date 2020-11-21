<template>
  <div class="tab-dialog-box" style="position: relative; margin-top: -15px;">
    <el-tabs v-model="activeFragmentId">
      <el-tab-pane label="用户查询" name="fragmentSysPermCodeUser" style="width: 100%;">
        <el-form label-width="100px" size="mini" label-position="left" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="用户名">
              <el-input class="filter-item" v-model="fragmentSysPermCodeUser.formFilter.loginName" clearable
                placeholder="" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysPermCodeUser(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysPermCodeUser.SysUser.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysPermCodeUser.SysUser.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysPermCodeUser.SysUser.impl.getTableIndex" />
              <el-table-column label="用户名" prop="loginName" />
              <el-table-column label="用户昵称" prop="showName" />
              <el-table-column label="用户角色" prop="roleName" />
              <el-table-column label="菜单">
                <template slot-scope="scope">
                  <span>{{getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName}}</span>
                </template>
              </el-table-column>
              <el-table-column label="菜单类型" prop="permCodeType">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getMenuType(scope.row)">{{SysMenuType.getValue(scope.row.menuType)}}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="角色查询" name="fragmentSysPermCodeRole" style="width: 100%;">
        <el-form label-width="100px" size="mini" label-position="left" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="角色名称">
              <el-input class="filter-item" v-model="fragmentSysPermCodeRole.formFilter.roleName" clearable
                placeholder="" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysPermCodeRole(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysPermCodeRole.SysRole.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysPermCodeRole.SysRole.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysPermCodeRole.SysRole.impl.getTableIndex" />
              <el-table-column label="菜单">
                <template slot-scope="scope">
                  <span>{{getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName}}</span>
                </template>
              </el-table-column>
              <el-table-column label="菜单类型" prop="permCodeType">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getMenuType(scope.row)">{{SysMenuType.getValue(scope.row.menuType)}}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="角色名称" prop="roleName" />
            </el-table>
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
import { SystemController } from '@/api';

export default {
  name: 'SysMenuPerm',
  props: {
    permCodeId: {
      type: String,
      required: true
    }
  },
  data () {
    return {
      activeFragmentId: 'fragmentSysPermCodeUser',
      menuMap: new Map(),
      fragmentSysPermCodeUser: {
        formFilter: {
          loginName: undefined
        },
        formFilterCopy: {
          loginName: undefined
        },
        SysUser: {
          impl: new TableWidget(this.loadSysUserData, this.loadSysUserVerify, false, false)
        }
      },
      fragmentSysPermCodeRole: {
        formFilter: {
          roleName: undefined
        },
        formFilterCopy: {
          roleName: undefined
        },
        SysRole: {
          impl: new TableWidget(this.loadSysRoleData, this.loadSysRoleVerify, false, false)
        }
      }
    }
  },
  methods: {
    getMenuType (row) {
      if (row.menuType === 0) {
        return 'primary'
      } else if (row.menuType === 1) {
        return 'success';
      } else if (row.menuType === 2) {
        return 'danger';
      } else if (row.menuType === 3) {
        return 'warning';
      }
    },
    /**
     * 获取所有菜单项
     */
    loadSysMenuData () {
      return new Promise((resolve, reject) => {
        SystemController.getMenuPermList(this, {}).then(res => {
          res.data.forEach(item => {
            this.menuMap.set(item.menuId, item);
          });
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    getMenuPathById (menuId) {
      if (menuId == null || menuId === '') return null;
      let menuPath = [];
      do {
        let menuItem = this.menuMap.get(menuId);
        if (menuItem != null) {
          menuPath.unshift(menuItem);
          menuId = menuItem.parentId;
        } else {
          menuId = null;
        }
      } while (menuId != null);

      return menuPath;
    },
    getMenuPathString (menuPath) {
      if (Array.isArray(menuPath) && menuPath.length > 0) {
        return menuPath.map(item => item.menuName).join(' / ');
      } else {
        return null;
      }
    },
    /**
     * 获取用户函数，返回Promise
     */
    loadSysUserData (params) {
      params.permCodeId = this.permCodeId;
      params.loginName = this.fragmentSysPermCodeUser.formFilterCopy.loginName;
      return new Promise((resolve, reject) => {
        SystemController.listSysUserByPermCodeIdWithDetail(this, params).then(res => {
          resolve({
            dataList: res.data,
            totalCount: res.data.length
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysUserVerify () {
      if (this.fragmentSysPermCodeUser.formFilter.loginName == null || this.fragmentSysPermCodeUser.formFilter.loginName === '') {
        this.$message.error('请输入用户名！');
        return false;
      }
      this.fragmentSysPermCodeUser.formFilterCopy.loginName = this.fragmentSysPermCodeUser.formFilter.loginName;
      return true;
    },
    /**
     * 更新用户
     */
    refreshFragmentSysPermCodeUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysPermCodeUser.SysUser.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysPermCodeUser.SysUser.impl.refreshTable();
      }
    },
    /**
     * 获取角色函数，返回Promise
     */
    loadSysRoleData (params) {
      params.permCodeId = this.permCodeId;
      params.roleName = this.fragmentSysPermCodeRole.formFilterCopy.roleName;
      return new Promise((resolve, reject) => {
        SystemController.listSysRoleByPermCodeIdWithDetail(this, params).then(res => {
          resolve({
            dataList: res.data,
            totalCount: res.data.length
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 角色获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysRoleVerify () {
      this.fragmentSysPermCodeRole.formFilterCopy.roleName = this.fragmentSysPermCodeRole.formFilter.roleName;
      return true;
    },
    /**
     * 更新角色
     */
    refreshFragmentSysPermCodeRole (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysPermCodeRole.SysRole.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysPermCodeRole.SysRole.impl.refreshTable();
      }
    }
  },
  computed: {
    getTableHeight () {
      return (this.getMainContextHeight - 150);
    },
    ...mapGetters(['getMainContextHeight'])
  },
  mounted () {
    this.loadSysMenuData().catch(e => {});
  }
}
</script>

<style>
</style>
