<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="登录名称">
          <el-input class="filter-item" v-model="formSysLoginUser.formFilter.loginName" :clearable="true" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormOperationType(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="teacher" :data="formSysLoginUser.loginUser.impl.dataList" size="mini" @sort-change="formSysLoginUser.loginUser.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formSysLoginUser.loginUser.impl.getTableIndex" />
          <el-table-column label="登录名称" prop="loginName" />
          <el-table-column label="用户昵称" prop="showName" />
          <el-table-column label="登录 IP" prop="loginIp" />
          <el-table-column label="登录时间" prop="loginTime" />
          <el-table-column label="操作" fixed="right" width="150px" >
            <template slot-scope="scope">
              <el-button @click.stop="onDeleteLoginUserClick(scope.row)" type="text" size="mini">
                强退
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formSysLoginUser.loginUser.impl.totalCount"
            :current-page="formSysLoginUser.loginUser.impl.currentPage"
            :page-size="formSysLoginUser.loginUser.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formSysLoginUser.loginUser.impl.onCurrentPageChange"
            @size-change="formSysLoginUser.loginUser.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { TableWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SystemController } from '@/api';

export default {
  name: 'formSysLoginUser',
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formSysLoginUser: {
        formFilter: {
          loginName: undefined
        },
        formFilterCopy: {
          loginName: undefined
        },
        loginUser: {
          impl: new TableWidget(this.loadLoginUserWidgetData, this.loadLoginUserVerify, true, false)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 登录用户数据数据获取函数，返回Promise
     */
    loadLoginUserWidgetData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        loginName: this.formSysLoginUser.formFilterCopy.loginName
      }
      return new Promise((resolve, reject) => {
        SystemController.listSysLoginUser(this, params).then(res => {
          console.log(res);
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
     * 登录用户数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadLoginUserVerify () {
      this.formSysLoginUser.formFilterCopy = { ...this.formSysLoginUser.formFilter };
      return true;
    },
    /**
     * 强制踢出登录用户
     */
    onDeleteLoginUserClick (row) {
      this.$confirm('是否删除此用户？').then(res => {
        SystemController.deleteSysLoginUser(this, {
          sessionId: row.sessionId
        }).then(res => {
          this.$message.success('删除成功');
          this.formSysLoginUser.loginUser.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 更新操作日志
     */
    refreshFormOperationType (reloadData = false) {
      if (reloadData) {
        this.formSysLoginUser.loginUser.impl.refreshTable(true, 1);
      } else {
        this.formSysLoginUser.loginUser.impl.refreshTable();
      }
      if (!this.formSysLoginUser.isInit) {
        // 初始化下拉数据
      }
      this.formSysLoginUser.isInit = true;
    },
    onResume () {
      this.refreshFormOperationType();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormOperationType();
    }
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  },
  watch: {
  }
}
</script>

<style>
</style>
