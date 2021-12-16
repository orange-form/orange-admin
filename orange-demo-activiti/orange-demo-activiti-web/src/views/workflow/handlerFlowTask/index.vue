<template>
  <HandlerFlowTask
    :processInstanceId="processInstanceId"
    :processDefinitionId="processDefinitionId"
    :flowEntryName="flowEntryName"
    :processInstanceInitiator="processInstanceInitiator"
    :taskName="taskName"
    :operationList="operationList"
    @close="handlerClose(false)"
    @start="handlerStart"
    @submit="handlerOperation"
  >
    <!-- 在线表单页面 -->
    <WorkflowForm v-if="formId != null" ref="workflowForm"
      :formId="formId"
      :readOnly="isReadOnly"
      :formType="SysOnlineFormType.FLOW"
      @ready="onFormReady"
    />
    <!-- 路由页面 -->
    <router-view ref="routerFlowForm" style="width: 100%;"
      :isRuntimeTask="isRuntime"
      :readOnly="readOnly"
      :processInstanceId="processInstanceId"
      :taskId="taskId"
      :taskVariableList="variableList"
    />
  </HandlerFlowTask>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import flowMixins from '../mixins/flowMixins.js';
import { FlowOperationController } from '@/api/flowController.js';
import WorkflowForm from '@/views/onlineForm/index.vue';
import HandlerFlowTask from '@/views/workflow/components/HandlerFlowTask';

export default {
  name: 'handlerFowTask',
  props: {
    isRuntime: {
      type: [Boolean, String],
      default: false
    },
    // 流程标识
    processDefinitionKey: {
      type: String
    },
    // 在线表单formId
    formId: {
      type: String
    },
    // 路由名称
    routerName: {
      type: String
    },
    // 只读页面
    readOnly: {
      type: [String, Boolean],
      default: true
    },
    // 流程实例id
    processInstanceId: {
      type: String
    },
    // 流程定义id
    processDefinitionId: {
      type: String
    },
    // 当前任务节点id
    taskId: {
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
    },
    // 当前任务节点变量列表
    variableList: {
      type: Array
    }
  },
  components: {
    WorkflowForm,
    HandlerFlowTask
  },
  mixins: [flowMixins],
  data () {
    return {
      isStart: false,
      // 在线表单是否渲染完毕
      isFormReady: false,
      // 在线表单页面数据
      formData: undefined,
      // 在线表单页面一对多数据
      oneToManyRelationData: undefined
    }
  },
  methods: {
    // 在线表单渲染完毕
    onFormReady () {
      this.isFormReady = true;
      let flowData = {
        processDefinitionKey: this.processDefinitionKey,
        processInstanceId: this.processInstanceId,
        taskId: this.taskId
      }
      
      this.$refs.workflowForm.setFormData(this.formData, this.oneToManyRelationData, flowData);
    },
    /**
     * 获得路由组件下的函数
     * @param {string} functionName 函数名称
     * @returns {function}
     */
    getRouterCompomentFunction (functionName) {
      return typeof this.$refs.routerFlowForm[functionName] === 'function' ? this.$refs.routerFlowForm[functionName] : undefined;
    },
    /**
     * 获取表单数据
     */
    getMasterData (operationType, assignee) {
      return new Promise((resolve, reject) => {
        if (this.isOnlineForm) {
          this.$refs.workflowForm.getFormData().then(formData => {
            formData.taskVariableData = this.$refs.workflowForm.getVariableData(this.variableList);
            assignee = (assignee && assignee !== '') ? assignee.split(',') : undefined;
            if (operationType === this.SysFlowTaskOperationType.MULTI_SIGN) {
              // 会签操作设置多实例处理人集合
              if (formData.taskVariableData == null) formData.taskVariableData = {};
              formData.taskVariableData.assigneeList = assignee;
            } else if (operationType === this.SysFlowTaskOperationType.SET_ASSIGNEE) {
              // 设置下一个任务节点处理人
              if (formData.taskVariableData == null) formData.taskVariableData = {};
              formData.taskVariableData.appointedAssignee = assignee;
            }
            resolve(formData);
          }).catch(e => {
            reject(e);
          });
        } else {
          // 获得静态表单页面的getMasterData函数
          let funGetMasterData = this.getRouterCompomentFunction('getMasterData');
          return funGetMasterData ? funGetMasterData(this.variableList) : reject();
        }
      });
    },
    /**
     * 启动流程
     */
    handlerStart (operation) {
      if (!this.isOnlineForm) {
        let funHandlerStart = this.getRouterCompomentFunction('handlerStart');
        if (funHandlerStart != null) {
          funHandlerStart(operation).then(res => {
            this.handlerClose();
          }).catch(e => {});
        } else {
          this.$message.error('当前流程并未实现启动功能，请联系管理员！');
        }
      } else {
        this.preHandlerOperation(operation, true).then(res => {
          this.getMasterData(operation.type, (res || {}).assignee).then(formData => {
            FlowOperationController.startAndTakeUserTask(this, {
              processDefinitionKey: this.processDefinitionKey,
              masterData: formData.masterData,
              slaveData: formData.slaveData,
              taskVariableData: formData.taskVariableData,
              flowTaskCommentDto: {
                approvalType: operation.type
              }
            }, {
              processDefinitionKey: this.processDefinitionKey
            }).then(res => {
              this.handlerClose();
              this.$message.success('启动成功！');
            }).catch(e => {});
          });
        }).catch(e => {});
      }
    },
    /**
     * 流程操作
     * @param {Object} operation 流程操作
     */
    handlerOperation (operation) {
      if (this.isOnlineForm) {
        this.preHandlerOperation(operation, this.isStart).then(res => {
          // 加签操作
          if (operation.type === this.SysFlowTaskOperationType.CO_SIGN) {
            this.submitConsign((res || {}).assignee).then(res => {
              this.handlerClose();
            }).catch(e => {});
            return;
          }
          // 驳回操作
          if (operation.type === this.SysFlowTaskOperationType.REJECT) {
            FlowOperationController.rejectRuntimeTask(this, {
              processInstanceId: this.processInstanceId,
              taskId: this.taskId,
              comment: (res || {}).message
            }).then(res => {
              this.handlerClose();
            }).catch(e => {});
            return;
          }
          // 撤销操作
          if (operation.type === this.SysFlowTaskOperationType.REVOKE) {
            this.$confirm('是否撤销此任务？').then(res => {
              FlowOperationController.revokeHistoricTask(this, {
                processInstanceId: this.processInstanceId,
                taskId: this.taskId,
                comment: '任务处理人撤销任务'
              }).then(res => {
                this.handlerClose();
              }).catch(e => {});
            }).catch(e => {});
            return;
          }
          this.getMasterData(operation.type, (res || {}).assignee).then(formData => {
            let params = {
              taskId: this.taskId,
              processInstanceId: this.processInstanceId,
              masterData: formData.masterData,
              slaveData: formData.slaveData,
              flowTaskCommentDto: {
                comment: (res || {}).message,
                approvalType: operation.type,
                delegateAssginee: operation.type === this.SysFlowTaskOperationType.TRANSFER ? (res || {}).assignee : undefined
              },
              taskVariableData: formData.taskVariableData
            }

            FlowOperationController.submitUserTask(this, params).then(res => {
              this.handlerClose();
              this.reloadMessage(this);
              this.$message.success('提交成功！');
            }).catch(e => {});
          });
        }).catch(e => {});
      } else {
        let funHandlerOperation = this.getRouterCompomentFunction('handlerOperation');
        if (funHandlerOperation) {
          funHandlerOperation(operation).then(res => {
            this.handlerClose();
          }).catch(e => {});
        } else {
          this.$message.error('当前流程并未实现处理功能，请联系管理员！');
        }
      }
    },
    /**
     * 初始化流程表单数据
     */
    initFormData () {
      if (this.processInstanceId == null || this.processInstanceId === '' || this.formId == null) {
        return;
      }

      if (this.isOnlineForm) {
        let params = {
          processInstanceId: this.processInstanceId,
          taskId: this.taskId
        }
        // 判断是展示历史流程的数据还是待办流程的数据
        let httpCall = (this.taskId != null && this.isRuntime) ? FlowOperationController.viewUserTask(this, params) : FlowOperationController.viewHistoricProcessInstance(this, params);
        httpCall.then(res => {
          this.isStart = (res.data == null);
          // 一对多数据
          this.oneToManyRelationData = (res.data || {}).oneToMany;
          // 主表数据以及一对一关联数据
          if ((res.data || {}).masterAndOneToOne != null) {
            this.formData = {
              ...res.data.masterAndOneToOne
            };
          }
        }).catch(e => {
        });
      } else {
        let funInitFormData = this.getRouterCompomentFunction('initFormData');
        funInitFormData ? funInitFormData() : this.$message.error('当前流程并未实现页面初始化功能，请联系管理员！');
      }
    },
    ...mapActions(['reloadMessage'])
  },
  computed: {
    isReadOnly () {
      return typeof this.readOnly === 'string' ? this.readOnly === 'true' : this.readOnly;
    },
    isOnlineForm () {
      return this.formId != null;
    },
    ...mapGetters(['getMainContextHeight'])
  },
  mounted () {
    this.initFormData();
  },
  watch: {
    formData: {
      handler (newValue) {
        if (this.isFormReady && newValue) {
          let flowData = {
            processDefinitionKey: this.processDefinitionKey,
            processInstanceId: this.processInstanceId,
            taskId: this.taskId
          }
          
          this.$refs.workflowForm.setFormData(newValue, this.oneToManyRelationData, flowData);
        }
      }
    }
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
