<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="应用名称">
          <el-input class="filter-item" v-model="formAuthClient.formFilter.clientDesc"
            :clearable="true" placeholder="应用名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormAuthClient(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini"
          @click="onFormCreateAuthClientClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formAuthClient.AuthClientDetails.impl.dataList" size="mini" @sort-change="formAuthClient.AuthClientDetails.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formAuthClient.AuthClientDetails.impl.getTableIndex" />
          <el-table-column label="应用标识" prop="clientId">
          </el-table-column>
          <el-table-column label="应用名称" prop="clientDesc">
          </el-table-column>
          <el-table-column label="TOKEN 有效期" prop="accessTokenValidity">
          </el-table-column>
          <el-table-column label="回调地址" prop="webServerRedirectUri">
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onFormEditAuthClientClick(scope.row)" type="text" size="mini">
                编辑
              </el-button>
              <el-button @click="onDeleteClick(scope.row)" type="text" size="mini">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formAuthClient.AuthClientDetails.impl.totalCount"
            :current-page="formAuthClient.AuthClientDetails.impl.currentPage"
            :page-size="formAuthClient.AuthClientDetails.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formAuthClient.AuthClientDetails.impl.onCurrentPageChange"
            @size-change="formAuthClient.AuthClientDetails.impl.onPageSizeChange">
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
import { AuthClientDetailsController, DictionaryController } from '@/api';
import formCreateAuthClient from '@/views/generated/formCreateAuthClient';

export default {
  name: 'formAuthClient',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formAuthClient: {
        formFilter: {
          clientDesc: undefined
        },
        formFilterCopy: {
          clientDesc: undefined
        },
        AuthClientDetails: {
          impl: new TableWidget(this.loadAuthClientDetailsData, this.loadAuthClientDetailsVerify, true)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 应用客户端数据获取函数，返回Primise
     */
    loadAuthClientDetailsData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        authClientDetailsFilter: {
          clientDesc: this.formAuthClient.formFilterCopy.clientDesc
        }
      }
      return new Promise((resolve, reject) => {
        AuthClientDetailsController.list(this, params).then(res => {
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
     * 应用客户端数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadAuthClientDetailsVerify () {
      this.formAuthClient.formFilterCopy.clientDesc = this.formAuthClient.formFilter.clientDesc;
      return true;
    },
    /**
     * 更新应用客户端管理
     */
    refreshFormAuthClient (reloadData = false) {
      if (reloadData) {
        this.formAuthClient.AuthClientDetails.impl.refreshTable(true, 1);
      } else {
        this.formAuthClient.AuthClientDetails.impl.refreshTable();
      }
      if (!this.formAuthClient.isInit) {
        // 初始化下拉数据
      }
      this.formAuthClient.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateAuthClientClick () {
      let params = {};

      this.$dialog.show('新建', formCreateAuthClient, {
        area: ['800px']
      }, params).then(res => {
        this.refreshFormAuthClient();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditAuthClientClick (row) {
      let params = {
        clientId: row.clientId
      };

      this.$dialog.show('编辑', formCreateAuthClient, {
        area: ['800px']
      }, params).then(res => {
        this.formAuthClient.AuthClientDetails.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        clientId: row.clientId
      };

      this.$confirm('是否删除此客户端？').then(res => {
        AuthClientDetailsController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formAuthClient.AuthClientDetails.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormAuthClient();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormAuthClient();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
