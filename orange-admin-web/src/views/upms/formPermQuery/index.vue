<template>
  <div>
    <el-tabs tab-position="left" v-model="activeFragmentId" :before-leave="onFragmentChange">
      <el-tab-pane label="角色查询" name="roleQuery" style="height: 300px;">
        <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="权限字">
              <el-cascader class="filter-item" :options="roleQuery.SysPermCode.impl.dropdownList"
                v-model="roleQuery.SysPermCode.value" size="mini"
                :props="roleQuery.permCodeProps" placeholder="请选择权限字" clearable
                @visible-change="roleQuery.SysPermCode.impl.onVisibleChange"
                @change="onRoleQueryPermCodeChange" />
            </el-form-item>
            <el-form-item label="URL">
              <el-input class="filter-item" v-model="roleQuery.formFilter.permUrl"
                :clearable="true" placeholder="请输入URL" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshSysRoleQuery(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="roleQuery.SysRole.impl.dataList" size="mini" @sort-change="roleQuery.SysRole.impl.onSortChange"
              header-cell-class-name="table-header-gray" :height="getTableHeight">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="50px" :index="roleQuery.SysRole.impl.getTableIndex" />
              <el-table-column label="角色名称" prop="roleName">
              </el-table-column>
              <el-table-column label="创建人" prop="createUsername">
              </el-table-column>
              <el-table-column label="创建时间">
                <template slot-scope="scope">
                  <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="150px">
                <template slot-scope="scope">
                  <el-button @click="onShowSysRoleUser(scope.row)" type="text" size="mini">
                    查看角色用户
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-col :span="24">
              <el-row type="flex" justify="end" style="margin-top: 10px;">
                <el-pagination
                  :total="roleQuery.SysRole.impl.totalCount"
                  :current-page="roleQuery.SysRole.impl.currentPage"
                  :page-size="roleQuery.SysRole.impl.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, prev, pager, next, sizes"
                  @current-change="roleQuery.SysRole.impl.onCurrentPageChange"
                  @size-change="roleQuery.SysRole.impl.onPageSizeChange">
                </el-pagination>
              </el-row>
            </el-col>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="用户权限字查询" name="permCodeQuery">
        <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="用户名称">
              <el-input class="filter-item" v-model="permCodeQuery.formFilter.loginName"
                :clearable="true" placeholder="请输入用户名" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshSysPermCodeQuery(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="permCodeQuery.SysPermCode.impl.dataList" size="mini" @sort-change="permCodeQuery.SysPermCode.impl.onSortChange"
              header-cell-class-name="table-header-gray" :height="getTableHeight">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="50px" :index="permCodeQuery.SysPermCode.impl.getTableIndex" />
              <el-table-column label="权限字名称" prop="showName">
              </el-table-column>
              <el-table-column label="权限字类型" prop="permCodeType">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getPermCodeType(scope.row.permCodeType)">{{SysPermCodeType.getValue(scope.row.permCodeType)}}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="显示顺序" prop="showOrder">
              </el-table-column>
              <el-table-column label="权限字标识" prop="permCode">
              </el-table-column>
            </el-table>
            <el-col :span="24">
              <el-row type="flex" justify="end" style="margin-top: 10px;">
                <el-pagination
                  :total="permCodeQuery.SysPermCode.impl.totalCount"
                  :current-page="permCodeQuery.SysPermCode.impl.currentPage"
                  :page-size="permCodeQuery.SysPermCode.impl.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, prev, pager, next, sizes"
                  @current-change="permCodeQuery.SysPermCode.impl.onCurrentPageChange"
                  @size-change="permCodeQuery.SysPermCode.impl.onPageSizeChange">
                </el-pagination>
              </el-row>
            </el-col>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="用户权限查询" name="permQuery">
        <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="所属权限模块">
              <el-cascader class="filter-item" :options="permQuery.PermModule.impl.dropdownList" placeholder="所属权限模块"
                v-model="permQuery.PermModule.value" size="mini" clearable
                :props="{label: 'moduleName', value: 'moduleId'}"
                @visible-change="permQuery.PermModule.impl.onVisibleChange"
                @change="onPermQueryPermModuleChange" />
            </el-form-item>
            <el-form-item label="用户名称">
              <el-input class="filter-item" v-model="permQuery.formFilter.loginName"
                :clearable="true" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="URL">
              <el-input class="filter-item" v-model="permQuery.formFilter.url"
                :clearable="true" placeholder="请输入URL" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshSysPermQuery(true)">查询</el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="permQuery.SysPerm.impl.dataList" size="mini" @sort-change="permQuery.SysPerm.impl.onSortChange"
              header-cell-class-name="table-header-gray" :height="getTableHeight">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="50px" :index="permQuery.SysPerm.impl.getTableIndex" />
              <el-table-column label="权限名称" prop="permName" width="150px">
              </el-table-column>
              <el-table-column label="所属权限模块" prop="moduleName" width="150px">
              </el-table-column>
              <el-table-column label="关联的url" prop="url">
              </el-table-column>
              <el-table-column label="创建时间" width="200px">
                <template slot-scope="scope">
                  <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
                </template>
              </el-table-column>
            </el-table>
            <el-col :span="24">
              <el-row type="flex" justify="end" style="margin-top: 10px;">
                <el-pagination
                  :total="permQuery.SysPerm.impl.totalCount"
                  :current-page="permQuery.SysPerm.impl.currentPage"
                  :page-size="permQuery.SysPerm.impl.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, prev, pager, next, sizes"
                  @current-change="permQuery.SysPerm.impl.onCurrentPageChange"
                  @size-change="permQuery.SysPerm.impl.onPageSizeChange">
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

export default {
  name: 'formPermQuery',
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      activeFragmentId: 'roleQuery',
      roleQuery: {
        formFilter: {
          permCodeId: undefined,
          permUrl: undefined
        },
        formFilterCopy: {
          permCodeId: undefined,
          permUrl: undefined
        },
        permCodeProps: {
          value: 'permCodeId',
          label: 'showName',
          checkStrictly: true
        },
        SysPermCode: {
          impl: new DropdownWidget(this.loadPermCodeDropdownList, true, 'permCodeId'),
          value: []
        },
        SysRole: {
          impl: new TableWidget(this.loadSysRoleData, this.loadSysRoleVerify, true, 'createTime', 1)
        },
        isInit: false
      },
      permCodeQuery: {
        formFilter: {
          loginName: undefined
        },
        formFilterCopy: {
          loginName: undefined
        },
        SysPermCode: {
          impl: new TableWidget(this.loadSysPermCodeData, this.loadSysPermCodeVerify, false)
        },
        isInit: false
      },
      permQuery: {
        formFilter: {
          loginName: undefined,
          permModuleId: undefined,
          url: undefined
        },
        formFilterCopy: {
          loginName: undefined,
          permModuleId: undefined,
          url: undefined
        },
        PermModule: {
          impl: new DropdownWidget(this.loadPermModuleIdDropdownList, true, 'moduleId', 'parentId'),
          value: []
        },
        SysPerm: {
          impl: new TableWidget(this.loadSysPermData, this.loadSysPermVerify, false)
        },
        isInit: false
      }
    }
  },
  methods: {
    onRoleQueryPermCodeChange (value) {
      this.roleQuery.formFilter.permCodeId = Array.isArray(value) ? value[value.length - 1] : undefined;
    },
    /**
     * 获取权限字下拉数据
     */
    loadPermCodeDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        SystemController.getPermCodeList(this, params).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取角色查询数据
     */
    loadSysRoleData (params) {
      // params.permCodeId = this.roleQuery.formFilterCopy.permCodeId;
      /*
      params.sysPermFilter = {
        url: this.roleQuery.formFilterCopy.permUrl
      }
      */
      params.url = this.roleQuery.formFilterCopy.permUrl;
      return new Promise((resolve, reject) => {
        SystemController.queryRoleByURL(this, params).then(res => {
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
     * 角色查询验证
     */
    loadSysRoleVerify () {
      this.roleQuery.formFilterCopy.permCodeId = this.roleQuery.formFilter.permCodeId;
      this.roleQuery.formFilterCopy.permUrl = this.roleQuery.formFilter.permUrl;
      return true;
    },
    /**
     * 跳转到角色用户页面
     */
    onShowSysRoleUser (row) {

    },
    /**
     * 刷新角色查询数据
     */
    refreshSysRoleQuery (reloadData = false) {
      if (reloadData) {
        this.roleQuery.SysRole.impl.refreshTable(true, 1);
      } else {
        this.roleQuery.SysRole.impl.refreshTable();
      }
      this.roleQuery.isInit = true;
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
     * 权限字数据查询
     */
    loadSysPermCodeData (params) {
      params.sysUserFilter = {
        loginName: this.permCodeQuery.formFilterCopy.loginName
      }

      return new Promise((resolve, reject) => {
        SystemController.queryPermCode(this, params).then(res => {
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
     * 权限字数据查询校验
     */
    loadSysPermCodeVerify () {
      this.permCodeQuery.formFilterCopy.loginName = this.permCodeQuery.formFilter.loginName;
      return true;
    },
    /**
     * 刷新权限字查询数据
     */
    refreshSysPermCodeQuery (reloadData = false) {
      if (reloadData) {
        this.permCodeQuery.SysPermCode.impl.refreshTable(true, 1);
      } else {
        this.permCodeQuery.SysPermCode.impl.refreshTable();
      }
      this.permCodeQuery.isInit = true;
    },
    /**
     * 所属权限模块下拉数据获取函数
     */
    loadPermModuleIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        SystemController.getPermGroupList(this, params).then(res => {
          resolve(res.data);
        }).catch(e => {
          reject();
        });
      });
    },
    onPermQueryPermModuleChange (value) {
      this.permQuery.formFilter.permModuleId = Array.isArray(value) ? value[value.length - 1] : undefined;
    },
    /**
     * 获取权限数据
     */
    loadSysPermData (params) {
      params = {
        ...params,
        moduleId: this.permQuery.formFilterCopy.permModuleId,
        loginName: this.permQuery.formFilterCopy.loginName,
        url: this.permQuery.formFilterCopy.url
      }

      return new Promise((resolve, reject) => {
        SystemController.queryPerm(this, params).then(res => {
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
     * 获取权限数据校验
     */
    loadSysPermVerify () {
      this.permQuery.formFilterCopy.permModuleId = this.permQuery.formFilter.permModuleId;
      this.permQuery.formFilterCopy.loginName = this.permQuery.formFilter.loginName;
      this.permQuery.formFilterCopy.url = this.permQuery.formFilter.url;
      return true;
    },
    /**
     * 刷新权限数据
     */
    refreshSysPermQuery (reloadData = false) {
      if (reloadData) {
        this.permQuery.SysPerm.impl.refreshTable(true, 1);
      } else {
        this.permQuery.SysPerm.impl.refreshTable();
      }
      this.permQuery.isInit = true;
    },
    /**
     * Tab切换调用
     */
    onFragmentChange (fragmentId) {
      return true;
    }
  },
  computed: {
    getTableHeight () {
      return (this.getClientHeight - 220) + 'px';
    },
    ...mapGetters(['getClientHeight'])
  }
}
</script>

<style>
</style>
