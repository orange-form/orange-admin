<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="操作员类型">
          <el-select class="filter-item" v-model="formUaaOperator.formFilter.userType" :clearable="true" filterable
            placeholder="操作员类型" :loading="formUaaOperator.userType.impl.loading"
            @visible-change="formUaaOperator.userType.impl.onVisibleChange"
            @change="onUserTypeValueChange">
            <el-option v-for="item in formUaaOperator.userType.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作员名称">
          <el-input class="filter-item" v-model="formUaaOperator.formFilter.searchString"
            :clearable="true" placeholder="操作员登录名称 / 昵称模糊查询" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormUaaOperator(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" @click="onFormCreateUaaOperatorClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formUaaOperator.SysUaaOperator.impl.dataList" size="mini" @sort-change="formUaaOperator.SysUaaOperator.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formUaaOperator.SysUaaOperator.impl.getTableIndex" />
          <el-table-column label="登录名称" prop="loginName">
          </el-table-column>
          <el-table-column label="昵称" prop="showName">
          </el-table-column>
          <el-table-column label="操作员类型" prop="operatorTypeDictMap.name">
          </el-table-column>
          <el-table-column label="操作员头像" min-width="100px">
            <template slot-scope="scope">
              <el-image v-for="item in parseUploadData(scope.row.headImageUrl, {operatorId: scope.row.operatorId, fieldName: 'headImageUrl', asImage: true})"
                :preview-src-list="getPictureList(scope.row.headImageUrl, {operatorId: scope.row.operatorId, fieldName: 'headImageUrl', asImage: true})"
                class="table-cell-image" :key="item.url" :src="item.url" fit="fill">
                <div slot="error" class="table-cell-image">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onFormEditUaaOperatorClick(scope.row)" type="text" size="mini">
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
            :total="formUaaOperator.SysUaaOperator.impl.totalCount"
            :current-page="formUaaOperator.SysUaaOperator.impl.currentPage"
            :page-size="formUaaOperator.SysUaaOperator.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formUaaOperator.SysUaaOperator.impl.onCurrentPageChange"
            @size-change="formUaaOperator.SysUaaOperator.impl.onPageSizeChange">
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
import { SysUaaOperatorController, DictionaryController } from '@/api';
import formCreateUaaOperator from '@/views/generated/formCreateUaaOperator';

export default {
  name: 'formUaaOperator',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formUaaOperator: {
        formFilter: {
          userType: undefined,
          searchString: undefined
        },
        formFilterCopy: {
          userType: undefined,
          searchString: undefined
        },
        userType: {
          impl: new DropdownWidget(this.loadUserTypeDropdownList)
        },
        SysUaaOperator: {
          impl: new TableWidget(this.loadSysUaaOperatorData, this.loadSysUaaOperatorVerify, true)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 操作员数据获取函数，返回Primise
     */
    loadSysUaaOperatorData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        sysUaaOperatorFilter: {
          operatorType: this.formUaaOperator.formFilterCopy.userType,
          searchString: this.formUaaOperator.formFilterCopy.searchString
        }
      }
      return new Promise((resolve, reject) => {
        SysUaaOperatorController.list(this, params).then(res => {
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
     * 操作员数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysUaaOperatorVerify () {
      this.formUaaOperator.formFilterCopy.userType = this.formUaaOperator.formFilter.userType;
      this.formUaaOperator.formFilterCopy.searchString = this.formUaaOperator.formFilter.searchString;
      return true;
    },
    /**
     * 用户类型下拉数据获取函数
     */
    loadUserTypeDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictUserType(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户类型选中值改变
     */
    onUserTypeValueChange (value) {
    },
    /**
     * 更新操作员管理
     */
    refreshFormUaaOperator (reloadData = false) {
      if (reloadData) {
        this.formUaaOperator.SysUaaOperator.impl.refreshTable(true, 1);
      } else {
        this.formUaaOperator.SysUaaOperator.impl.refreshTable();
      }
      if (!this.formUaaOperator.isInit) {
        // 初始化下拉数据
      }
      this.formUaaOperator.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateUaaOperatorClick () {
      let params = {};

      this.$dialog.show('新建', formCreateUaaOperator, {
        area: ['500px']
      }, params).then(res => {
        this.refreshFormUaaOperator();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditUaaOperatorClick (row) {
      let params = {
        operatorId: row.operatorId
      };

      this.$dialog.show('编辑', formCreateUaaOperator, {
        area: ['500px']
      }, params).then(res => {
        this.formUaaOperator.SysUaaOperator.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        operatorId: row.operatorId
      };

      this.$confirm('是否删除此操作员？').then(res => {
        SysUaaOperatorController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formUaaOperator.SysUaaOperator.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 重置密码
     */
    onResetPasswordClick (row) {
      this.$confirm('是否重置操作员密码？').then(res => {
        let params = {
          operatorId: row.operatorId
        };
        
        SysUaaOperatorController.resetPassword(this, params).then(res => {
          this.$message.success('重置密码成功');
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormUaaOperator();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormUaaOperator();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
