<template>
  <div style="position: relative;">
    <el-form label-width="80px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="330">
        <el-form-item label="工单状态">
          <el-select class="filter-item" v-model="flowStatus" :clearable="true"
            placeholder="工单状态">
            <el-option v-for="item in SysFlowWorkOrderStatus.getList()" :key="item.id"
              :label="item.name" :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建日期">
          <date-range class="filter-item" v-model="createTime" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini"
          :disabled="processDefinitionKey == null"
          @click="onSearch()">
          查询
        </el-button>
        <el-button slot="operator" type="primary" size="mini"
          :disabled="processDefinitionKey == null"
          @click="onStartFlow()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24" v-if="formConfig.formQueryTable">
        <CustomTable v-if="processDefinitionKey != null" :ref="formConfig.formQueryTable.variableName"
          :widgetConfig="formConfig.formQueryTable" :formType="formConfig.formType"
          :getTableQueryParams="getTableQueryParams"
          :loadTableDataFunc="listWorkOrder"
          @viewWOrkOrder="onView"
          @handlerWOrkOrder="onSubmit"
          @cancelWOrkOrder="onCancelWorkOrder"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import '@/staticDict/flowStaticDict.js';
import { mapGetters, mapMutations } from 'vuex';
import { OnlineFormMixins } from './onlineFormMixins.js';
import CustomTable from '@/views/onlineForm/components/customTable.vue';
import { FlowOperationController, FlowEntryController } from '@/api/flowController.js';

export default {
  name: 'onlineWorkOrder',
  props: {
    formId: {
      type: String,
      required: true
    },
    entryId: {
      type: String,
      required: true
    },
    isPreview: {
      type: Boolean,
      default: false
    }
  },
  mixins: [OnlineFormMixins],
  components: {
    CustomTable
  },
  data () {
    return {
      processDefinitionKey: undefined,
      processDefinitionName: undefined,
      createTime: [],
      flowStatus: undefined
    }
  },
  methods: {
    getTableQueryParams (widget) {
      let queryParams = [];
      if (Array.isArray(widget.queryParamList)) {
        queryParams = widget.queryParamList.map(item => {
          let paramValue = this.getParamValue(item.paramValueType, item.paramValue);
          if (paramValue == null || paramValue === '' || (Array.isArray(paramValue) && paramValue.length === 0)) return;
          let temp = {
            tableName: item.table.tableName,
            columnName: item.column.columnName,
            filterType: item.column.filterType,
            columnValue: item.column.filterType !== this.SysOnlineColumnFilterType.RANFGE_FILTER ? paramValue : undefined
          }

          if (item.column.filterType === this.SysOnlineColumnFilterType.RANFGE_FILTER) {
            temp.columnValueStart = Array.isArray(paramValue) ? paramValue[0] : undefined;
            temp.columnValueEnd = Array.isArray(paramValue) ? paramValue[1] : undefined;
          }

          return temp;
        }).filter(item => item != null);
      }

      return queryParams;
    },
    listWorkOrder (params) {
      if (this.isPreview || this.processDefinitionKey == null) return Promise.reject();
      return new Promise((resolve, reject) => {
        params = {
          ...params,
          flowWorkOrderDtoFilter: {
            flowStatus: this.flowStatus,
            createTimeStart: Array.isArray(this.createTime) ? this.createTime[0] : undefined,
            createTimeEnd: Array.isArray(this.createTime) ? this.createTime[1] : undefined
          }
        }

        FlowOperationController.listWorkOrder(this, params, {
          processDefinitionKey: this.processDefinitionKey
        }).then(res => {
          res.data.dataList = res.data.dataList.map(item => {
            let initTaskInfo = JSON.parse(item.initTaskInfo);
            let runtimeTaskInfo = (Array.isArray(item.runtimeTaskInfoList) && item.runtimeTaskInfoList.length > 0) ? item.runtimeTaskInfoList[0] : {};
            return {
              ...item,
              flowStatus: this.SysFlowWorkOrderStatus.getValue(item.flowStatus),
              ...Object.keys(item.masterData).reduce((retObj, key) => {
                retObj['masterTable__' + key] = item.masterData[key];
                return retObj;
              }, {}),
              initTaskInfo,
              runtimeTaskInfo,
              masterData: undefined
            }
          });
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    onSearch () {
      this.formData.formFilterCopy = {
        ...this.formData.formFilter
      }
      this.$refs[this.formConfig.formQueryTable.variableName].refresh();
    },
    onResume () {
      let key = this.$route.fullPath;
      let cacheFormData = this.getOnlineFormCache[key];
      if (cacheFormData) {
        this.$nextTick(() => {
          if (Array.isArray(this.dropdownWidgetList)) {
            this.dropdownWidgetList.forEach(dropdownWidget => {
              let dropdownWidgetImpl = this.$refs[dropdownWidget.variableName][0];
              if (dropdownWidgetImpl) {
                dropdownWidgetImpl.onVisibleChange();
              }
            });
          }
          this.formData.formFilter = cacheFormData.formFilter;
          this.formData.formFilterCopy = cacheFormData.formFilterCopy;
          this.$refs[this.formConfig.formQueryTable.variableName].setTableWidget(cacheFormData.tableImpl);
          this.removeOnlineFormCache(key);
        });
      }
    },
    onStartFlow () {
      if (this.isPreview || this.processDefinitionKey == null) return;
      let params = {
        processDefinitionKey: this.processDefinitionKey
      }
      FlowOperationController.viewInitialTaskInfo(this, params).then(res => {
        if (res.data && res.data.taskType === this.SysFlowTaskType.USER_TASK && res.data.assignedMe) {
          this.$router.push({
            name: res.data.routerName || 'handlerFlowTask',
            query: {
              processDefinitionKey: this.processDefinitionKey,
              formId: res.data.formId,
              routerName: res.data.routerName,
              readOnly: res.data.readOnly,
              taskName: '启动流程',
              flowEntryName: this.processDefinitionName,
              operationList: (res.data.operationList || []).filter(item => item.type !== this.SysFlowTaskOperationType.CO_SIGN),
              variableList: res.data.variableList
            }
          });
        } else {
          FlowOperationController.startOnly(this, {
            processDefinitionKey: this.processDefinitionKey
          }).then(res => {
            this.$message.success('启动成功！');
          }).catch(e => {});
        }
      }).catch(e => {});
    },
    onSubmit (row) {
      if (this.isPreview || this.processDefinitionKey == null) return;
      let taskId = (Array.isArray(row.runtimeTaskInfoList) && row.runtimeTaskInfoList.length > 0) ? row.runtimeTaskInfoList[0].taskId : undefined;
      let params = {
        processInstanceId: row.processInstanceId,
        processDefinitionId: row.processDefinitionId,
        taskId: taskId
      }

      FlowOperationController.viewRuntimeTaskInfo(this, params).then(res => {
        if (res.data) {
          this.$router.push({
            name: res.data.routerName || 'handlerFlowTask',
            query: {
              isRuntime: true,
              taskId: taskId,
              processDefinitionKey: row.processDefinitionKey,
              processInstanceId: row.processInstanceId,
              processDefinitionId: row.processDefinitionId,
              formId: res.data.formId,
              routerName: res.data.routerName,
              readOnly: res.data.readOnly,
              taskName: (row.runtimeInitialTask || {}).taskName,
              flowEntryName: row.processDefinitionName,
              processInstanceInitiator: row.processInstanceInitiator,
              operationList: (res.data.operationList || []).filter(item => item.type !== this.SysFlowTaskOperationType.CO_SIGN),
              variableList: res.data.variableList
            }
          });
        }
      }).catch(e => {});
    },
    onView (row) {
      if (this.isPreview || this.processDefinitionKey == null) return;
      let params = {
        processInstanceId: row.processInstanceId
      }

      FlowOperationController.viewInitialHistoricTaskInfo(this, params).then(res => {
        if (res.data) {
          this.$router.push({
            name: res.data.routerName || 'handlerFlowTask',
            query: {
              isRuntime: false,
              processDefinitionKey: row.processDefinitionKey,
              processInstanceId: row.processInstanceId,
              processDefinitionId: row.processDefinitionId,
              formId: res.data.formId,
              routerName: res.data.routerName,
              readOnly: true,
              flowEntryName: row.processDefinitionName,
              processInstanceInitiator: row.processInstanceInitiator
            }
          });
        }
      }).catch(e => {});
    },
    onCancelWorkOrder (row) {
      this.$confirm('是否撤销此工单？').then(res => {
        let params = {
          workOrderId: row.workOrderId,
          cancelReason: '主动撤销'
        }

        FlowOperationController.cancelWorkOrder(this, params).then(res => {
          this.$message.success('撤销成功！');
          this.onSearch();
        }).catch(e => {});
      }).catch(e => {});
    },
    ...mapMutations(['removeOnlineFormCache'])
  },
  provide () {
    return {
      preview: () => this.isPreview
    }
  },
  computed: {
    ...mapGetters(['getOnlineFormCache'])
  },
  mounted () {
    FlowEntryController.viewDict(this, {
      entryId: this.entryId
    }).then(res => {
      this.processDefinitionKey = res.data.processDefinitionKey;
      this.processDefinitionName = res.data.processDefinitionName;
    }).catch(e => {
      console.log(e);
    });
  }
}
</script>

<style>
</style>
