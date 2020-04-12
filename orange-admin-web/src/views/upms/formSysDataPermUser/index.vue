<template>
  <div>
    <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="数据权限">
          <el-select class="filter-item" v-model="formDataPermUser.formFilter.dataPermId" clearable
            placeholder="数据权限" :loading="formDataPermUser.dataPermId.impl.loading"
            @visible-change="formDataPermUser.dataPermId.impl.onVisibleChange"
            @change="onDataPermChange">
            <el-option v-for="item in formDataPermUser.dataPermId.impl.dropdownList" :key="item.dataPermId"
              :value="item.dataPermId" :label="item.dataPermName" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input class="filter-item" v-model="formDataPermUser.formFilter.searchString"
            :clearable="true" placeholder="输入用户名 / 昵称查询" @change="refreshFormDataPermUser(true)" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormDataPermUser(true)">
          查询
        </el-button>
        <el-button slot="operator" type="primary" size="mini" @click="onAddDataPermUserClick()"
          :disabled="!checkPermCodeExist('sysDataPermUserManagement:addDataPermUser') || formDataPermUser.formFilter.dataPermId == null || formDataPermUser.formFilter.dataPermId === ''">
          授权用户
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formDataPermUser.SysDataPermUserList.impl.dataList" size="mini"
          @sort-change="formDataPermUser.SysDataPermUserList.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
            :index="formDataPermUser.SysDataPermUserList.impl.getTableIndex" />
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
              <el-button class="btn-table-delete" type="text" size="mini" @click="onDeleteRow(scope.row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-col :span="24">
          <el-row type="flex" justify="end" style="margin-top: 10px;">
            <el-pagination
              :total="formDataPermUser.SysDataPermUserList.impl.totalCount"
              :current-page="formDataPermUser.SysDataPermUserList.impl.currentPage"
              :page-size="formDataPermUser.SysDataPermUserList.impl.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="formDataPermUser.SysDataPermUserList.impl.onCurrentPageChange"
              @size-change="formDataPermUser.SysDataPermUserList.impl.onPageSizeChange">
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
import { SysDataPermController, DictionaryController } from '@/api';
import formSetSysDataPermUser from '@/views/upms/formSetSysDataPermUser';

export default {
  name: 'formDataPermUser',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formDataPermUser: {
        formFilter: {
          dataPermId: undefined,
          searchString: undefined
        },
        dataPermId: {
          impl: new DropdownWidget(this.loadDataPermDropdownList)
        },
        SysDataPermUserList: {
          impl: new TableWidget(this.loadSysDataPermUserListData, this.loadSysDataPermUserListVerify, true, 'createTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    onDataPermChange (value) {
      this.refreshFormDataPermUser(true);
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
    loadDataPermDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        SysDataPermController.list(this, params).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户列表数据获取函数，返回Primise
     */
    loadSysDataPermUserListData (params) {
      return new Promise((resolve, reject) => {
        if (this.formDataPermUser.formFilter.dataPermId == null || this.formDataPermUser.formFilter.dataPermId === '') {
          this.$message.error('请选择数据权限');
          resolve({
            dataList: [],
            totalCount: 0
          });
          return;
        }

        params.dataPermId = this.formDataPermUser.formFilter.dataPermId;
        params.searchString = this.formDataPermUser.formFilter.searchString;
        SysDataPermController.listDataPermUser(this, params).then(res => {
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
    loadSysDataPermUserListVerify () {
      return true;
    },
    onDeleteRow (row) {
      this.$confirm('是否移除此用户?').then(res => {
        let params = {
          dataPermId: this.formDataPermUser.formFilter.dataPermId,
          userId: row.userId
        }

        SysDataPermController.deleteDataPermUser(this, params).then(res => {
          this.refreshFormDataPermUser(true);
          this.$message.success('移除成功');
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 更新数据权限授权
     */
    refreshFormDataPermUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.formDataPermUser.SysDataPermUserList.impl.refreshTable(true, 1);
      } else {
        this.formDataPermUser.SysDataPermUserList.impl.refreshTable();
      }
      this.formDataPermUser.isInit = true;
    },
    /**
     * 授权用户
     */
    onAddDataPermUserClick () {
      let params = {
        dataPermId: this.formDataPermUser.formFilter.dataPermId
      };

      this.$dialog.show('授权用户', formSetSysDataPermUser, {
        area: ['1100px', '580px']
      }, params).then(res => {
        this.refreshFormDataPermUser(true);
      }).catch(e => {
        this.refreshFormDataPermUser(true);
      });
    },
    onResume () {
      this.refreshFormDataPermUser();
    },
    initFormData () {
    },
    formInit () {
      this.initFormData();
      // this.refreshFormDataPermUser();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
