<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormMessage(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="flowCategory" :data="messageListWidget.dataList" size="mini" @sort-change="messageListWidget.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="messageListWidget.getTableIndex" />
          <el-table-column label="流程名称" prop="processDefinitionName" />
          <el-table-column label="任务名称" prop="taskName" />
          <el-table-column label="催办人" prop="createUsername" />
          <el-table-column label="任务创建时间" prop="taskStartTime" />
          <el-table-column label="催办时间" prop="createTime" />
          <el-table-column label="操作" width="100px">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="onSubmit(scope.row)">办理</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="messageListWidget.totalCount"
            :current-page="messageListWidget.currentPage"
            :page-size="messageListWidget.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="messageListWidget.onCurrentPageChange"
            @size-change="messageListWidget.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
import { TableWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { FlowOperationController } from '@/api/flowController.js';

export default {
  name: 'formMessage',
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      messageListWidget: new TableWidget(this.loadMessageData, this.loadMessageVerify, true, false, 'createTime', 1)
    }
  },
  methods: {
    loadMessageData (params) {
      if (params == null) params = {};
      return new Promise((resolve, reject) => {
        FlowOperationController.listRemindingTask(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadMessageVerify () {
      return true;
    },
    refreshFormMessage (reloadData = false) {
      if (reloadData) {
        this.messageListWidget.refreshTable(true, 1);
      } else {
        this.messageListWidget.refreshTable();
      }
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
              // 过滤掉加签和撤销操作，加签只有在已完成任务里可以操作
              operationList: (res.data.operationList || []).filter(item => {
                return item.type !== this.SysFlowTaskOperationType.CO_SIGN && item.type !== this.SysFlowTaskOperationType.REVOKE;
              }),
              variableList: res.data.variableList
            }
          });
        }
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormMessage();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormMessage();
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
