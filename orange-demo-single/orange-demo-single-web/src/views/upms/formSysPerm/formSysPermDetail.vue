<template>
  <div class="tab-dialog-box" style="position: relative; margin-top: -15px;">
    <el-tabs v-model="activeFragmentId">
      <el-tab-pane label="用户查询" name="fragmentSysPermUser" style="width: 100%;">
        <el-form label-width="100px" size="mini" label-position="left" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="用户名">
              <el-input class="filter-item" v-model="fragmentSysPermUser.formFilter.loginName" clearable
                placeholder="" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysPermUser(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysPermUser.SysUser.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysPermUser.SysUser.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysPermUser.SysUser.impl.getTableIndex" />
              <el-table-column label="用户名" prop="loginName" />
              <el-table-column label="用户昵称" prop="showName" />
              <el-table-column label="用户角色" prop="roleName" />
              <el-table-column label="权限字" prop="permCode" />
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="角色查询" name="fragmentSysPermRole" style="width: 100%;">
        <el-form label-width="100px" size="mini" label-position="left" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="角色名称">
              <el-input class="filter-item" v-model="fragmentSysPermRole.formFilter.roleName" clearable
                placeholder="" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysPermRole(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysPermRole.SysRole.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysPermRole.SysRole.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysPermRole.SysRole.impl.getTableIndex" />
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
              <el-table-column label="权限字" prop="permCode" />
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="菜单查询" name="fragmentSysPermMenu" style="width: 100%;">
        <el-form label-width="100px" size="mini" label-position="left" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="菜单名称">
              <el-input class="filter-item" v-model="fragmentSysPermMenu.formFilter.menuName" clearable
                placeholder="" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysPermMenu(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysPermMenu.SysMenu.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysPermMenu.SysMenu.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysPermMenu.SysMenu.impl.getTableIndex" />
              <el-table-column label="菜单">
                <template slot-scope="scope">
                  <span>{{getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName}}</span>
                </template>
              </el-table-column>
              <el-table-column label="菜单类型" prop="menuType">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getMenuType(scope.row)">{{SysMenuType.getValue(scope.row.menuType)}}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="权限字" prop="permCode" />
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
    permId: {
      type: String,
      required: true
    }
  },
  data () {
    return {
      activeFragmentId: 'fragmentSysPermUser',
      menuMap: new Map(),
      fragmentSysPermUser: {
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
      fragmentSysPermRole: {
        formFilter: {
          roleName: undefined
        },
        formFilterCopy: {
          roleName: undefined
        },
        SysRole: {
          impl: new TableWidget(this.loadSysRoleData, this.loadSysRoleVerify, false, false)
        }
      },
      fragmentSysPermMenu: {
        formFilter: {
          menuName: undefined
        },
        formFilterCopy: {
          menuName: undefined
        },
        SysMenu: {
          impl: new TableWidget(this.loadPermSysMenuData, this.loadPermSysMenuVerify, false, false)
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
      params.permId = this.permId;
      params.loginName = this.fragmentSysPermUser.formFilterCopy.loginName;
      return new Promise((resolve, reject) => {
        SystemController.listSysUserByPermIdWithDetail(this, params).then(res => {
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
      if (this.fragmentSysPermUser.formFilter.loginName == null || this.fragmentSysPermUser.formFilter.loginName === '') {
        this.$message.error('请输入用户名！');
        return false;
      }
      this.fragmentSysPermUser.formFilterCopy.loginName = this.fragmentSysPermUser.formFilter.loginName;
      return true;
    },
    /**
     * 更新用户
     */
    refreshFragmentSysPermUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysPermUser.SysUser.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysPermUser.SysUser.impl.refreshTable();
      }
    },
    /**
     * 获取角色函数，返回Promise
     */
    loadSysRoleData (params) {
      params.permId = this.permId;
      params.roleName = this.fragmentSysPermRole.formFilterCopy.roleName;
      return new Promise((resolve, reject) => {
        SystemController.listSysRoleByPermIdWithDetail(this, params).then(res => {
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
      this.fragmentSysPermRole.formFilterCopy.roleName = this.fragmentSysPermRole.formFilter.roleName;
      return true;
    },
    /**
     * 更新角色
     */
    refreshFragmentSysPermRole (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysPermRole.SysRole.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysPermRole.SysRole.impl.refreshTable();
      }
    },
    /**
     * 获取菜单函数，返回Promise
     */
    loadPermSysMenuData (params) {
      params.permId = this.permId;
      params.menuName = this.fragmentSysPermMenu.formFilterCopy.menuName;
      return new Promise((resolve, reject) => {
        SystemController.listSysMenuByPermIdWithDetail(this, params).then(res => {
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
    loadPermSysMenuVerify () {
      this.fragmentSysPermMenu.formFilterCopy.menuName = this.fragmentSysPermMenu.formFilter.menuName;
      return true;
    },
    /**
     * 更新角色
     */
    refreshFragmentSysPermMenu (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysPermMenu.SysMenu.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysPermMenu.SysMenu.impl.refreshTable();
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
