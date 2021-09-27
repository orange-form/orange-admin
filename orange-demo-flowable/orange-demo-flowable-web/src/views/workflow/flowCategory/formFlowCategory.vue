<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="分类名称">
          <el-input class="filter-item" v-model="formFlowCategory.formFilter.name"
            :clearable="true" placeholder="流程分类名称" />
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input class="filter-item" v-model="formFlowCategory.formFilter.code"
            :clearable="true" placeholder="分类编码" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormFlowCategory(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formFlowCategory:formFlowCategory:add')"
          @click="onAddFlowCategoryClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="flowCategory" :data="flowCategoryWidget.dataList" size="mini" @sort-change="flowCategoryWidget.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="flowCategoryWidget.getTableIndex" />
          <el-table-column label="流程分类名称" prop="name">
          </el-table-column>
          <el-table-column label="分类编码" prop="code">
          </el-table-column>
          <el-table-column label="显示顺序" prop="showOrder" sortable="custom">
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" sortable="custom">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button class="table-btn success" @click.stop="onEditFlowCategoryClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formFlowCategory:formFlowCategory:update')">
                编辑
              </el-button>
              <el-button class="table-btn delete" @click.stop="onDeleteFlowCategoryClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formFlowCategory:formFlowCategory:delete')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="flowCategoryWidget.totalCount"
            :current-page="flowCategoryWidget.currentPage"
            :page-size="flowCategoryWidget.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="flowCategoryWidget.onCurrentPageChange"
            @size-change="flowCategoryWidget.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { FlowCategoryController } from '@/api/flowController.js';
import formEditFlowCategory from '@/views/workflow/flowCategory/formEditFlowCategory.vue';

export default {
  name: 'formFlowCategory',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formFlowCategory: {
        formFilter: {
          name: undefined,
          code: undefined
        },
        formFilterCopy: {
          name: undefined,
          code: undefined
        },
        isInit: false
      },
      flowCategoryWidget: new TableWidget(this.loadFlowCategoryWidgetData, this.loadFlowCategoryVerify, true, false, 'showOrder', 1)
    }
  },
  methods: {
    /**
     * FlowCategory数据获取函数，返回Promise
     */
    loadFlowCategoryWidgetData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        flowCategoryDtoFilter: {
          name: this.formFlowCategory.formFilterCopy.name,
          code: this.formFlowCategory.formFilterCopy.code
        }
      }
      return new Promise((resolve, reject) => {
        FlowCategoryController.list(this, params).then(res => {
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
     * FlowCategory数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadFlowCategoryVerify () {
      this.formFlowCategory.formFilterCopy.name = this.formFlowCategory.formFilter.name;
      this.formFlowCategory.formFilterCopy.code = this.formFlowCategory.formFilter.code;
      return true;
    },
    /**
     * 更新流程分类管理
     */
    refreshFormFlowCategory (reloadData = false) {
      if (reloadData) {
        this.flowCategoryWidget.refreshTable(true, 1);
      } else {
        this.flowCategoryWidget.refreshTable();
      }
      if (!this.formFlowCategory.isInit) {
        // 初始化下拉数据
      }
      this.formFlowCategory.isInit = true;
    },
    /**
     * 新建
     */
    onAddFlowCategoryClick () {
      let params = {};

      this.$dialog.show('新建', formEditFlowCategory, {
        area: '500px'
      }, params).then(res => {
        this.refreshFormFlowCategory();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onEditFlowCategoryClick (row) {
      let params = {
        categoryId: row.categoryId
      };

      this.$dialog.show('编辑', formEditFlowCategory, {
        area: '500px'
      }, params).then(res => {
        this.flowCategoryWidget.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteFlowCategoryClick (row) {
      if (
        row.categoryId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        categoryId: row.categoryId
      };

      this.$confirm('是否删除此流程分类？').then(res => {
        FlowCategoryController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.flowCategoryWidget.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormFlowCategory();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormFlowCategory();
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
