import { SysCustomWidgetKind, SysCustomWidgetType, SysCustomWidgetOperationType } from '@/staticDict/onlineStaticDict.js';

const defaultWidgetAttributes = {
  label: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.Label,
    span: 12,
    placeholder: '',
    defaultValue: '',
    readOnly: true
  },
  input: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.Input,
    span: 12,
    type: 'text',
    placeholder: '',
    defaultValue: '',
    minRows: 2,
    maxRows: 2,
    readOnly: false,
    disabled: false
  },
  numberInput: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.NumberInput,
    span: 12,
    defaultValue: 0,
    min: 0,
    max: 100,
    step: 1,
    precision: 0,
    controlVisible: 1,
    controlPosition: 0,
    readOnly: false,
    disabled: false
  },
  numberRange: {
    widgetKind: SysCustomWidgetKind.Filter,
    widgetType: SysCustomWidgetType.NumberRange,
    readOnly: false,
    disabled: false
  },
  switch: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.Switch,
    span: 12,
    readOnly: false,
    disabled: false
  },
  select: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.Select,
    span: 12,
    placeholder: ''
  },
  cascader: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.Cascader,
    span: 12,
    placeholder: ''
  },
  date: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.Date,
    span: 12,
    placeholder: '',
    type: 'date',
    format: 'yyyy-MM-dd',
    valueFormat: 'yyyy-MM-dd',
    readOnly: false,
    disabled: false
  },
  dateRange: {
    widgetKind: SysCustomWidgetKind.Filter,
    widgetType: SysCustomWidgetType.DateRange,
    readOnly: false,
    disabled: false
  },
  richEditor: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.RichEditor,
    span: 24
  },
  upload: {
    widgetKind: SysCustomWidgetKind.Form,
    widgetType: SysCustomWidgetType.Upload,
    span: 12,
    isImage: true,
    maxFileCount: undefined,
    fileFieldName: 'uploadFile',
    actionUrl: '',
    downloadUrl: ''
  },
  divider: {
    widgetKind: SysCustomWidgetKind.Data,
    widgetType: SysCustomWidgetType.Divider,
    span: 24,
    position: 'center'
  },
  text: {
    widgetKind: SysCustomWidgetKind.Data,
    widgetType: SysCustomWidgetType.Text,
    span: 24,
    color: undefined,
    backgroundColor: undefined,
    fontSize: undefined,
    lineHeight: undefined,
    indent: undefined,
    decoration: 'none',
    align: 'left',
    padding: 0
  },
  image: {
    widgetKind: SysCustomWidgetKind.Data,
    widgetType: SysCustomWidgetType.Image,
    span: 24,
    src: 'https://www.w3school.com.cn/i/eg_tulip.jpg',
    height: undefined,
    width: undefined,
    fit: 'fill'
  },
  table: {
    widgetKind: SysCustomWidgetKind.Data,
    widgetType: SysCustomWidgetType.Table,
    span: 24,
    supportBottom: 0,
    tableInfo: {
      height: undefined,
      paged: true,
      optionColumnWidth: 150
    },
    titleColor: '#409EFF',
    tableColumnList: [],
    operationList: [
      /**
       * 暂时去掉导出操作，等支持后再开启
      {
        id: 0,
        type: SysCustomWidgetOperationType.EXPORT,
        name: '导出',
        enabled: true,
        builtin: true,
        rowOperation: false,
        btnType: 'primary',
        plain: true,
        formId: undefined
      },
       */
      {
        id: 1,
        type: SysCustomWidgetOperationType.ADD,
        name: '新建',
        enabled: true,
        builtin: true,
        rowOperation: false,
        btnType: 'primary',
        plain: false,
        formId: undefined
      },
      {
        id: 2,
        type: SysCustomWidgetOperationType.EDIT,
        name: '编辑',
        enabled: true,
        builtin: true,
        rowOperation: true,
        btnClass: 'table-btn success',
        formId: undefined
      },
      {
        id: 3,
        type: SysCustomWidgetOperationType.DELETE,
        name: '删除',
        enabled: true,
        builtin: true,
        rowOperation: true,
        btnClass: 'table-btn delete',
        formId: undefined
      }
    ],
    queryParamList: []
  },
  card: {
    widgetKind: SysCustomWidgetKind.Container,
    widgetType: SysCustomWidgetType.Card,
    span: 12,
    supportBottom: 0,
    padding: 15,
    gutter: 15,
    height: undefined,
    shadow: 'always',
    childWidgetList: []
  }
}

const defaultFormConfig = {
  formType: undefined,
  formKind: undefined,
  gutter: 20,
  labelWidth: 100,
  labelPosition: 'right',
  tableWidget: undefined,
  width: 800,
  height: undefined
}

const baseWidgetList = [
  {
    id: 'orang_base_card',
    columnName: 'card',
    showName: '基础卡片',
    widgetType: SysCustomWidgetType.Card
  },
  {
    id: 'orange_base_divider',
    columnName: 'divider',
    showName: '分割线',
    widgetType: SysCustomWidgetType.Divider
  },
  {
    id: 'orange_base_text',
    columnName: 'text',
    showName: '文本',
    widgetType: SysCustomWidgetType.Text
  },
  {
    id: 'orange_base_image',
    columnName: 'image',
    showName: '图片',
    widgetType: SysCustomWidgetType.Image
  }
];

export {
  baseWidgetList,
  defaultWidgetAttributes,
  defaultFormConfig
};
