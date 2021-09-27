<template>
  <div>
    <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="部门名称">
          <el-input class="filter-item" v-model="formSysDept.formFilter.deptName"
            :clearable="true" placeholder="部门名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSysDept(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formSysDept:fragmentSysDept:add')"
          @click="onCreateSysDeptClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formSysDept.SysDeptList.impl.dataList" size="mini" row-key="deptId"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="50px">
          </el-table-column>
          <el-table-column label="部门名称" prop="deptName">
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="150px">
            <template slot-scope="scope">
            <el-button @click="onEditSysDeptClick(scope.row)" type="text" size="mini"
              :disabled="!checkPermCodeExist('formSysDept:fragmentSysDept:update')">
              编辑
            </el-button>
            <el-button @click="onEditSysDeptPostClick(scope.row)" type="text" size="mini"
              :disabled="!(checkPermCodeExist('formSysDept:fragmentSysDept:editPost') || checkPermCodeExist('formSysDept:fragmentSysDept:viewPost'))">
              岗位
            </el-button>
            <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
              :disabled="!checkPermCodeExist('formSysDept:fragmentSysDept:delete')">
              删除
            </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { treeDataTranslate } from '@/utils';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SysDeptController, DictionaryController } from '@/api';
import formEditSysDept from '../formEditSysDept';

export default {
  name: 'formSysDept',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formSysDept: {
        formFilter: {
          deptName: undefined
        },
        formFilterCopy: {
          deptName: undefined
        },
        SysDeptList: {
          impl: new TableWidget(this.loadSysDeptListData, this.loadSysDeptListVerify, false)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 部门列表数据获取函数，返回Primise
     */
    loadSysDeptListData (params) {
      params.sysDeptDtoFilter = {
        deptName: this.formSysDept.formFilterCopy.deptName
      };
      return new Promise((resolve, reject) => {
        SysDeptController.list(this, params).then(res => {
          resolve({
            dataList: treeDataTranslate(res.data.dataList, 'deptId', 'parentId'),
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 部门列表数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysDeptListVerify () {
      this.formSysDept.formFilterCopy.deptName = this.formSysDept.formFilter.deptName;
      return true;
    },
    /**
     * 部门列表当前页变化函数
     */
    onSysDeptListCurrentPageChange (newPage) {
      this.formSysDept.SysDeptList.impl.onCurrentPageChange(newPage);
    },
    /**
     * 部门列表每页显示数量变化函数（跳转回第一页）
     */
    onSysDeptListPageSizeChange (newPage) {
      this.formSysDept.SysDeptList.impl.onPageSizeChange(newPage);
    },
    /**
     * 更新部门管理
     */
    refreshFormSysDept (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.formSysDept.SysDeptList.impl.refreshTable(true, 1);
      } else {
        this.formSysDept.SysDeptList.impl.refreshTable();
      }
      this.formSysDept.isInit = true;
    },
    /**
     * 新建
     */
    onCreateSysDeptClick () {
      let params = {};

      this.$dialog.show('新建部门', formEditSysDept, {
        area: ['500px']
      }, params).then(res => {
        this.refreshFormSysDept();
      }).catch(e => {});
    },
    /**
     * 编辑部门
     */
    onEditSysDeptClick (row) {
      let params = {
        deptId: row.deptId
      };

      this.$dialog.show('编辑部门', formEditSysDept, {
        area: ['500px']
      }, params).then(res => {
        this.formSysDept.SysDeptList.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 岗位
     */
    onEditSysDeptPostClick (row) {
      let params = {
        deptId: row.deptId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formSysDeptPost', query: params});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        deptId: row.deptId
      };

      this.$confirm('是否删除部门？').then(res => {
        SysDeptController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formSysDept.SysDeptList.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormSysDept();
    },
    initFormData () {
    },
    formInit () {
      this.initFormData();
      this.refreshFormSysDept();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
