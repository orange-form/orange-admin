<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="操作人员">
          <el-input class="filter-item" v-model="formSysOperationLog.formFilter.operatorName" :clearable="true" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select class="filter-item" v-model="formSysOperationLog.formFilter.operationType" :clearable="true" placeholder=""
            filterable :loading="formSysOperationLog.operationType.impl.loading"
            @visible-change="formSysOperationLog.operationType.impl.onVisibleChange">
            <el-option v-for="item in formSysOperationLog.operationType.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作状态">
          <el-select class="filter-item" v-model="formSysOperationLog.formFilter.success" :clearable="true" filterable placeholder="">
            <el-option :value="1" label="成功" />
            <el-option :value="0" label="失败" />
          </el-select>
        </el-form-item>
        <el-form-item label="Trace Id">
          <el-input class="filter-item" v-model="formSysOperationLog.formFilter.traceId" :clearable="true" />
        </el-form-item>
        <el-form-item label="调用时长">
          <input-number-range class="filter-item" v-model="formSysOperationLog.formFilter.elapse" :clearable="true" />
        </el-form-item>
        <el-form-item label="操作日期">
          <date-range class="filter-item" v-model="formSysOperationLog.formFilter.operationTime" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormOperationType(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="teacher" :data="formSysOperationLog.operationLog.impl.dataList" size="mini" @sort-change="formSysOperationLog.operationLog.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formSysOperationLog.operationLog.impl.getTableIndex" />
          <el-table-column label="系统模块" prop="serviceName" width="200px" />
          <el-table-column label="操作类型" prop="operationType" width="150px">
            <template slot-scope="scope">
              <span>{{SysOperationType.getValue(scope.row.operationType)}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作员" prop="operatorName" width="150px" />
          <el-table-column label="调用地址" prop="requestUrl" min-width="300px"/>
          <el-table-column label="登录 IP" prop="requestIp" width="150px" />
          <el-table-column label="调用时长" prop="elapse" width="100px" />
          <el-table-column label="操作状态" prop="success" width="100px" >
            <template slot-scope="scope">
              <el-tag size="mini" v-if="scope.row.success != null"
                :type="scope.row.success ? 'success' : 'danger'">
                {{scope.row.success ? '成功' : '失败'}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作时间" prop="operationTime" width="150px" />
          <el-table-column label="操作" fixed="right" width="150px" >
            <template slot-scope="scope">
              <el-button @click.stop="onFormViewSysOperationLogClick(scope.row)" type="text" size="mini">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formSysOperationLog.operationLog.impl.totalCount"
            :current-page="formSysOperationLog.operationLog.impl.currentPage"
            :page-size="formSysOperationLog.operationLog.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formSysOperationLog.operationLog.impl.onCurrentPageChange"
            @size-change="formSysOperationLog.operationLog.impl.onPageSizeChange">
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
import { SystemController } from '@/api';
import formViewSysOperationLog from '@/views/upms/formViewSysOperationLog';

export default {
  name: 'formSysOperationLog',
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formSysOperationLog: {
        formFilter: {
          operatorName: undefined,
          operationType: undefined,
          success: undefined,
          traceId: undefined,
          elapse: undefined,
          operationTime: undefined
        },
        formFilterCopy: {
          operatorName: undefined,
          operationType: undefined,
          success: undefined,
          traceId: undefined,
          elapse: undefined,
          operationTime: undefined
        },
        operationType: {
          impl: new DropdownWidget(this.loadOperationTypeDropdownList)
        },
        operationLog: {
          impl: new TableWidget(this.loadOperationLogWidgetData, this.loadOperationLogVerify, true, false, 'logId', false)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 操作日志数据数据获取函数，返回Promise
     */
    loadOperationLogWidgetData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        sysOperationLogDtoFilter: {
          operatorName: this.formSysOperationLog.formFilterCopy.operatorName,
          operationType: this.formSysOperationLog.formFilterCopy.operationType,
          success: this.formSysOperationLog.formFilterCopy.success,
          traceId: this.formSysOperationLog.formFilterCopy.traceId,
          elapseMin: Array.isArray(this.formSysOperationLog.formFilterCopy.elapse) ? this.formSysOperationLog.formFilterCopy.elapse[0] : undefined,
          elapseMax: Array.isArray(this.formSysOperationLog.formFilterCopy.elapse) ? this.formSysOperationLog.formFilterCopy.elapse[1] : undefined,
          operationTimeStart: Array.isArray(this.formSysOperationLog.formFilterCopy.operationTime) ? this.formSysOperationLog.formFilterCopy.operationTime[0] : undefined,
          operationTimeEnd: Array.isArray(this.formSysOperationLog.formFilterCopy.operationTime) ? this.formSysOperationLog.formFilterCopy.operationTime[1] : undefined
        }
      }
      return new Promise((resolve, reject) => {
        SystemController.listSysOperationLog(this, params).then(res => {
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
     * 操作日志数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadOperationLogVerify () {
      this.formSysOperationLog.formFilterCopy = { ...this.formSysOperationLog.formFilter };
      return true;
    },
    /**
     * 操作类型下拉数据获取函数
     */
    loadOperationTypeDropdownList () {
      return new Promise((resolve) => {
        resolve(this.SysOperationType.getList());
      });
    },
    /**
     * 操作日志详情
     */
    onFormViewSysOperationLogClick (row) {
      let params = {
        rowData: row
      };

      this.$dialog.show('操作日志详情', formViewSysOperationLog, {
        area: ['800px', '85vh']
      }, params).catch(e => {});
    },
    /**
     * 更新操作日志
     */
    refreshFormOperationType (reloadData = false) {
      if (reloadData) {
        this.formSysOperationLog.operationLog.impl.refreshTable(true, 1);
      } else {
        this.formSysOperationLog.operationLog.impl.refreshTable();
      }
      if (!this.formSysOperationLog.isInit) {
        // 初始化下拉数据
      }
      this.formSysOperationLog.isInit = true;
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
