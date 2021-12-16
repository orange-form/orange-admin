/**
 * 工作流常量字典
 */
import Vue from 'vue';
import { DictionaryBase } from './index.js';

const SysFlowEntryBindFormType = new DictionaryBase('流程绑定表单类型', [
  {
    id: 0,
    name: '动态表单',
    symbol: 'ONLINE_FORM'
  },
  {
    id: 1,
    name: '路由表单',
    symbol: 'ROUTER_FORM'
  }
]);
Vue.prototype.SysFlowEntryBindFormType = SysFlowEntryBindFormType;

const SysFlowEntryPublishedStatus = new DictionaryBase('流程设计发布状态', [
  {
    id: 0,
    name: '未发布',
    symbol: 'UNPUBLISHED'
  },
  {
    id: 1,
    name: '已发布',
    symbol: 'PUBLISHED'
  }
]);
Vue.prototype.SysFlowEntryPublishedStatus = SysFlowEntryPublishedStatus;

const SysFlowEntryStep = new DictionaryBase('流程设计步骤', [
  {
    id: 0,
    name: '编辑基础信息',
    symbol: 'BASIC'
  },
  {
    id: 1,
    name: '流程变量设置',
    symbol: 'PROCESS_VARIABLE'
  },
  {
    id: 2,
    name: '设计流程',
    symbol: 'PROCESS_DESIGN'
  }
]);
Vue.prototype.SysFlowEntryStep = SysFlowEntryStep;

const SysFlowTaskOperationType = new DictionaryBase('任务操作类型', [
  {
    id: 'agree',
    name: '同意',
    symbol: 'AGREE'
  },
  {
    id: 'refuse',
    name: '拒绝',
    symbol: 'REFUSE'
  },
  {
    id: 'reject',
    name: '驳回',
    symbol: 'REJECT'
  },
  {
    id: 'revoke',
    name: '撤销',
    symbol: 'REVOKE'
  },
  {
    id: 'transfer',
    name: '转办',
    symbol: 'TRANSFER'
  },
  {
    id: 'multi_consign',
    name: '加签',
    symbol: 'CO_SIGN'
  },
  {
    id: 'save',
    name: '保存',
    symbol: 'SAVE'
  },
  {
    id: 'stop',
    name: '终止',
    symbol: 'STOP'
  },
  {
    id: 'multi_sign',
    name: '会签',
    symbol: 'MULTI_SIGN'
  },
  {
    id: 'multi_agree',
    name: '同意（会签）',
    symbol: 'MULTI_AGREE'
  },
  {
    id: 'multi_refuse',
    name: '拒绝（会签）',
    symbol: 'MULTI_REFUSE'
  },
  {
    id: 'multi_abstain',
    name: '弃权（会签）',
    symbol: 'MULTI_ABSTAIN'
  },
  {
    id: 'set_assignee',
    name: '指定审批人',
    symbol: 'SET_ASSIGNEE'
  }
]);
Vue.prototype.SysFlowTaskOperationType = SysFlowTaskOperationType;

const SysFlowTaskType = new DictionaryBase('工作流任务类型', [
  {
    id: 0,
    name: '其他任务',
    symbol: 'OTHER_TASK'
  },
  {
    id: 1,
    name: '用户任务',
    symbol: 'USER_TASK'
  }
]);
Vue.prototype.SysFlowTaskType = SysFlowTaskType;

const SysFlowVariableType = new DictionaryBase('工作流变量类型', [
  {
    id: 0,
    name: '流程变量',
    symbol: 'INSTANCE'
  },
  {
    id: 1,
    name: '任务变量',
    symbol: 'TASK'
  }
]);
Vue.prototype.SysFlowVariableType = SysFlowVariableType;

const SysFlowWorkOrderStatus = new DictionaryBase('工单状态', [
  {
    id: 0,
    name: '已提交',
    symbol: 'SUBMITED'
  },
  {
    id: 1,
    name: '审批中',
    symbol: 'APPROVING'
  },
  {
    id: 2,
    name: '已拒绝',
    symbol: 'REFUSED'
  },
  {
    id: 3,
    name: '已完成',
    symbol: 'FINISHED'
  },
  {
    id: 4,
    name: '终止',
    symbol: 'STOPPED'
  },
  {
    id: 5,
    name: '撤销',
    symbol: 'CANCEL'
  }
]);
Vue.prototype.SysFlowWorkOrderStatus = SysFlowWorkOrderStatus;

export {
  SysFlowEntryPublishedStatus,
  SysFlowEntryBindFormType,
  SysFlowEntryStep,
  SysFlowTaskOperationType,
  SysFlowTaskType,
  SysFlowVariableType,
  SysFlowWorkOrderStatus
}
