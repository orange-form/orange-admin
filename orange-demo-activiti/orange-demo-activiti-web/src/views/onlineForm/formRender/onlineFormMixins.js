import { mapMutations } from 'vuex';
import * as StaticDict from '@/staticDict';
import rules from '@/utils/validate.js';
import { getOperationPermCode } from '../utils/index.js';
import OnlineForm from '@/views/onlineForm/index.vue';
import {
  OnlineFormController
} from '@/api/onlineController.js';

const OnlineFormMixins = {
  props: {
    formId: {
      type: String,
      required: true
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    closeVisible: {
      type: String,
      default: '0'
    },
    params: {
      type: Object
    }
  },
  data () {
    return {
      isLoading: true,
      formData: {},
      rules: {},
      formConfig: {
        formType: undefined,
        formKind: undefined,
        gutter: 20,
        labelPosition: 'right',
        labelWidth: 120,
        width: undefined,
        height: undefined,
        formWidgetList: [],
        formQueryTable: undefined
      },
      masterTable: undefined,
      errorMessage: [],
      tableWidgetList: [],
      dropdownWidgetList: [],
      richEditWidgetList: [],
      datasourceMap: new Map(),
      relationMap: new Map(),
      tableMap: new Map(),
      columnMap: new Map(),
      dictMap: new Map(),
      linkageMap: new Map()
    }
  },
  methods: {
    getFormData () {
      return this.formData;
    },
    getPermCode (widget, operation) {
      return getOperationPermCode(widget, operation);
    },
    loadOnlineFormData () {
      return new Promise((resolve, reject) => {
        OnlineFormController.render(this, {
          formId: this.formId
        }).then(res => {
          let onlineForm = res.data.onlineForm;
          let formConfigData = JSON.parse(onlineForm.widgetJson);
          let formConfig = {
            formName: onlineForm.formName,
            formType: onlineForm.formType,
            formKind: onlineForm.formKind,
            masterTableId: onlineForm.masterTableId,
            labelWidth: formConfigData.formConfig.labelWidth,
            labelPosition: formConfigData.formConfig.labelPosition,
            gutter: formConfigData.formConfig.gutter,
            height: formConfigData.formConfig.height,
            width: formConfigData.formConfig.width,
            formWidgetList: formConfigData.widgetList,
            formQueryTable: formConfigData.formConfig.tableWidget
          }
          onlineForm = null;
          res.data.onlineForm = null;
          // 字典
          if (Array.isArray(res.data.onlineDictList)) {
            res.data.onlineDictList.forEach(dict => {
              this.dictMap.set(dict.dictId, dict);
            });
          }
          res.data.onlineDictList = null;
          // 数据表
          if (Array.isArray(res.data.onlineTableList)) {
            res.data.onlineTableList.forEach(table => {
              this.tableMap.set(table.tableId, table);
            });
          }
          res.data.onlineTableList = null;
          // 字段
          if (Array.isArray(res.data.onlineColumnList)) {
            res.data.onlineColumnList.forEach(column => {
              if (column.dictId != null) {
                column.dictInfo = this.dictMap.get(column.dictId);
              }
              let table = this.tableMap.get(column.tableId);
              if (table) {
                if (!Array.isArray(table.columnList)) table.columnList = [];
                table.columnList.push(column);
              }
              this.columnMap.set(column.columnId, column);
            });
          }
          res.data.onlineColumnList = null;
          // 虚拟字段
          if (Array.isArray(res.data.onlineVirtualColumnList)) {
            res.data.onlineVirtualColumnList.forEach(column => {
              column.columnId = column.virtualColumnId;
              column.columnComment = column.columnPrompt;
              column.columnName = column.objectFieldName;
              column.primaryKey = false;
              column.isVirtualColumn = true;
              this.columnMap.set(column.columnId, column);
            });
          }
          res.data.onlineVirtualColumnList = null;
          // 数据源
          if (Array.isArray(res.data.onlineDatasourceList)) {
            res.data.onlineDatasourceList.forEach(datasource => {
              datasource.masterTable = this.tableMap.get(datasource.masterTableId);
              if (datasource.masterTable) datasource.masterTable.datasource = datasource;
              this.datasourceMap.set(datasource.datasourceId, datasource);
            });
          }
          res.data.onlineDatasourceList = null;
          // 关联
          if (Array.isArray(res.data.onlineDatasourceRelationList)) {
            res.data.onlineDatasourceRelationList.forEach(relation => {
              let datasource = this.datasourceMap.get(relation.datasourceId);
              if (datasource) {
                if (!Array.isArray(datasource.relationList)) datasource.relationList = [];
                datasource.relationList.push(relation);
              }
              relation.masterColumn = this.columnMap.get(relation.masterColumnId);
              relation.slaveTable = this.tableMap.get(relation.slaveTableId);
              if (relation.slaveTable) {
                relation.slaveTable.relation = relation;
                relation.slaveTable.datasource = datasource;
              }
              relation.slaveColumn = this.columnMap.get(relation.slaveColumnId);
              this.relationMap.set(relation.relationId, relation);
            });
          }
          res.data.onlineDatasourceRelationList = null;
          // 校验规则
          if (Array.isArray(res.data.onlineColumnRuleList)) {
            res.data.onlineColumnRuleList.forEach(rule => {
              let column = this.columnMap.get(rule.columnId);
              if (column) {
                if (!Array.isArray(column.ruleList)) column.ruleList = [];
                column.ruleList.push(rule);
              }
            });
          }
          res.data.onlineColumnRuleList = null;
          this.initFormData(formConfig);
          this.formConfig = formConfig;
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    initWidget (widget, formConfig) {
      if (widget != null) {
        if (widget.datasourceId) widget.datasource = this.datasourceMap.get(widget.datasourceId);
        if (widget.relationId) {
          widget.relation = this.relationMap.get(widget.relationId);
          if (widget.datasource == null && widget.relation != null) {
            widget.datasource = this.datasourceMap.get(widget.relation.datasourceId);
          }
        }
        if (widget.datasource == null) {
          widget.datasource = {
            masterTable: {}
          }
          widget.column = {}
        }
        if (widget.tableId) widget.table = this.tableMap.get(widget.tableId);
        if (widget.columnId) widget.column = this.columnMap.get(widget.columnId);
        if (widget.widgetType === this.SysCustomWidgetType.RichEditor) {
          this.richEditWidgetList.push(widget);
        }

        // 初始化组件下拉字典参数
        if (Array.isArray(widget.dictParamList)) {
          widget.dictParamList.forEach(param => {
            if (param.dictValueType === this.SysOnlineParamValueType.STATIC_DICT) {
              let errorItem = null;
              if (Array.isArray(param.dictValue) && param.dictValue.length === 2) {
                let staticDict = StaticDict[param.dictValue[0]];
                if (staticDict == null) {
                  errorItem = {
                    widget: widget,
                    message: '组件字典参数' + param.dictParamName + '绑定的静态字典 [' + param.dictValue[0] + '] 并不存在！'
                  }
                } else {
                  if (staticDict.getValue(param.dictValue[1]) == null) {
                    errorItem = {
                      widget: widget,
                      message: '组件字典参数' + param.dictParamName + '绑定的静态字典值并不属于静态字段 [' + param.dictValue[0] + '] ！'
                    }
                  }
                }
              } else {
                errorItem = {
                  widget: widget,
                  message: '组件字典参数' + param.dictParamName + '绑定的静态字典错误！'
                }
              }
              if (errorItem != null) this.errorMessage.push(errorItem);
            }
          });
        }
        if (widget.column && widget.column.dictInfo != null) {
          this.dropdownWidgetList.push(widget);
        }
        // 初始化表格列
        if (widget.widgetType === this.SysCustomWidgetType.Table) {
          // 寻找表格主键
          widget.primaryColumnName = undefined;
          if (widget.table && Array.isArray(widget.table.columnList)) {
            for (let i = 0; i < widget.table.columnList.length; i++) {
              if (widget.table.columnList[i].primaryKey) {
                widget.primaryColumnName = widget.table.columnList[i].columnName;
                break;
              }
            }
          }
          if (Array.isArray(widget.tableColumnList)) {
            widget.tableColumnList.forEach(tableColumn => {
              tableColumn.table = this.tableMap.get(tableColumn.tableId);
              tableColumn.column = this.columnMap.get(tableColumn.columnId);
              tableColumn.relation = this.relationMap.get(tableColumn.relationId);
              if (tableColumn.table == null || tableColumn.column == null) {
                this.errorMessage.push({
                  widget: widget,
                  message: '表格列 [' + tableColumn.showName + '] 绑定的字段不存在！'
                });
              }
            });
          }
          if (Array.isArray(widget.queryParamList)) {
            widget.queryParamList.forEach(param => {
              param.table = this.tableMap.get(param.tableId);
              param.column = this.columnMap.get(param.columnId);
              param.relation = this.relationMap.get(param.relationId);
  
              if (param.table == null || param.column == null) {
                this.errorMessage.push({
                  widget: widget,
                  message: '表格查询参数不存在！'
                });
              }
            });
          }

          this.tableWidgetList.push(widget);
        }

        while (widget.datasourceId != null || widget.relationId != null) {
          if (widget.datasourceId == null && widget.relation == null) {
            let errorItem = {
              widget: widget,
              message: '组件绑定字段所属' + (widget.datasourceId ? '数据源' : '关联') + '不存在！'
            }
            this.errorMessage.push(errorItem);
            break;
          }
          if (widget.table == null) {
            let errorItem = {
              widget: widget,
              messagre: '组件绑定字段所属数据表不存在！'
            }
            this.errorMessage.push(errorItem);
            break;
          }
          if (widget.column == null && widget.columnId != null) {
            let errorItem = {
              widget: widget,
              messagre: '组件绑定字段不存在！'
            }
            this.errorMessage.push(errorItem);
            break;
          }
          if (widget.column) {
            let table = this.tableMap.get(widget.tableId);
            if (table.tableId !== widget.table.tableId) {
              let errorItem = {
                widget: widget,
                messagre: '组件绑定字段不属于选张的数据表！'
              }
              this.errorMessage.push(errorItem);
              break;
            }
          }
          break;
        }

        if (Array.isArray(widget.childWidgetList)) {
          widget.childWidgetList.forEach(subWidget => {
            if (formConfig.formType === this.SysOnlineFormType.FLOW && this.formReadOnly) {
              subWidget.readOnly = true;
            }
            this.initWidget(subWidget, formConfig);
          })
        }

        if (widget.column && widget.column.dictInfo) {
          if (Array.isArray(widget.dictParamList)) {
            widget.dictParamList.forEach(dictParam => {
              if (dictParam.dictValueType === this.SysOnlineParamValueType.TABLE_COLUMN) {
                let linkageItem = this.linkageMap.get(dictParam.dictValue);
                if (linkageItem == null) {
                  linkageItem = [];
                  this.linkageMap.set(dictParam.dictValue, linkageItem);
                }
                linkageItem.push(widget);
              }
            });
          }
        }
      }
    },
    initFormWidgetList (formConfig) {
      this.errorMessage = [];
      if (Array.isArray(formConfig.formWidgetList)) {
        formConfig.formWidgetList.forEach(widget => {
          if (formConfig.formType === this.SysOnlineFormType.FLOW && this.formReadOnly) {
            widget.readOnly = true;
          }
          this.initWidget(widget, formConfig);
        });
      }
      if (this.errorMessage.length > 0) {
        console.error(this.errorMessage);
      }
    },
    buildRuleItem (widget, rule) {
      if (rule.propDataJson) rule.data = JSON.parse(rule.propDataJson);
      if (widget != null && rule != null) {
        switch (rule.onlineRule.ruleType) {
          case this.SysOnlineRuleType.INTEGER_ONLY:
            return { type: 'integer', message: rule.data.message, trigger: 'blur', transform: (value) => Number(value) };
          case this.SysOnlineRuleType.DIGITAL_ONLY:
            return { type: 'number', message: rule.data.message, trigger: 'blur', transform: (value) => Number(value) };
          case this.SysOnlineRuleType.LETTER_ONLY:
            return { type: 'string', pattern: rules.pattern.english, message: rule.data.message, trigger: 'blur' };
          case this.SysOnlineRuleType.EMAIL:
            return { type: 'email', message: rule.data.message, trigger: 'blur' };
          case this.SysOnlineRuleType.MOBILE:
            return { type: 'string', pattern: rules.pattern.mobie, message: rule.data.message, trigger: 'blur' };
          case this.SysOnlineRuleType.RANGE:
            if (widget.column) {
              let isNumber = ['Boolean', 'Date', 'String'].indexOf(widget.column.objectFieldType) === -1;
              return { type: isNumber ? 'number' : 'string', min: rule.data.min, max: rule.data.max, message: rule.data.message, trigger: 'blur' };
            }
            break;
          case this.SysOnlineRuleType.CUSTOM:
            return { type: 'string', pattern: new RegExp(rule.data.pattern), message: rule.data.message, trigger: 'blur' };
        }
      }
    },
    buildWidgetRule (widget, rules) {
      if (widget != null && widget.column != null) {
        let widgetRuleKey = (widget.relation ? widget.relation.variableName + '__' : '') + widget.column.columnName;
        // 必填字段以及设置了验证规则的字段
        if (!widget.column.nullable || Array.isArray(widget.column.ruleList)) {
          rules[widgetRuleKey] = [];
          // 必填验证
          if (!widget.column.nullable) {
            rules[widgetRuleKey].push(
              { required: true, message: widget.showName + '不能为空！', trigger: 'true' }
            )
          }
          // 其他验证
          if (Array.isArray(widget.column.ruleList)) {
            widget.column.ruleList.forEach(rule => {
              let ruleItem = this.buildRuleItem(widget, rule);
              if (ruleItem) rules[widgetRuleKey].push(ruleItem);
            });
          }
        }
      }
    },
    initWidgetRule (formConfig) {
      if (Array.isArray(formConfig.formWidgetList)) {
        let rules = {};
        formConfig.formWidgetList.forEach(widget => {
          this.buildWidgetRule(widget, rules);
        });
        this.$set(this, 'rules', rules);
        this.$nextTick(() => {
          if (this.$refs.form) this.$refs.form.clearValidate();
        });
      }
    },
    initFormDatasourceData (formConfig) {
      let that = this;
      function addFormDataByColumn (retObj, column, relation) {
        let fieldName = (relation ? relation.variableName + '__' : '') + column.columnName;
        if (retObj == null) retObj = {};
        if (formConfig.formType === that.SysOnlineFormType.QUERY) {
          if (retObj.formFilter == null) retObj.formFilter = {};
          if (retObj.formFilterCopy == null) retObj.formFilterCopy = {};
          retObj.formFilter[fieldName] = column.objectFieldType === 'Boolean' ? false : undefined;
          retObj.formFilterCopy[fieldName] = column.objectFieldType === 'Boolean' ? false : undefined;
        } else {
          retObj[fieldName] = column.objectFieldType === 'Boolean' ? false : undefined;
        }

        return retObj;
      }
      // 设置数据源数据
      let datasourceFormData = {};
      if (this.masterTable) {
        // 添加表单主表的数据
        this.masterTable.columnList.forEach(column => {
          datasourceFormData = addFormDataByColumn(datasourceFormData, column, this.masterTable.relation);
        });
        // 如果表单主表是数据源主表，添加关联数据
        if (this.masterTable.relation == null) {
          let datasource = this.masterTable.datasource;
          if (datasource != null && Array.isArray(datasource.relationList)) {
            datasource.relationList.forEach(relation => {
              // 一对一关联从表数据
              if (relation.relationType === this.SysOnlineRelationType.ONE_TO_ONE) {
                let slaveTable = this.tableMap.get(relation.slaveTableId);
                if (slaveTable && Array.isArray(slaveTable.columnList)) {
                  slaveTable.columnList.forEach(column => {
                    datasourceFormData = addFormDataByColumn(datasourceFormData, column, relation);
                  });
                }
              }
            });
          }
        }
      }
      this.$set(this, 'formData', datasourceFormData);
    },
    initWidgetLinkage (formConfig) {
      this.linkageMap.forEach((widgetList, key) => {
        let column = this.columnMap.get(key);
        let watchKey = null;
        if (formConfig.formType === this.SysOnlineFormType.QUERY) {
          watchKey = 'formData.formFilter.' + column.columnName;
        } else {
          watchKey = 'formData.' + column.columnName;
        }
        this.$watch(watchKey, (newValue) => {
          if (Array.isArray(widgetList)) {
            widgetList.forEach(widget => {
              if (Array.isArray(this.$refs[widget.variableName])) {
                this.$refs[widget.variableName].forEach(ref => {
                  ref.reset();
                });
              } else {
                this.$refs[widget.variableName].reset();
              }
            });
          }
        });
      });
    },
    initFormData (formConfig) {
      this.masterTable = this.tableMap.get(formConfig.masterTableId);
      // 初始化表单数据
      this.initFormDatasourceData(formConfig);
      // 初始化表单组件
      this.initWidget(formConfig.formQueryTable, formConfig);
      this.initFormWidgetList(formConfig);
      // 初始化校验信息
      this.initWidgetRule(formConfig);
    },
    getParamValue (valueType, valueData) {
      switch (valueType) {
        case this.SysOnlineParamValueType.FORM_PARAM:
          return this.params ? this.params[valueData] : undefined;
        case this.SysOnlineParamValueType.TABLE_COLUMN:
        {
          let column = this.columnMap.get(valueData);
          let columnValue = null;
          if (this.formConfig.formType === this.SysOnlineFormType.QUERY) {
            columnValue = this.formData.formFilterCopy[column.columnName];
          } else {
            columnValue = this.formData[column.columnName];
          }
          if (column == null || columnValue == null || columnValue === '') {
            return null;
          } else {
            return columnValue;
          }
        }
        case this.SysOnlineParamValueType.STATIC_DICT:
          return Array.isArray(valueData) ? valueData[1] : undefined;
        case this.SysOnlineParamValueType.INPUT_VALUE:
          return valueData;
      }
    },
    getParamValueObj (paramName, valueType, valueData, retObj) {
      try {
        if (retObj == null) retObj = {};
        retObj[paramName] = this.getParamValue(valueType, valueData);
        if (retObj[paramName] == null) return null;
        return retObj;
      } catch (e) {
        console.log(e);
      }
    },
    clean () {
      this.datasourceMap = null;
      this.relationMap = null;
      this.tableMap = null;
      this.columnMap = null;
      this.dictMap = null;
    },
    loadAllDropdownData () {
      if (Array.isArray(this.dropdownWidgetList)) {
        this.dropdownWidgetList.forEach(dropdownWidget => {
          let dropdownWidgetImpl = this.$refs[dropdownWidget.variableName][0];
          if (dropdownWidgetImpl) {
            dropdownWidgetImpl.onVisibleChange();
          }
        });
      }
    },
    reload () {
      this.loadOnlineFormData().then(res => {
        this.isLoading = false;
        if (this.formConfig.formType === this.SysOnlineFormType.FORM) {
          if (Number.parseInt(this.operationType) === this.SysCustomWidgetOperationType.EDIT && this.saveOnClose === '1') {
            // 编辑操作页面，初始化页面数据
            let httpCall = null;
            if (this.saveOnClose === '0') {
              httpCall = Promise.resolve({
                data: this.params
              });
            } else {
              let params = {
                datasourceId: (this.masterTable.datasource || {}).datasourceId,
                relationId: (this.masterTable.relation || {}).relationId
              }
              
              for (let i = 0; i < this.masterTable.columnList.length; i++) {
                let column = this.masterTable.columnList[i];
                if (column.primaryKey) {
                  params.dataId = this.params[column.columnName];
                  break;
                }
              }
              if (params.relationId) {
                httpCall = this.doUrl('/admin/online/onlineOperation/viewByOneToManyRelationId/' + (this.masterTable.datasource || {}).variableName, 'get', params);
              } else {
                httpCall = this.doUrl('/admin/online/onlineOperation/viewByDatasourceId/' + (this.masterTable.datasource || {}).variableName, 'get', params);
              }
            }

            httpCall.then(res => {
              this.formData = {
                ...this.formData,
                ...res.data
              }
              this.loadAllDropdownData();
              // 初始化组件联动
              this.initWidgetLinkage(this.formConfig);
            }).catch(e => {});
            return;
          } else {
            if (this.rowData != null) {
              this.formData = {
                ...this.rowData
              }
            }
          }
          this.loadAllDropdownData();
        }
        setTimeout(() => {
          if (this.formConfig.formType === this.SysOnlineFormType.FLOW) {
            this.loadAllDropdownData();
          }
          // 初始化组件联动
          this.initWidgetLinkage(this.formConfig);
          this.onResume();
          this.$emit('ready');
        }, 30);
      }).catch(e => {
        console.log(e);
      });
    },
    onResume () {},
    getPrimaryKeyColumnParam (table, row) {
      if (table && Array.isArray(table.columnList)) {
        return table.columnList.reduce((retObj, column) => {
          let fieldName = (table.relation ? table.relation.variableName + '__' : '') + column.columnName;
          if (column.primaryKey) {
            retObj[column.columnName] = row ? row[fieldName] : undefined;
          }
          return retObj;
        }, {});
      }

      return null;
    },
    buildSubFormParams (operation, subFormInfo, row) {
      let subFormMasterTable = this.tableMap.get(subFormInfo.masterTableId);
      if (subFormMasterTable == null) return null;
      if (subFormMasterTable.relation == null) {
        // 下级表单操作的是主表数据的编辑
        if (operation.type === this.SysCustomWidgetOperationType.EDIT) {
          return this.getPrimaryKeyColumnParam(this.masterTable, row);
        } else {
          return null;
        }
      } else {
        // 下级表单操作的是从表
        if (subFormInfo.formType === this.SysOnlineFormType.QUERY) {
          // 从表的查询页面，参数为主表主键
          return this.getPrimaryKeyColumnParam(this.masterTable, row);
        } else {
          if (operation.type === this.SysCustomWidgetOperationType.EDIT) {
            // 从表的编辑页面
            if (this.formConfig.formType === this.SysOnlineFormType.FORM &&
              Number.parseInt(this.operationType) === this.SysCustomWidgetOperationType.ADD) {
              return {
                ...row
              }
            } else {
              return this.getPrimaryKeyColumnParam(subFormMasterTable, row);
            }
          } else {
            // 从表的添加页面
            return {
              ...this.params
            }
          }
        }
      }
    },
    handlerOperation (operation, row, widget) {
      if (this.preview()) return;
      if (operation.formId != null) {
        OnlineFormController.view(this, {
          formId: operation.formId
        }).then(res => {
          let formInfo = res.data;
          if (formInfo != null) {
            let params = this.buildSubFormParams(operation, formInfo, row);
            if (formInfo.formKind === this.SysOnlineFormKind.DIALOG) {
              let formJsonData = JSON.parse(formInfo.widgetJson);
              let area = (formInfo.height != null) ? [(formJsonData.formConfig.width || 800) + 'px', formJsonData.formConfig.height + 'px'] : (formJsonData.formConfig.width || 800) + 'px';
              this.$dialog.show(operation.name, OnlineForm, {
                area: area,
                offset: '100px'
              }, {
                flowData: this.flowData,
                formId: formInfo.formId,
                formType: formInfo.formType,
                operationType: operation.type,
                params,
                saveOnClose: (
                  formInfo.formType === this.SysOnlineFormType.FLOW || this.formConfig.formType === this.SysOnlineFormType.FLOW ||
                  (
                    formInfo.formType === this.SysOnlineFormType.FORM &&
                    Number.parseInt(this.operationType) === this.SysCustomWidgetOperationType.ADD
                  )
                ) ? '0' : '1',
                rowData: row
              }).then(res => {
                let widgetObj = this.$refs[widget.variableName];
                if (Array.isArray(widgetObj)) {
                  widgetObj.forEach(item => {
                    item.refresh(res, operation.type);
                  });
                } else {
                  widgetObj.refresh(res, operation.type);
                }
              }).catch(e => {
              });
            } else {
              if (this.formConfig.formType === this.SysOnlineFormType.QUERY) {
                let tableWidget = this.$refs[this.formConfig.formQueryTable.variableName].getTableWidget();
                this.addOnlineFormCache({
                  key: this.$route.fullPath,
                  value: {
                    formFilter: { ...this.formData.formFilter },
                    formFilterCopy: { ...this.formData.formFilterCopy },
                    tableImpl: {
                      totalCount: tableWidget.totalCount,
                      currentPage: tableWidget.currentPage,
                      pageSize: tableWidget.pageSize
                    }
                  }
                });
              }
              this.$router.push({
                name: 'onlineForm',
                query: {
                  flowData: this.flowData,
                  formId: formInfo.formId,
                  formType: formInfo.formType,
                  closeVisible: '1',
                  operationType: operation.type,
                  params,
                  saveOnClose: (
                    formInfo.formType === this.SysOnlineFormType.FLOW || this.formConfig.formType === this.SysOnlineFormType.FLOW ||
                    (
                      formInfo.formType === this.SysOnlineFormType.FORM &&
                      Number.parseInt(this.operationType) === this.SysCustomWidgetOperationType.ADD
                    )
                  ) ? '0' : '1',
                  rowData: row
                }
              })
            }
          }
        }).catch(e => {});
      } else {
        if (operation.type === this.SysCustomWidgetOperationType.DELETE) {
          this.$confirm('是否删除当前数据？').then(res => {
            if (this.formConfig.formType !== this.SysOnlineFormType.FLOW &&
              (this.formConfig.formType !== this.SysOnlineFormType.FORM || Number.parseInt(this.operationType) !== this.SysCustomWidgetOperationType.ADD)) {
              let params = {
                datasourceId: (widget.table.datasource || {}).datasourceId,
                relationId: (widget.table.relation || {}).relationId
              }
              for (let i = 0; i < widget.table.columnList.length; i++) {
                let column = widget.table.columnList[i];
                if (column.primaryKey) {
                  let fieldName = (widget.table.relation ? widget.table.relation.variableName + '__' : '') + column.columnName;
                  params.dataId = row[fieldName];
                  break;
                }
              }
              let httpCall = null;
              if (params.relationId) {
                httpCall = this.doUrl('/admin/online/onlineOperation/deleteOneToManyRelation/' + widget.datasource.variableName, 'post', params);
              } else {
                httpCall = this.doUrl('/admin/online/onlineOperation/deleteDatasource/' + widget.datasource.variableName, 'post', params);
              }

              httpCall.then(res => {
                this.$message.success('删除成功！');
                let widgetObj = this.$refs[widget.variableName];
                if (Array.isArray(widgetObj)) {
                  widgetObj.forEach(item => {
                    item.refresh(res, operation.type);
                  });
                } else {
                  widgetObj.refresh(res, operation.type);
                }
              }).catch(e => {
              });
            } else {
              let widgetObj = this.$refs[widget.variableName];
              if (Array.isArray(widgetObj)) {
                widgetObj.forEach(item => {
                  item.refresh(row, operation.type);
                });
              } else {
                widgetObj.refresh(row, operation.type);
              }
            }
          }).catch(e => {});
        } else if (operation.type === this.SysCustomWidgetOperationType.EXPORT) {
          this.$message.warning('导出操作尚未支持！');
        }
      }
    },
    getRelationTableData (tableWidget) {
      if (tableWidget.widgetType === this.SysCustomWidgetType.Table) {
        let table = tableWidget.table;
        let temp = this.$refs[tableWidget.variableName];
        if (table != null && table.relation != null && Array.isArray(table.columnList) && temp != null) {
          let tableWidgetImpl = temp[0] || temp;
          return tableWidgetImpl.getTableWidget().dataList.map(data => {
            return table.columnList.reduce((retObj, column) => {
              let fieldName = (table.relation ? table.relation.variableName + '__' : '') + column.columnName;
              retObj[column.columnName] = data[fieldName];
              return retObj
            }, {});
          });
        }
      }
      return null;
    },
    getWidgetPrimaryColumnId (widget) {
      console.log(widget);
      let columnList = null;
      if (widget.relationId == null) {
        columnList = widget.datasource.masterTable.columnList;
      } else {
        columnList = widget.relation.slaveTable.columnList;
      }

      if (Array.isArray(columnList)) {
        for (let i = 0; i < columnList.length; i++) {
          let column = columnList[i];
          if (column.primaryKey) {
            let columnName = column.columnName;
            if (widget.relation != null) columnName = widget.relation.variableName + '__' + columnName;
            console.log(this.formData, columnName);
            return this.formData[columnName];
          }
        }
      }
    },
    ...mapMutations(['addOnlineFormCache'])
  },
  created () {
    this.reload();
  },
  destoryed () {
    this.clean();
  },
  watch: {
    formId: {
      handler (newValue) {
        this.reload();
      }
    }
  }
}

export {
  OnlineFormMixins
}
