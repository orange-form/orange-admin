<template>
  <div class="form-single-fragment">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules"
      :label-width="formConfig.labelWidth + 'px'" size="mini"
      :label-position="formConfig.labelPosition"
      @submit.native.prevent>
      <el-row :gutter="formConfig.gutter">
        <template v-for="widget in formConfig.formWidgetList">
          <CustomTable :ref="widget.variableName"
            v-if="widget.widgetType === SysCustomWidgetType.Table" :key="widget.id"
            :widgetConfig="widget"
            :formType="formConfig.formType"
            :primaryColumnName="widget.primaryColumnName"
            :flowData="flowData"
            :isNew="true"
            :getTableQueryParams="getTableQueryParams"
            @operationClick="onTableOperationClick"
          />
          <CustomUpload :ref="widget.variableName" :key="widget.id"
            v-else-if="widget.widgetType === SysCustomWidgetType.Upload"
            :widgetConfig="widget"
            :flowData="flowData"
            :getDataId="getWidgetPrimaryColumnId"
            v-model="formData[getWidgetFieldName(widget)]" />
          <CustomWidget
            v-else
            :ref="widget.variableName"
            :key="widget.id"
            :widgetConfig="widget"
            :formConfig="formConfig"
            :getDropdownParams="getDropdownParams"
            v-model="formData[getWidgetFieldName(widget)]"
          >
            <template v-for="subWidget in widget.childWidgetList">
              <CustomTable :ref="subWidget.variableName"
                v-if="subWidget.widgetType === SysCustomWidgetType.Table" :key="subWidget.id"
                :widgetConfig="subWidget"
                :formType="formConfig.formType"
                :isNew="true"
                :getTableQueryParams="getTableQueryParams"
                @operationClick="onTableOperationClick"
              />
              <CustomUpload :ref="subWidget.variableName" :key="subWidget.id"
                v-else-if="subWidget.widgetType === SysCustomWidgetType.Upload"
                :widgetConfig="subWidget"
                :flowData="flowData"
                :getDataId="getWidgetPrimaryColumnId"
                v-model="formData[getWidgetFieldName(subWidget)]" />
              <CustomWidget
                v-else
                :ref="subWidget.variableName"
                :key="subWidget.id"
                :widgetConfig="subWidget"
                :formConfig="formConfig"
                :getDropdownParams="getDropdownParams"
                v-model="formData[getWidgetFieldName(subWidget)]"
              />
            </template>
          </CustomWidget>
        </template>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { OnlineFormMixins } from './onlineFormMixins.js';
import CustomUpload from '@/views/onlineForm/components/customUpload.vue';
import CustomWidget from '@/views/onlineForm/components/customWidget.vue';
import CustomTable from '@/views/onlineForm/components/customTable.vue';

export default {
  name: 'workflowForm',
  props: {
    readOnly: {
      type: [String, Boolean]
    }
  },
  inject: ['preview'],
  mixins: [OnlineFormMixins],
  components: {
    CustomWidget,
    CustomTable,
    CustomUpload
  },
  data () {
    return {
      flowData: {}
    }
  },
  methods: {
    getWidgetFieldName (widget) {
      if (widget && widget.relation == null) {
        return (widget.column || {}).columnName;
      } else {
        return widget.relation.variableName + '__' + (widget.column || {}).columnName
      }
    },
    getDropdownParams (widget) {
      if (Array.isArray(widget.dictParamList)) {
        let params = {};
        for (let i = 0; i < widget.dictParamList.length; i++) {
          let dictParam = widget.dictParamList[i];
          if (dictParam.dictValue == null || dictParam.dictValueType == null) continue;
          params = this.getParamValueObj(dictParam.dictParamName, dictParam.dictValueType, dictParam.dictValue, params);
          if (params == null) return null;
        }

        return params;
      } else {
        return {};
      }
    },
    getTableQueryParams (widget) {
      return {};
    },
    onTableOperationClick (operation, row, widget) {
      this.handlerOperation(operation, row, widget);
    },
    setFormData (formData, oneToManyData, flowData) {
      this.flowData = flowData;
      this.formData = {
        ...this.formData,
        ...formData
      }
      this.setOneToManyRelationData(oneToManyData);
    },
    setOneToManyRelationData (oneToManyRelationList) {
      if (oneToManyRelationList != null) {
        Object.keys(oneToManyRelationList).forEach(relationVariableName => {
          if (Array.isArray(this.tableWidgetList)) {
            for (let i = 0; i < this.tableWidgetList.length; i++) {
              let tableWidget = this.tableWidgetList[i];
              if (tableWidget.relation.variableName === relationVariableName) {
                let temp = this.$refs[tableWidget.variableName];
                let tableImpl = Array.isArray(temp) ? temp[0] : temp;
                if (tableImpl) {
                  tableImpl.getTableWidget().dataList = oneToManyRelationList[relationVariableName];
                }
                break;
              }
            }
          }
        });
      }
    },
    getVariableData (variableList) {
      let variableData;
      if (Array.isArray(variableList) && variableList.length > 0) {
        variableList.forEach(variable => {
          if (!variable.builtin) {
            let column = this.columnMap.get(variable.bindColumnId);
            let relation = this.relationMap.get(variable.bindRelationId);
            if (column != null) {
              if (variableData == null) variableData = {};
              let key = relation == null ? '' : relation.variableName + '__';
              key += column.columnName;
              variableData[variable.variableName] = this.formData[key];
            }
          }
        });
      }
      return variableData;
    },
    getFormData () {
      return new Promise((resolve, reject) => {
        this.$refs.form.validate(valid => {
          if (!valid) return reject();
          let tempObj = {};
          let that = this;
          function getFlowWidgetData (widget) {
            if (widget == null || widget.readOnly || widget.disabled) return;
            if (widget.relation == null) {
              if (tempObj.masterData == null) tempObj.masterData = {};
              tempObj.masterData[widget.column.columnName] = that.formData[that.getWidgetFieldName(widget)];
              tempObj.masterData.__maasterTable__ = widget.table;
            } else {
              if (tempObj.slaveData == null) tempObj.slaveData = {};
              if (widget.widgetType === that.SysCustomWidgetType.Table) {
                let tableData = that.getRelationTableData(widget);
                if (tableData != null) {
                  tempObj.slaveData[widget.relation.relationId] = tableData;
                }
              } else {
                tempObj.slaveData[widget.relation.relationId][widget.column.columnName] = that.formData[that.getWidgetFieldName(widget)];
                if (tempObj.slaveData[widget.relation.relationId]['__slaveWidget__'] == null) tempObj.slaveData[widget.relation.relationId]['__slaveWidget__'] = widget;
              }
            }

            if (Array.isArray(widget.childWidgetList)) {
              widget.childWidgetList.forEach(subWidget => {
                getFlowWidgetData(subWidget);
              });
            }
          }

          if (Array.isArray(this.formConfig.formWidgetList)) {
            this.formConfig.formWidgetList.forEach(widget => {
              getFlowWidgetData(widget);
            });
          }

          if (tempObj.masterData != null && tempObj.masterData.__maasterTable__ != null) {
            let primaryColumn = null;
            if (Array.isArray(tempObj.masterData.__maasterTable__.columnList)) {
              primaryColumn = tempObj.masterData.__maasterTable__.columnList.filter(column => {
                return column.primaryKey;
              })[0];
            }
            if (primaryColumn != null && this.formData[primaryColumn.columnName] != null) {
              tempObj.masterData[primaryColumn.columnName] = this.formData[primaryColumn.columnName];
            }
            delete tempObj.masterData.__maasterTable__;
          }

          if (tempObj.slaveData != null) {
            Object.keys(tempObj.slaveData).map(key => {
              let slaveObj = tempObj.slaveData[key];
              let primaryColumn = null;
              if (slaveObj != null && slaveObj.__slaveWidget__ != null && slaveObj.__slaveWidget__.table != null) {
                if (Array.isArray(slaveObj.__slaveWidget__.table.columnList)) {
                  primaryColumn = slaveObj.__slaveWidget__.table.columnList.filter(column => {
                    return column.primaryKey;
                  })[0];
                }
                let widget = slaveObj.__slaveWidget__;
                if (primaryColumn != null && this.formData[widget.relation.variableName + '__' + (widget.column || {}).columnName] != null) {
                  slaveObj[primaryColumn.columnName] = this.formData[widget.relation.variableName + '__' + (widget.column || {}).columnName]
                }
                delete slaveObj.__slaveWidget__;
              }
            });
          }

          resolve(tempObj);
        });
      });
    }
  },
  computed: {
    formReadOnly () {
      return this.readOnly === 'true' || this.readOnly;
    }
  }
}

</script>

<style scoped>
</style>
