<template>
  <!-- 待办任务 -->
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="流程名称">
          <el-input class="filter-item" v-model="formMyTask.formFilter.processDefinitionName"
            :clearable="true" placeholder="流程名称" />
        </el-form-item>
        <el-form-item label="流程标识">
          <el-input class="filter-item" v-model="formMyTask.formFilter.processDefinitionKey"
            :clearable="true" placeholder="流程标识" />
        </el-form-item>
        <el-form-item label="任务名称">
          <el-input class="filter-item" v-model="formMyTask.formFilter.taskName"
            :clearable="true" placeholder="任务名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshMyTask(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="flowEntry" :data="formMyTask.taskWidget.dataList" size="mini" @sort-change="formMyTask.taskWidget.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formMyTask.taskWidget.getTableIndex" />
          <el-table-column label="流程名称" prop="processDefinitionName" />
          <el-table-column label="当前任务" prop="taskName" />
          <el-table-column label="任务发起人" prop="processInstanceInitiator" />
          <el-table-column label="任务发起时间" prop="processInstanceStartTime" />
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button class="table-btn primary" size="mini" type="text" @click="onSubmit(scope.row)">办理</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formMyTask.taskWidget.totalCount"
            :current-page="formMyTask.taskWidget.currentPage"
            :page-size="formMyTask.taskWidget.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formMyTask.taskWidget.onCurrentPageChange"
            @size-change="formMyTask.taskWidget.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import '@/staticDict/flowStaticDict.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { FlowDictionaryController, FlowOperationController } from '@/api/flowController.js';

export default {
  name: 'formMyTask',
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formMyTask: {
        formFilter: {
          processDefinitionName: undefined,
          processDefinitionKey: undefined,
          taskName: undefined
        },
        formFilterCopy: {
          processDefinitionName: undefined,
          processDefinitionKey: undefined,
          taskName: undefined
        },
        taskWidget: new TableWidget(this.loadTaskData, this.loadTaskDataVerify, true, false, 'entryId', 1),
        isInit: false
      }
    }
  },
  methods: {
    loadTaskData (params) {
      if (params == null) params = {};
      params = {
        processDefinitionName: this.formMyTask.formFilterCopy.processDefinitionName,
        processDefinitionKey: this.formMyTask.formFilterCopy.processDefinitionKey,
        taskName: this.formMyTask.formFilterCopy.taskName,
        ...params
      }
      return new Promise((resolve, reject) => {
        FlowOperationController.listRuntimeTask(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadTaskDataVerify () {
      this.formMyTask.formFilterCopy.processDefinitionName = this.formMyTask.formFilter.processDefinitionName;
      this.formMyTask.formFilterCopy.processDefinitionKey = this.formMyTask.formFilter.processDefinitionKey;
      this.formMyTask.formFilterCopy.taskName = this.formMyTask.formFilter.taskName;
      return true;
    },
    onSubmit (row) {
      let params = {
        processInstanceId: row.processInstanceId,
        processDefinitionId: row.processDefinitionId,
        taskId: row.taskId
      }

      FlowOperationController.viewRuntimeTaskInfo(this, params).then(res => {
        if (res.data) {
          this.$router.push({
            name: res.data.routerName || 'handlerFlowTask',
            query: {
              isRuntime: true,
              taskId: row.taskId,
              processDefinitionKey: row.processDefinitionKey,
              processInstanceId: row.processInstanceId,
              processDefinitionId: row.processDefinitionId,
              formId: res.data.formId,
              routerName: res.data.routerName,
              readOnly: res.data.readOnly,
              taskName: row.taskName,
              flowEntryName: row.processDefinitionName,
              processInstanceInitiator: row.processInstanceInitiator,
              // 过滤掉加签操作，加签只有在已完成任务里可以操作
              operationList: (res.data.operationList || []).filter(item => {
                return item.type !== this.SysFlowTaskOperationType.CO_SIGN && item.type !== this.SysFlowTaskOperationType.REVOKE;
              }),
              variableList: res.data.variableList
            }
          });
        }
      }).catch(e => {});
    },
    refreshMyTask (reloadData = false) {
      if (reloadData) {
        this.formMyTask.taskWidget.refreshTable(true, 1);
      } else {
        this.formMyTask.taskWidget.refreshTable();
      }
    },
    onResume () {
      this.refreshMyTask();
    },
    formInit () {
      this.refreshMyTask();
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
