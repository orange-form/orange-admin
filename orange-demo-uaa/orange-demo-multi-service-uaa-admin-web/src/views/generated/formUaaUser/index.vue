<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="用户状态">
          <el-select class="filter-item" v-model="formUaaUser.formFilter.userStatus" :clearable="true" filterable
            placeholder="用户状态" :loading="formUaaUser.userStatus.impl.loading"
            @visible-change="formUaaUser.userStatus.impl.onVisibleChange"
            @change="onUserStatusValueChange">
            <el-option v-for="item in formUaaUser.userStatus.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名称">
          <el-input class="filter-item" v-model="formUaaUser.formFilter.searchString"
            :clearable="true" placeholder="用户登录名称 / 用户昵称模糊查询" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormUaaUser(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" @click="onFormCreateUaaUserClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formUaaUser.SysUaaUser.impl.dataList" size="mini" @sort-change="formUaaUser.SysUaaUser.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formUaaUser.SysUaaUser.impl.getTableIndex" />
          <el-table-column label="登录名称" prop="username">
          </el-table-column>
          <el-table-column label="用户昵称" prop="showName">
          </el-table-column>
          <el-table-column label="用户状态" prop="lockedDictMap.name">
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onFormEditUaaUserClick(scope.row)" type="text" size="mini">
                编辑
              </el-button>
              <el-button @click="onResetPasswordClick(scope.row)" type="text" size="mini">
                重置密码
              </el-button>
              <el-button @click="onDeleteClick(scope.row)" type="text" size="mini">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formUaaUser.SysUaaUser.impl.totalCount"
            :current-page="formUaaUser.SysUaaUser.impl.currentPage"
            :page-size="formUaaUser.SysUaaUser.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formUaaUser.SysUaaUser.impl.onCurrentPageChange"
            @size-change="formUaaUser.SysUaaUser.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SysUaaUserController, DictionaryController } from '@/api';
import formCreateUaaUser from '@/views/generated/formCreateUaaUser';

export default {
  name: 'formUaaUser',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formUaaUser: {
        formFilter: {
          userStatus: undefined,
          searchString: undefined
        },
        formFilterCopy: {
          userStatus: undefined,
          searchString: undefined
        },
        userStatus: {
          impl: new DropdownWidget(this.loadUserStatusDropdownList)
        },
        SysUaaUser: {
          impl: new TableWidget(this.loadSysUaaUserData, this.loadSysUaaUserVerify, true)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * UAA用户数据获取函数，返回Primise
     */
    loadSysUaaUserData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        sysUaaUserFilter: {
          locked: this.formUaaUser.formFilterCopy.userStatus,
          searchString: this.formUaaUser.formFilterCopy.searchString
        }
      }
      return new Promise((resolve, reject) => {
        SysUaaUserController.list(this, params).then(res => {
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
     * UAA用户数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysUaaUserVerify () {
      this.formUaaUser.formFilterCopy.userStatus = this.formUaaUser.formFilter.userStatus;
      this.formUaaUser.formFilterCopy.searchString = this.formUaaUser.formFilter.searchString;
      return true;
    },
    /**
     * 用户状态下拉数据获取函数
     */
    loadUserStatusDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictUserStatus(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户状态选中值改变
     */
    onUserStatusValueChange (value) {
    },
    /**
     * 更新UAA用户管理
     */
    refreshFormUaaUser (reloadData = false) {
      if (reloadData) {
        this.formUaaUser.SysUaaUser.impl.refreshTable(true, 1);
      } else {
        this.formUaaUser.SysUaaUser.impl.refreshTable();
      }
      if (!this.formUaaUser.isInit) {
        // 初始化下拉数据
      }
      this.formUaaUser.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateUaaUserClick () {
      let params = {};

      this.$dialog.show('新建', formCreateUaaUser, {
        area: ['500px']
      }, params).then(res => {
        this.refreshFormUaaUser();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditUaaUserClick (row) {
      let params = {
        userId: row.userId
      };

      this.$dialog.show('编辑', formCreateUaaUser, {
        area: ['500px']
      }, params).then(res => {
        this.formUaaUser.SysUaaUser.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        userId: row.userId
      };

      this.$confirm('是否删除此用户？').then(res => {
        SysUaaUserController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formUaaUser.SysUaaUser.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 重置密码
     */
    onResetPasswordClick (row) {
      this.$confirm('是否重置用户密码？').then(res => {
        let params = {
          uaaUserId: row.userId
        };
        
        SysUaaUserController.resetPassword(this, params).then(res => {
          this.$message.success('重置密码成功');
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormUaaUser();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormUaaUser();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
