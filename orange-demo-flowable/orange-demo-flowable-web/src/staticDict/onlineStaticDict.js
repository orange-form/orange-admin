/**
 * 在线表单常量字典
 */
import Vue from 'vue';
import { DictionaryBase } from './index.js';

const SysOnlineFieldKind = new DictionaryBase('字段类型', [
  {
    id: 1,
    name: '文件上传字段',
    symbol: 'UPLOAD'
  },
  {
    id: 2,
    name: '图片上传字段',
    symbol: 'UPLOAD_IMAGE'
  },
  {
    id: 3,
    name: '富文本字段',
    symbol: 'RICH_TEXT'
  },
  {
    id: 20,
    name: '创建时间字段',
    symbol: 'CREATE_TIME'
  },
  {
    id: 21,
    name: '创建人字段',
    symbol: 'CREATE_USER_ID'
  },
  {
    id: 22,
    name: '更新时间字段',
    symbol: 'UPDATE_TIME'
  },
  {
    id: 23,
    name: '更新人字段',
    symbol: 'UPDATE_USER_ID'
  },
  /**
   * 暂时屏蔽掉，等租户运营支持在线表单再开启
  {
    id: 30,
    name: '租户过滤字段',
    symbol: 'TENANT_FILTER'
  },
  */
  {
    id: 31,
    name: '逻辑删除字段',
    symbol: 'LOGIC_DELETE'
  }
]);
Vue.prototype.SysOnlineFieldKind = SysOnlineFieldKind;

const SysOnlineDataPermFilterType = new DictionaryBase('数据权限过滤类型', [
  {
    id: 1,
    name: '用户过滤字段',
    symbol: 'USER_FILTER'
  },
  {
    id: 2,
    name: '部门过滤字段',
    symbol: 'DEPT_FILTER'
  }
]);
Vue.prototype.SysOnlineDataPermFilterType = SysOnlineDataPermFilterType;

const SysOnlineRelationType = new DictionaryBase('关联类型', [
  {
    id: 0,
    name: '一对一关联',
    symbol: 'ONE_TO_ONE'
  },
  {
    id: 1,
    name: '一对多关联',
    symbol: 'ONE_TO_MANY'
  }
]);
Vue.prototype.SysOnlineRelationType = SysOnlineRelationType;

const SysOnlineFormType = new DictionaryBase('表单类型', [
  {
    id: 1,
    name: '查询表单',
    symbol: 'QUERY'
  },
  {
    id: 5,
    name: '编辑表单',
    symbol: 'FORM'
  },
  {
    id: 10,
    name: '流程表单',
    symbol: 'FLOW'
  },
  {
    id: 11,
    name: '工单列表',
    symbol: 'WORK_ORDER'
  }
]);
Vue.prototype.SysOnlineFormType = SysOnlineFormType;

const SysOnlineFormKind = new DictionaryBase('表单类别', [
  {
    id: 1,
    name: '弹出窗口',
    symbol: 'DIALOG'
  },
  {
    id: 5,
    name: '跳转页面',
    symbol: 'PAGE'
  }
]);
Vue.prototype.SysOnlineFormKind = SysOnlineFormKind;

const SysOnlinePageType = new DictionaryBase('页面类型', [
  {
    id: 1,
    name: '业务页面',
    symbol: 'BIZ'
  },
  {
    id: 5,
    name: '统计页面',
    symbol: 'STATS'
  },
  {
    id: 10,
    name: '流程页面',
    symbol: 'FLOW'
  }
]);
Vue.prototype.SysOnlinePageType = SysOnlinePageType;

const SysOnlinePageStatus = new DictionaryBase('页面状态', [
  {
    id: 0,
    name: '基础信息录入',
    symbol: 'BASIC'
  },
  {
    id: 1,
    name: '数模模型录入',
    symbol: 'DATASOURCE'
  },
  {
    id: 2,
    name: '表单设计',
    symbol: 'DESIGNING'
  }
]);
Vue.prototype.SysOnlinePageStatus = SysOnlinePageStatus;

const SysOnlineDictType = new DictionaryBase('字典类型', [
  {
    id: 1,
    name: '数据表字典',
    symbol: 'TABLE'
  },
  {
    id: 5,
    name: 'URL字典',
    symbol: 'URL'
  },
  {
    id: 10,
    name: '静态字典',
    symbol: 'STATIC'
  },
  {
    id: 15,
    name: '自定义字典',
    symbol: 'CUSTOM'
  }
]);
Vue.prototype.SysOnlineDictType = SysOnlineDictType;

const SysOnlineRuleType = new DictionaryBase('验证规则类型', [
  {
    id: 1,
    name: '只允许整数',
    symbol: 'INTEGER_ONLY'
  },
  {
    id: 2,
    name: '只允许数字',
    symbol: 'DIGITAL_ONLY'
  },
  {
    id: 3,
    name: '只允许英文字符',
    symbol: 'LETTER_ONLY'
  },
  {
    id: 4,
    name: '范围验证',
    symbol: 'RANGE'
  },
  {
    id: 5,
    name: '邮箱格式验证',
    symbol: 'EMAIL'
  },
  {
    id: 6,
    name: '手机格式验证',
    symbol: 'MOBILE'
  },
  {
    id: 7,
    name: '自定义验证',
    symbol: 'CUSTOM'
  }
]);
Vue.prototype.SysOnlineRuleType = SysOnlineRuleType;

const SysCustomWidgetType = new DictionaryBase('组件类型', [
  {
    id: 0,
    name: '文本显示',
    symbol: 'Label'
  },
  {
    id: 1,
    name: '文本输入框',
    symbol: 'Input'
  },
  {
    id: 3,
    name: '数字输入框',
    symbol: 'NumberInput'
  },
  {
    id: 4,
    name: '数字范围输入框',
    symbol: 'NumberRange'
  },
  {
    id: 5,
    name: '开关组件',
    symbol: 'Switch'
  },
  {
    id: 10,
    name: '下拉选择框',
    symbol: 'Select'
  },
  {
    id: 12,
    name: '级联选择框',
    symbol: 'Cascader'
  },
  {
    id: 20,
    name: '日期选择框',
    symbol: 'Date'
  },
  {
    id: 21,
    name: '日期范围选择框',
    symbol: 'DateRange'
  },
  {
    id: 31,
    name: '上传组件',
    symbol: 'Upload'
  },
  {
    id: 32,
    name: '富文本编辑',
    symbol: 'RichEditor'
  },
  {
    id: 40,
    name: '分割线',
    symbol: 'Divider'
  },
  {
    id: 41,
    name: '文本',
    symbol: 'Text'
  },
  {
    id: 42,
    name: '图片',
    symbol: 'Image'
  },
  {
    id: 100,
    name: '表格组件',
    symbol: 'Table'
  },
  {
    id: 300,
    name: '基础块',
    symbol: 'Block'
  },
  {
    id: 301,
    name: '卡片组件',
    symbol: 'Card'
  }
]);
Vue.prototype.SysCustomWidgetType = SysCustomWidgetType;

const SysCustomWidgetKind = new DictionaryBase('组件类别', [
  {
    id: 0,
    name: '过滤组件',
    symbol: 'Filter'
  },
  {
    id: 1,
    name: '表单组件',
    symbol: 'Form'
  },
  {
    id: 2,
    name: '数据组件',
    symbol: 'Data'
  },
  {
    id: 4,
    name: '容器组件',
    symbol: 'Container'
  }
]);
Vue.prototype.SysCustomWidgetKind = SysCustomWidgetKind;

const SysOnlineColumnFilterType = new DictionaryBase('组件类别', [
  {
    id: 0,
    name: '无过滤',
    symbol: 'NONE'
  },
  {
    id: 1,
    name: '普通过滤',
    symbol: 'EQUAL_FILTER'
  },
  {
    id: 2,
    name: '范围过滤',
    symbol: 'RANFGE_FILTER'
  },
  {
    id: 3,
    name: '模糊过滤',
    symbol: 'LIKE_FILTER'
  }
]);
Vue.prototype.SysOnlineColumnFilterType = SysOnlineColumnFilterType;

const SysCustomWidgetOperationType = new DictionaryBase('操作类型', [
  {
    id: 0,
    name: '新建',
    symbol: 'ADD'
  },
  {
    id: 1,
    name: '编辑',
    symbol: 'EDIT'
  },
  {
    id: 2,
    name: '删除',
    symbol: 'DELETE'
  },
  {
    id: 3,
    name: '导出',
    symbol: 'EXPORT'
  },
  {
    id: 20,
    name: '自定义操作',
    symbol: 'CUSTOM'
  }
]);
Vue.prototype.SysCustomWidgetOperationType = SysCustomWidgetOperationType;

const SysOnlinePageDatasourceFieldStatus = new DictionaryBase('数据表状态', [
  {
    id: 0,
    name: '已删除',
    symbol: 'DELETED'
  },
  {
    id: 1,
    name: '已使用',
    symbol: 'USED'
  },
  {
    id: 0,
    name: '未使用',
    symbol: 'UNUSED'
  }
]);
Vue.prototype.SysOnlinePageDatasourceFieldStatus = SysOnlinePageDatasourceFieldStatus;

const SysOnlinePageSettingStep = new DictionaryBase('在线表单编辑步骤', [
  {
    id: 0,
    name: '编辑基础信息',
    symbol: 'BASIC'
  },
  {
    id: 1,
    name: '编辑数据模型',
    symbol: 'DATASOURCE'
  },
  {
    id: 2,
    name: '设计表单',
    symbol: 'FORM_DESIGN'
  }
]);
Vue.prototype.SysOnlinePageSettingStep = SysOnlinePageSettingStep;

const SysOnlineParamValueType = new DictionaryBase('参数值类型', [
  {
    id: 0,
    name: '表单参数',
    symbol: 'FORM_PARAM'
  },
  {
    id: 1,
    name: '数据字段',
    symbol: 'TABLE_COLUMN'
  },
  {
    id: 2,
    name: '静态字典',
    symbol: 'STATIC_DICT'
  },
  {
    id: 3,
    name: '直接输入',
    symbol: 'INPUT_VALUE'
  }
]);
Vue.prototype.SysOnlineParamValueType = SysOnlineParamValueType;

const SysOnlineAggregationType = new DictionaryBase('字段聚合类型', [
  {
    id: 0,
    name: '总数',
    symbol: 'SUM'
  },
  {
    id: 1,
    name: '个数',
    symbol: 'COUNT'
  },
  {
    id: 2,
    name: '平均数',
    symbol: 'AVG'
  },
  {
    id: 3,
    name: '最小值',
    symbol: 'MIN'
  },
  {
    id: 4,
    name: '最大值',
    symbol: 'MAX'
  }
]);
Vue.prototype.SysOnlineAggregationType = SysOnlineAggregationType;

const SysOnlineFilterOperationType = new DictionaryBase('过滤条件操作类型', [
  {
    id: 0,
    name: ' = ',
    symbol: 'EQUAL'
  },
  {
    id: 1,
    name: ' != ',
    symbol: 'NOT_EQUAL'
  },
  {
    id: 2,
    name: ' >= ',
    symbol: 'GREATER_THAN_OR_EQUAL'
  },
  {
    id: 3,
    name: ' > ',
    symbol: 'GREATER_THAN'
  },
  {
    id: 4,
    name: ' <= ',
    symbol: 'LESS_THAN_OR_EQUAL'
  },
  {
    id: 5,
    name: ' < ',
    symbol: 'LESS_THAN'
  },
  {
    id: 6,
    name: ' like ',
    symbol: 'LIKE'
  },
  {
    id: 7,
    name: ' not null ',
    symbol: 'NOT_NULL'
  },
  {
    id: 8,
    name: ' is null ',
    symbol: 'IS_NULL'
  }
]);
Vue.prototype.SysOnlineFilterOperationType = SysOnlineFilterOperationType;

const SysOnlineVirtualColumnFilterValueType = new DictionaryBase('虚拟字段过滤值类型', [
  {
    id: 0,
    name: '输入值',
    symbol: 'CUSTOM_INPUT'
  },
  {
    id: 1,
    name: '静态字典',
    symbol: 'STATIC_DICT'
  }
]);
Vue.prototype.SysOnlineVirtualColumnFilterValueType = SysOnlineVirtualColumnFilterValueType;

export {
  SysOnlineFieldKind,
  SysOnlineDataPermFilterType,
  SysOnlineRelationType,
  SysOnlineFormType,
  SysOnlineFormKind,
  SysOnlinePageType,
  SysOnlinePageStatus,
  SysOnlineDictType,
  SysOnlineRuleType,
  SysCustomWidgetType,
  SysCustomWidgetKind,
  SysOnlineColumnFilterType,
  SysCustomWidgetOperationType,
  SysOnlinePageSettingStep,
  SysOnlinePageDatasourceFieldStatus,
  SysOnlineParamValueType,
  SysOnlineAggregationType,
  SysOnlineFilterOperationType,
  SysOnlineVirtualColumnFilterValueType
}
