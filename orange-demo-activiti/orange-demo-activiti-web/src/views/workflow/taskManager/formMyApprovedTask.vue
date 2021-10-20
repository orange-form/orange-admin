<template>
  <!-- 已办任务 -->
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="流程名称">
          <el-input class="filter-item"
            v-model="formFilter.processDefinitionName"
            :clearable="true" placeholder="流程名称"
          />
        </el-form-item>
        <el-form-item label="发起时间">
          <date-range class="filter-item"
            v-model="formFilter.createDate"
            :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormMyApprovedTask(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="teacher" :data="handlerTaskWidget.dataList" size="mini" @sort-change="handlerTaskWidget.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="handlerTaskWidget.getTableIndex" />
          <el-table-column label="流程名称" prop="processDefinitionName" />
          <el-table-column label="流程标识" prop="processDefinitionKey" />
          <el-table-column label="任务名称" prop="name" />
          <el-table-column label="任务发起人" prop="startUser" />
          <el-table-column label="任务发起时间" prop="startTime" />
          <el-table-column label="操作" width="100px">
            <template slot-scope="scope">
              <el-button class="table-btn success" size="mini" type="text" @click="onTaskDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="handlerTaskWidget.totalCount"
            :current-page="handlerTaskWidget.currentPage"
            :page-size="handlerTaskWidget.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="handlerTaskWidget.onCurrentPageChange"
            @size-change="handlerTaskWidget.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import '@/staticDict/flowStaticDict.js';
/* eslint-disable-next-line */
import { TableWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
import { FlowOperationController } from '@/api/flowController.js';

export default {
  name: 'formMyApprovedTask',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formFilter: {
        processDefinitionName: undefined,
        createDate: []
      },
      formFilterCopy: {
        processDefinitionName: undefined,
        createDate: []
      },
      handlerTaskWidget: new TableWidget(this.loadHandlerTaskData, this.loadHandlerTaskVerify, true, false),
      isInit: false
    }
  },
  methods: {
    /**
     * 获取已办任务列表
     */
    loadHandlerTaskData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        processDefinitionName: this.formFilterCopy.processDefinitionName,
        beginDate: this.formFilterCopy.createDate[0],
        endDate: this.formFilterCopy.createDate[1]
      }
      return new Promise((resolve, reject) => {
        FlowOperationController.listHistoricTask(this, params).then(res => {
          resolve({
            dataList: res.data.dataList.map(item => {
              let formInfo = JSON.parse(item.formKey);
              return {
                ...item,
                formInfo
              }
            }),
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 校验已办任务过滤参数
     */
    loadHandlerTaskVerify () {
      this.formFilterCopy.processDefinitionName = this.formFilter.processDefinitionName;
      this.formFilterCopy.createDate = Array.isArray(this.formFilter.createDate) ? [...this.formFilter.createDate] : [];
      return true;
    },
    /**
     * 刷新已办任务列表
     */
    refreshFormMyApprovedTask (reloadData = false) {
      if (reloadData) {
        this.handlerTaskWidget.refreshTable(true, 1);
      } else {
        this.handlerTaskWidget.refreshTable();
      }
    },
    /**
     * 详情
     */
    onTaskDetail (row) {
      let params = {
        taskId: row.id,
        processDefinitionId: row.processDefinitionId,
        processInstanceId: row.processInstanceId
      }

      FlowOperationController.viewHistoricTaskInfo(this, params).then(res => {
        this.$router.push({
          name: res.data.routerName || 'handlerFlowTask',
          query: {
            processDefinitionKey: row.processDefinitionKey,
            taskId: row.id,
            processInstanceId: row.processInstanceId,
            processDefinitionId: row.processDefinitionId,
            formId: (row.formInfo || {}).formId,
            routerName: row.routerName,
            readOnly: true,
            flowEntryName: row.processDefinitionName,
            processInstanceInitiator: row.processInstanceInitiator,
            operationList: res.data.operationList.filter(item => item.type === this.SysFlowTaskOperationType.CO_SIGN)
          }
        });
      }).then(res => {});
    },
    onResume () {
      this.refreshFormMyApprovedTask();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormMyApprovedTask();
    }
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  }
}
</script>

<style>
</style>
