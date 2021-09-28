<template>
  <div>
    <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="用户状态">
          <el-select class="filter-item" v-model="formSysUser.formFilter.sysUserStatus" :clearable="true"
            placeholder="用户状态" :loading="formSysUser.sysUserStatus.impl.loading"
            @visible-change="formSysUser.sysUserStatus.impl.onVisibleChange"
            @change="onSysUserStatusValueChange">
            <el-option v-for="item in formSysUser.sysUserStatus.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录名称">
          <el-input class="filter-item" v-model="formSysUser.formFilter.sysUserLoginName"
            :clearable="true" placeholder="登录名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSysUser(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formSysUser:fragmentSysUser:add')"
          @click="onAddRow()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formSysUser.SysUser.impl.dataList" size="mini" @sort-change="formSysUser.SysUser.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="50px" :index="formSysUser.SysUser.impl.getTableIndex" />
          <el-table-column label="用户名" prop="loginName" sortable="custom">
          </el-table-column>
          <el-table-column label="昵称" prop="showName">
          </el-table-column>
          <el-table-column label="账号类型" prop="userTypeDictMap.name" />
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
          <el-table-column label="操作" fixed="right" width="220px">
            <template slot-scope="scope">
              <el-button class="btn-table-edit" type="text" size="mini" @click="onEditRow(scope.row)"
                :disabled="isAdmin(scope.row) || !checkPermCodeExist('formSysUser:fragmentSysUser:update')"
              >
                编辑
              </el-button>
              <el-button class="btn-table-delete" type="text" size="mini" @click="onDeleteRow(scope.row)"
                :disabled="isAdmin(scope.row) || !checkPermCodeExist('formSysUser:fragmentSysUser:delete')"
              >
                删除
              </el-button>
              <el-button class="btn-table-delete" type="text" size="mini" @click="onResetPassword(scope.row)"
                :disabled="!checkPermCodeExist('formSysUser:fragmentSysUser:resetPassword')"
              >
                重置密码
              </el-button>
              <el-button class="btn-table-primary" type="text" size="mini"
                v-if="checkPermCodeExist('formSysUser:fragmentSysUser:listSysUserPermDetail')"
                @click="onSysUserPermClick(scope.row)">
                权限详情
              </el-button>
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
import editUser from '@/views/upms/formEditSysUser';
import FormSysUserPerm from './formSysUserPerm.vue';

export default {
  name: 'formSysUser',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formSysUser: {
        formFilter: {
          sysUserStatus: undefined,
          sysUserLoginName: undefined
        },
        formFilterCopy: {
          sysUserStatus: undefined,
          sysUserLoginName: undefined
        },
        sysUserStatus: {
          impl: new DropdownWidget(this.loadSysUserStatusDropdownList)
        },
        SysUser: {
          impl: new TableWidget(this.loadSysUserData, this.loadSysUserVerify, true, false, 'createTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    isAdmin (row) {
      return (row.userType === this.SysUserType.ADMIN);
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
      this.$dialog.show('新建用户', editUser, {
        area: '600px'
      }).then((res) => {
        this.refreshFormSysUser();
      }).catch(() => {
      });
    },
    onEditRow (row) {
      this.loadRowData(row).then(rowData => {
        this.$dialog.show('编辑用户', editUser, {
          area: '600px'
        }, {
          rowData: rowData
        }).then((res) => {
          this.refreshFormSysUser();
        }).catch(e => {
        });
      }).catch(e => {
        //
      });
    },
    onDeleteRow (row) {
      let params = {
        userId: row.userId
      }
      this.$confirm('是否删除用户？').then(res => {
        return SystemController.deleteUser(this, params);
      }).then(res => {
        this.$message.success('删除成功');
        this.refreshFormSysUser(true);
      }).catch(e => {
        //
      });
    },
    onResetPassword (row) {
      this.$confirm('是否重置用户密码？').then(res => {
        return SystemController.resetUserPassword(this, {userId: row.userId});
      }).then(res => {
        this.$message.success('重置密码成功');
      }).catch(e => {
        //
      });
    },
    /**
     * 用户管理数据获取函数，返回Primise
     */
    loadSysUserData (params) {
      params.sysUserDtoFilter = {
        loginName: this.formSysUser.formFilterCopy.sysUserLoginName,
        userStatus: this.formSysUser.formFilterCopy.sysUserStatus
      }
      return new Promise((resolve, reject) => {
        SystemController.getUserList(this, params).then(res => {
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
      this.formSysUser.formFilterCopy.sysUserLoginName = this.formSysUser.formFilter.sysUserLoginName;
      this.formSysUser.formFilterCopy.sysUserStatus = this.formSysUser.formFilter.sysUserStatus;
      return true;
    },
    /**
     * 用户状态下拉数据获取函数
     */
    loadSysUserStatusDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSysUserStatus(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户状态下拉框显隐
     */
    onSysUserStatusVisibleChange (show) {
      this.formSysUser.sysUserStatus.impl.onVisibleChange(show).catch(e => {});
    },
    /**
     * 用户状态选中值改变
     */
    onSysUserStatusValueChange (value) {
    },
    /**
     * 权限详情
     */
    onSysUserPermClick (row) {
      this.$dialog.show('权限详情', FormSysUserPerm, {
        area: '1200px',
        offset: '30px'
      }, {
        userId: row.userId
      }).then(res => {}).catch(e => {});
    },
    /**
     * 更新用户管理
     */
    refreshFormSysUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.formSysUser.SysUser.impl.refreshTable(true, 1);
      } else {
        this.formSysUser.SysUser.impl.refreshTable();
      }
      this.formSysUser.sysUserStatus.impl.onVisibleChange(true).catch(e => {});
      this.formSysUser.isInit = true;
    },
    loadRowData (row) {
      return new Promise((resolve, reject) => {
        var params = {
          userId: row.userId
        }
        
        SystemController.getUser(this, params).then(res => {
          resolve(res.data);
        }).catch(e => {
          reject(e);
        });
      });
    },
    onResume () {
      this.refreshFormSysUser();
    },
    initFormData () {
    },
    formInit () {
      this.initFormData();
      this.refreshFormSysUser();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
