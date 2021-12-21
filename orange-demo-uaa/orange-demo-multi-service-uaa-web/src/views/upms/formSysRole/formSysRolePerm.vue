<template>
  <div class="tab-dialog-box" style="position: relative; margin-top: -15px;">
    <el-tabs v-model="activeFragmentId">
      <el-tab-pane label="权限资源" name="fragmentSysRolePerm" style="width: 100%;">
        <el-form label-width="100px" size="mini" label-position="left" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="URL">
              <el-input class="filter-item" v-model="fragmentSysRolePerm.formFilter.url" clearable
                placeholder="" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshfragmentSysRolePerm(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysRolePerm.SysRolePerm.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysRolePerm.SysRolePerm.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysRolePerm.SysRolePerm.impl.getTableIndex" />
              <el-table-column label="菜单">
                <template slot-scope="scope">
                  <span>{{getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName}}</span>
                </template>
              </el-table-column>
              <el-table-column label="所属权限字" prop="permCode" />
              <el-table-column label="URL" prop="url" />
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="权限字" name="fragmentSysRolePermCode" style="width: 100%;">
        <el-form label-width="100px" size="mini" label-position="left" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="权限字">
              <el-input class="filter-item" v-model="fragmentSysRolePermCode.formFilter.permCode" clearable
                placeholder="" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshfragmentSysRolePermCode(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysRolePermCode.SysRolePermCode.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysRolePermCode.SysRolePermCode.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysRolePermCode.SysRolePermCode.impl.getTableIndex" />
              <el-table-column label="菜单">
                <template slot-scope="scope">
                  <span>{{getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName}}</span>
                </template>
              </el-table-column>
              <el-table-column label="权限字" prop="permCode" />
              <el-table-column label="权限字类型" prop="permCodeType">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getPermCodeType(scope.row.permCodeType)">{{SysPermCodeType.getValue(scope.row.permCodeType)}}</el-tag>
                </template>
              </el-table-column>
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
  name: 'SysRolePerm',
  props: {
    roleId: {
      type: String,
      required: true
    }
  },
  data () {
    return {
      activeFragmentId: 'fragmentSysRolePerm',
      menuMap: new Map(),
      fragmentSysRolePerm: {
        formFilter: {
          url: undefined
        },
        formFilterCopy: {
          url: undefined
        },
        SysRolePerm: {
          impl: new TableWidget(this.loadSysRolePermData, this.loadSysRolePermVerify, false, false)
        },
        isInit: false
      },
      fragmentSysRolePermCode: {
        formFilter: {
          permCode: undefined
        },
        formFilterCopy: {
          permCode: undefined
        },
        SysRolePermCode: {
          impl: new TableWidget(this.loadSysRolePermCodeData, this.loadSysRolePermCodeVerify, false, false)
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
     * 获取角色权限资源列表函数，返回Promise
     */
    loadSysRolePermData (params) {
      params.roleId = this.roleId;
      params.url = this.fragmentSysRolePerm.formFilterCopy.url;
      return new Promise((resolve, reject) => {
        SystemController.listSysPermByRoleIdWithDetail(this, params).then(res => {
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
     * 角色权限资源获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysRolePermVerify () {
      this.fragmentSysRolePerm.formFilterCopy.url = this.fragmentSysRolePerm.formFilter.url;
      return true;
    },
    /**
     * 更新角色权限资源
     */
    refreshfragmentSysRolePerm (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysRolePerm.SysRolePerm.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysRolePerm.SysRolePerm.impl.refreshTable();
      }
      this.fragmentSysRolePerm.isInit = true;
    },
    /**
     * 获取角色权限字列表函数，返回Promise
     */
    loadSysRolePermCodeData (params) {
      params.roleId = this.roleId;
      params.permCode = this.fragmentSysRolePermCode.formFilterCopy.permCode;
      return new Promise((resolve, reject) => {
        SystemController.listSysPermCodeByRoleIdWithDetail(this, params).then(res => {
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
     * 角色权限资源获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysRolePermCodeVerify () {
      this.fragmentSysRolePermCode.formFilterCopy.permCode = this.fragmentSysRolePermCode.formFilter.permCode;
      return true;
    },
    getPermCodeType (permCodeType) {
      switch (permCodeType) {
        case this.SysPermCodeType.FORM: return 'primary';
        case this.SysPermCodeType.FRAGMENT: return 'warning';
        case this.SysPermCodeType.OPERATION: return 'success';
        default: return 'info';
      }
    },
    /**
     * 更新角色权限资源
     */
    refreshfragmentSysRolePermCode (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysRolePermCode.SysRolePermCode.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysRolePermCode.SysRolePermCode.impl.refreshTable();
      }
      this.fragmentSysRolePermCode.isInit = true;
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
