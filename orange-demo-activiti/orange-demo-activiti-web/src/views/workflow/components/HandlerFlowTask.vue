<template>
  <div class="flow-task" style="position: relative;">
    <div class="task-title">
      <div>
        <span class="text">{{flowInfo.flowEntryName}}</span>
        <el-tag v-if="flowInfo.taskName" effect="dark" size="mini" type="success">{{'当前节点：' + flowInfo.taskName}}</el-tag>
        <el-tag v-if="flowInfo.processInstanceInitiator" effect="dark" size="mini" type="info">{{'发起人：' + flowInfo.processInstanceInitiator}}</el-tag>
      </div>
    </div>
    <el-row type="flex" justify="space-between" style="margin-bottom: 15px;">
      <el-radio-group size="small" v-model="currentPage" v-if="processInstanceId != null" style="min-width: 300px;">
        <el-radio-button label="formInfo">表单信息</el-radio-button>
        <el-radio-button v-if="processInstanceId != null" label="flowProcess">流程图</el-radio-button>
        <el-radio-button v-if="processInstanceId != null" label="approveInfo">审批记录</el-radio-button>
      </el-radio-group>
      <el-row class="task-operation" type="flex" justify="end" style="width: 100%;">
        <template v-if="$slots.operations">
          <slot name="operations" />
        </template>
        <template v-else>
          <el-button v-for="(operation, index) in flowOperationList" :key="index"
            size="small" :type="getButtonType(operation.type) || 'primary'" :plain="operation.plain || false"
            @click="handlerOperation(operation)">
            {{operation.label}}
          </el-button>
        </template>
      </el-row>
    </el-row>
    <el-scrollbar class="custom-scroll" :style="{height: (getMainContextHeight - 140) + 'px'}">
      <el-form ref="form" class="full-width-input" style="width: 100%;"
        label-width="100px" size="mini" label-position="right" @submit.native.prevent>
        <!-- 表单信息 -->
        <el-row v-show="currentPage === 'formInfo'" type="flex" :key="formKey">
          <slot />
        </el-row>
        <!-- 审批记录 -->
        <el-row v-if="currentPage === 'approveInfo'" :gutter="20">
          <el-col :span="24">
            <el-table :data="flowTaskCommentList" size="mini" border header-cell-class-name="table-header-gray" :height="(getMainContextHeight - 150) + 'px'">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" />
              <el-table-column label="流程环节" prop="taskName" width="200px" />
              <el-table-column label="执行人" prop="createUsername" width="200px" />
              <el-table-column label="操作" width="150px">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getOperationTagType(scope.row.approvalType)" effect="dark">{{SysFlowTaskOperationType.getValue(scope.row.approvalType)}}</el-tag>
                  <el-tag v-if="scope.row.delegateAssginee != null" size="mini" type="success" effect="plain" style="margin-left: 10px;">{{scope.row.delegateAssginee}}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="审批意见">
                <template slot-scope="scope">
                  <span>{{scope.row.comment ? scope.row.comment : ''}}</span>
                </template>
              </el-table-column>
              <el-table-column label="处理时间" prop="createTime" width="200px" />
            </el-table>
          </el-col>
        </el-row>
        <!-- 流程图 -->
        <el-row v-show="currentPage === 'flowProcess'">
          <ProcessViewer :style="{height: (getMainContextHeight - 148) + 'px'}"
            :xml="processXml"
            :finishedInfo="finishedInfo"
            :allCommentList="flowTaskCommentList"
          />
        </el-row>
      </el-form>
    </el-scrollbar>
    <label class="page-close-box">
      <el-button type="text" @click="onClose(true)" icon="el-icon-close" />
    </label>
  </div>
</template>

<script>
import '../package/theme/index.scss';
import { mapGetters } from 'vuex';
/* eslint-disable-next-line */
import { cachedPageChildMixin } from '@/core/mixins';
import { FlowOperationController } from '@/api/flowController.js';
import ProcessViewer from '@/views/workflow/components/ProcessViewer.vue';

export default {
  name: 'handlerFowTask',
  props: {
    // 流程实例id
    processInstanceId: {
      type: String
    },
    // 流程定义id
    processDefinitionId: {
      type: String
    },
    // 流程名称
    flowEntryName: {
      type: String
    },
    // 发起人
    processInstanceInitiator: {
      type: String
    },
    // 当前任务节点名称
    taskName: {
      type: String
    },
    // 当前任务节点操作列表
    operationList: {
      type: Array
    }
  },
  components: {
    ProcessViewer
  },
  mixins: [cachedPageChildMixin],
  data () {
    return {
      formKey: new Date().getTime(),
      currentPage: 'formInfo',
      processXml: undefined,
      finishedInfo: undefined,
      flowInfo: {
        taskName: this.taskName,
        flowEntryName: this.flowEntryName,
        processInstanceInitiator: this.processInstanceInitiator
      },
      flowTaskCommentList: []
    }
  },
  methods: {
    onClose () {
      this.$emit('close');
    },
    getButtonType (type) {
      switch (type) {
        case this.SysFlowTaskOperationType.AGREE:
        case this.SysFlowTaskOperationType.TRANSFER:
        case this.SysFlowTaskOperationType.CO_SIGN:
        case this.SysFlowTaskOperationType.MULTI_AGREE:
        case this.SysFlowTaskOperationType.MULTI_SIGN:
        case this.SysFlowTaskOperationType.SET_ASSIGNEE:
          return 'primary';
        case this.SysFlowTaskOperationType.SAVE:
          return 'success';
        case this.SysFlowTaskOperationType.REFUSE:
        case this.SysFlowTaskOperationType.PARALLEL_REFUSE:
        case this.SysFlowTaskOperationType.MULTI_REFUSE:
          return 'default';
        case this.SysFlowTaskOperationType.REJECT:
        case this.SysFlowTaskOperationType.REVOKE:
          return 'danger';
        default: return 'default';
      }
    },
    getOperationTagType (type) {
      switch (type) {
        case this.SysFlowTaskOperationType.AGREE:
        case this.SysFlowTaskOperationType.MULTI_AGREE:
        case this.SysFlowTaskOperationType.SET_ASSIGNEE:
          return 'success';
        case this.SysFlowTaskOperationType.REFUSE:
        case this.SysFlowTaskOperationType.PARALLEL_REFUSE:
        case this.SysFlowTaskOperationType.MULTI_REFUSE:
          return 'warning';
        case this.SysFlowTaskOperationType.STOP:
        case this.SysFlowTaskOperationType.REJECT:
        case this.SysFlowTaskOperationType.REVOKE:
          return 'danger';
        default:
          return 'primary';
      }
    },
    onStartFlow (operation) {
      this.$emit('start', operation);
    },
    handlerOperation (operation) {
      if (this.processInstanceId == null) {
        this.onStartFlow(operation);
      } else {
        this.$emit('submit', operation);
      }
    },
    getTaskHighlightData () {
      if (this.processInstanceId == null || this.processInstanceId === '') {
        return;
      }
      let params = {
        processInstanceId: this.processInstanceId
      }

      FlowOperationController.viewHighlightFlowData(this, params).then(res => {
        // 已完成节点
        this.finishedInfo = res.data;
      }).catch(e => {});
    },
    getTaskProcessXml () {
      if (this.processDefinitionId == null || this.processDefinitionId === '') {
        return;
      }
      let params = {
        processDefinitionId: this.processDefinitionId
      }
      FlowOperationController.viewProcessBpmn(this, params).then(res => {
        // 当前流程实例xml
        this.processXml = res.data;
      }).catch(e => {});
    },
    loadProcessCommentList () {
      this.flowTaskCommentList = [];
      if (this.processInstanceId == null || this.processInstanceId === '') {
        return;
      }
      FlowOperationController.listFlowTaskComment(this, {
        processInstanceId: this.processInstanceId
      }).then(res => {
        this.flowTaskCommentList = res.data;
      }).catch(e => {});
    }
  },
  computed: {
    isReadOnly () {
      return typeof this.readOnly === 'string' ? this.readOnly === 'true' : this.readOnly;
    },
    flowOperationList () {
      if (Array.isArray(this.operationList)) {
        return this.operationList.map(item => {
          if (item.type === this.SysFlowTaskOperationType.MULTI_SIGN && item.multiSignAssignee != null) {
            let multiSignAssignee = {
              ...item.multiSignAssignee
            }
            multiSignAssignee.assigneeList = item.multiSignAssignee.assigneeList ? multiSignAssignee.assigneeList.split(',') : undefined;
            return {
              ...item,
              multiSignAssignee
            }
          } else {
            return {
              ...item
            }
          }
        });
      } else {
        return [];
      }
    },
    ...mapGetters(['getMainContextHeight'])
  },
  mounted () {
    this.getTaskHighlightData();
    this.getTaskProcessXml();
    this.loadProcessCommentList();
  }
}
</script>

<style scoped>
  .task-title {
    display: flex;
    justify-content: space-between;
    padding-bottom: 5px;
    margin-bottom: 10px;
    border-bottom: 3px solid #409EFF;
  }
  .task-title .text {
    height: 28px;
    line-height: 28px;
    font-weight: 600;
    font-size: 16px;
    color: #383838;
  }

  .task-title .el-tag {
    margin-left: 10px;
  }
</style>
