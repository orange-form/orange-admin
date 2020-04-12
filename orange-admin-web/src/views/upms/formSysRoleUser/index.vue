<template>
  <div>
    <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="用户角色">
          <el-select class="filter-item" v-model="formSysUser.formFilter.sysRoleId" clearable
            placeholder="用户角色" :loading="formSysUser.sysRole.impl.loading"
            @visible-change="formSysUser.sysRole.impl.onVisibleChange"
            @change="onRoleChange">
            <el-option v-for="item in formSysUser.sysRole.impl.dropdownList" :key="item.roleId" :value="item.roleId" :label="item.roleName" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input class="filter-item" v-model="formSysUser.formFilter.sysUserLoginName"
            :clearable="true" placeholder="输入用户名 / 昵称查询" @change="refreshFormSysUser(true)" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSysUser(true)">
          查询
        </el-button>
        <el-button slot="operator" type="primary" size="mini" @click="onAddRow()"
          :disabled="!checkPermCodeExist('sysRoleUserManagement:addUserRole') || formSysUser.formFilter.sysRoleId == null || formSysUser.formFilter.sysRoleId === ''">
          添加人员
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formSysUser.SysUser.impl.dataList" size="mini" @sort-change="formSysUser.SysUser.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="50px" :index="formSysUser.SysUser.impl.getTableIndex" />
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
          <el-table-column label="创建时间">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="80px">
            <template slot-scope="scope">
              <el-button class="btn-table-delete" type="text" size="mini"
                :disabled="!checkPermCodeExist('sysRoleUserManagement:deleteUserRole')" @click="onDeleteRow(scope.row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-col :span="24">
          <el-row type="flex" justify="end" style="margin-top: 10px;">
            <el-pagination
              :total="formSysUser.SysUser.impl.totalCount"
              :current-page="formSysUser.SysUser.impl.currentPage"
              :page-size="formSysUser.SysUser.impl.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="formSysUser.SysUser.impl.onCurrentPageChange"
              @size-change="formSysUser.SysUser.impl.onPageSizeChange">
            </el-pagination>
          </el-row>
        </el-col>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SystemController, DictionaryController } from '@/api';
import formSetRoleUsers from '@/views/upms/formSetRoleUsers';

export default {
  name: 'sysRoleUserManagement',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formSysUser: {
        formFilter: {
          sysRoleId: undefined,
          sysUserLoginName: undefined
        },
        sysRole: {
          impl: new DropdownWidget(this.loadSysRoleDropdownList)
        },
        SysUser: {
          impl: new TableWidget(this.loadSysUserData, this.loadSysUserVerify, true, 'createTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    onRoleChange (value) {
      this.refreshFormSysUser(true);
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
      if (this.formSysUser.formFilter.sysRoleId == null || this.formSysUser.formFilter.sysRoleId === '') {
        this.$message.error('请选择角色');
        return false;
      }
      this.$dialog.show('角色用户授权', formSetRoleUsers, {
        area: '1100px',
        offset: '50px'
      }, {
        roleId: this.formSysUser.formFilter.sysRoleId
      }).catch(e => {
        this.refreshFormSysUser(true);
      });
    },
    onDeleteRow (row) {
      this.$confirm('是否移除此用户?').then(res => {
        let params = {
          roleId: this.formSysUser.formFilter.sysRoleId,
          userId: row.userId
        }
        return SystemController.deleteRoleUser(this, params);
      }).then(res => {
        this.$message.success('移除成功');
        this.refreshFormSysUser(true);
      }).catch(e => {});
    },
    /**
     * 用户列表数据获取函数，返回Primise
     */
    loadSysUserData (params) {
      return new Promise((resolve, reject) => {
        if (this.formSysUser.formFilter.sysRoleId == null || this.formSysUser.formFilter.sysRoleId === '') {
          this.$message.error('请选择角色');
          resolve({
            dataList: [],
            totalCount: 0
          });
          return;
        }
        params.roleId = this.formSysUser.formFilter.sysRoleId;
        params.sysUserFilter = {
          loginName: this.formSysUser.formFilter.sysUserLoginName
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
     * 用户列表数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysUserVerify () {
      if (this.formSysUser.formFilter.sysRoleId == null || this.formSysUser.formFilter.sysRoleId === '') {
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
      this.refreshFormSysUser(true);
    },
    /**
     * 更新用户列表
     */
    refreshFormSysUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.formSysUser.SysUser.impl.refreshTable(true, 1);
      } else {
        this.formSysUser.SysUser.impl.refreshTable();
      }
      this.formSysUser.sysRole.impl.onVisibleChange(true).catch(e => {});
      this.formSysUser.isInit = true;
    },
    onResume () {
      this.refreshFormSysUser();
    },
    initFormData () {
    },
    formInit () {
      this.initFormData();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
