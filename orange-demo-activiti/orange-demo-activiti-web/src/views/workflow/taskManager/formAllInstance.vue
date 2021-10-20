<template>
  <!-- 流程实例 -->
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="流程名称">
          <el-input class="filter-item"
            v-model="formFilter.processDefinitionName"
            :clearable="true" placeholder="流程名称"
          />
        </el-form-item>
        <el-form-item label="发起人">
          <el-input class="filter-item"
            v-model="formFilter.startUser"
            :clearable="true" placeholder="发起人"
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
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormAllInstance(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="teacher" :data="formAllInstanceWidget.dataList" size="mini" @sort-change="formAllInstanceWidget.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formAllInstanceWidget.getTableIndex" />
          <el-table-column label="流程名称" prop="processDefinitionName" />
          <el-table-column label="流程标识" prop="processDefinitionKey" />
          <el-table-column label="任务发起人" prop="startUserId" />
          <el-table-column label="任务发起时间" prop="startTime" />
          <el-table-column label="任务结束时间" prop="endTime" />
          <el-table-column label="操作" width="150px">
            <template slot-scope="scope">
              <el-button class="table-btn success" size="mini" type="text" @click="onShowProcessViewer(scope.row)">流程图</el-button>
              <el-button class="table-btn primary" size="mini" type="text"
                :disabled="scope.row.endTime != null || !checkPermCodeExist('formAllInstance:formAllInstance:stop')"
                @click="onStopTask(scope.row)">
                终止
              </el-button>
              <el-button class="table-btn delete" size="mini" type="text"
                :disabled="scope.row.endTime == null || !checkPermCodeExist('formAllInstance:formAllInstance:delete')"
                @click="onDeleteTask(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formAllInstanceWidget.totalCount"
            :current-page="formAllInstanceWidget.currentPage"
            :page-size="formAllInstanceWidget.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formAllInstanceWidget.onCurrentPageChange"
            @size-change="formAllInstanceWidget.onPageSizeChange">
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
import FormTaskProcessViewer from './formTaskProcessViewer.vue';
import StopTask from './stopTask.vue';

export default {
  name: 'formAllInstance',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formFilter: {
        processDefinitionName: undefined,
        startUser: undefined,
        createDate: []
      },
      formFilterCopy: {
        processDefinitionName: undefined,
        startUser: undefined,
        createDate: []
      },
      formAllInstanceWidget: new TableWidget(this.loadAllTaskData, this.loadAllTaskVerify, true, false),
      isInit: false
    }
  },
  methods: {
    /**
     * 获取所有流程实例
     */
    loadAllTaskData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        processDefinitionName: this.formFilterCopy.processDefinitionName,
        startUser: this.formFilterCopy.startUser,
        beginDate: this.formFilterCopy.createDate[0],
        endDate: this.formFilterCopy.createDate[1]
      }

      return new Promise((resolve, reject) => {
        FlowOperationController.listAllHistoricProcessInstance(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadAllTaskVerify () {
      this.formFilterCopy.processDefinitionName = this.formFilter.processDefinitionName;
      this.formFilterCopy.startUser = this.formFilter.startUser;
      this.formFilterCopy.createDate = Array.isArray(this.formFilter.createDate) ? [...this.formFilter.createDate] : [];
      return true;
    },
    refreshFormAllInstance (reloadData = false) {
      if (reloadData) {
        this.formAllInstanceWidget.refreshTable(true, 1);
      } else {
        this.formAllInstanceWidget.refreshTable();
      }
      if (!this.isInit) {
        // 初始化下拉数据
      }
      this.isInit = true;
    },
    onShowProcessViewer (row) {
      this.$dialog.show('流程图', FormTaskProcessViewer, {
        area: ['1200px', '750px']
      }, {
        processDefinitionId: row.processDefinitionId,
        processInstanceId: row.processInstanceId
      }).catch(e => {});
    },
    onStopTask (row) {
      this.$dialog.show('终止任务', StopTask, {
        area: '500px'
      }, {
        processInstanceId: row.processInstanceId,
        taskId: row.taskId
      }).then(res => {
        this.formAllInstanceWidget.refreshTable();
      }).catch(e => {});
    },
    onDeleteTask (row) {
      this.$confirm('是否删除此流程实例？').then(res => {
        return FlowOperationController.deleteProcessInstance(this, {
          processInstanceId: row.processInstanceId
        });
      }).then(res => {
        this.$message.success('删除成功');
        this.formAllInstanceWidget.refreshTable();
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormAllInstance();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormAllInstance();
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
