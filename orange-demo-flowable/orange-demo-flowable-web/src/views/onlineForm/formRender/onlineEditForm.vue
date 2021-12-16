<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-scrollbar class="custom-scroll" :style="{height: formConfig.height + 'px', 'min-height': '100px'}">
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
              :isNew="isRelationTableAdd(widget)"
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
                  :isNew="isRelationTableAdd(subWidget)"
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
    </el-scrollbar>
    <el-row v-if="formConfig.formType === SysOnlineFormType.FORM" type="flex" justify="end" style="margin-top: 15px;">
      <el-button type="primary" size="mini" :plain="true"
        v-if="formConfig.formKind === SysOnlineFormKind.DIALOG"
        @click="onCancel(false)">
        取消
      </el-button>
      <el-button type="primary" size="mini"
        @click="onSave()">
        保存
      </el-button>
    </el-row>
  </div>
</template>

<script>
import { OnlineFormMixins } from './onlineFormMixins.js';
import CustomUpload from '@/views/onlineForm/components/customUpload.vue';
import CustomWidget from '@/views/onlineForm/components/customWidget.vue';
import CustomTable from '@/views/onlineForm/components/customTable.vue';

export default {
  name: 'onlineEditForm',
  inject: ['preview'],
  mixins: [OnlineFormMixins],
  props: {
    operationType: {
      type: [Number, String],
      required: true
    },
    saveOnClose: {
      type: String,
      default: '1'
    },
    rowData: {
      type: Object
    },
    flowData: {
      type: Object
    }
  },
  components: {
    CustomWidget,
    CustomTable,
    CustomUpload
  },
  methods: {
    getWidgetFieldName (widget) {
      if (widget && widget.relation == null) {
        return (widget.column || {}).columnName;
      } else {
        return widget.relation.variableName + '__' + (widget.column || {}).columnName
      }
    },
    // 当前表格是否为新建从表数据
    isRelationTableAdd (tableWidget) {
      let temp = Number.parseInt(this.operationType) === this.SysCustomWidgetOperationType.ADD && tableWidget.relation != null;
      return temp;
    },
    onCancel (isSuccess, data) {
      this.$emit('close', isSuccess, data);
    },
    onSave () {
      if (this.preview()) return;
      
      setTimeout(() => {
        this.$refs.form.validate(valid => {
          if (!valid) return;
          let datasourceId = (this.masterTable.datasource || {}).datasourceId;
          let relationId = (this.masterTable.relation || {}).relationId;
          let params = {
            datasourceId,
            relationId,
            masterData: relationId == null ? this.formData : undefined
          }
          // 添加调用按钮接口代码
          if (this.saveOnClose === '1') {
            if (datasourceId != null && datasourceId !== '') {
              // 从表数据
              let slaveData = {};
              if (relationId == null) {
                if (Array.isArray(this.tableWidgetList) && this.tableWidgetList.length > 0 && Number.parseInt(this.operationType) === this.SysCustomWidgetOperationType.ADD) {
                  this.tableWidgetList.forEach(tableWidget => {
                    let tableData = this.getRelationTableData(tableWidget);
                    if (tableData != null) {
                      slaveData[tableWidget.relation.relationId] = tableData;
                    }
                  });
                }
              } else {
                slaveData = this.masterTable.columnList.reduce((retObj, column) => {
                  let fieldName = this.masterTable.relation.variableName + '__' + column.columnName;
                  retObj[column.columnName] = this.formData[fieldName];
                  return retObj;
                }, {});
                slaveData = {
                  ...slaveData,
                  ...this.params
                }
              }
              params.slaveData = slaveData;
              // params.slaveData.video_course_id = '1037103328223401584';
              let httpCall = null;
              if (Number.parseInt(this.operationType) === this.SysCustomWidgetOperationType.ADD) {
                if (relationId == null) {
                  httpCall = this.doUrl('/admin/online/onlineOperation/addDatasource/' + (this.masterTable.datasource || {}).variableName, 'post', params);
                } else {
                  httpCall = this.doUrl('/admin/online/onlineOperation/addOneToManyRelation/' + (this.masterTable.datasource || {}).variableName, 'post', params);
                }
              } else {
                if (relationId == null) {
                  httpCall = this.doUrl('/admin/online/onlineOperation/updateDatasource/' + (this.masterTable.datasource || {}).variableName, 'post', params);
                } else {
                  httpCall = this.doUrl('/admin/online/onlineOperation/updateOneToManyRelation/' + (this.masterTable.datasource || {}).variableName, 'post', params);
                }
              }
              httpCall.then(res => {
                this.onCancel(true, this.formData);
              }).catch(e => {});
            }
          } else {
            let masterData = {
              // 级联添加数据唯一标识
              __cascade_add_id__: this.formData.__cascade_add_id__ || new Date().getTime()
            };
            if (this.masterTable && Array.isArray(this.masterTable.columnList)) {
              this.masterTable.columnList.forEach(column => {
                let keyName = column.columnName;
                if (this.masterTable.relation) keyName = this.masterTable.relation.variableName + '__' + keyName;
                masterData[keyName] = this.formData[keyName];
              });

              if (Array.isArray(this.dropdownWidgetList)) {
                this.dropdownWidgetList.forEach(dropdwonWidget => {
                  let keyName = dropdwonWidget.column.columnName;
                  if (dropdwonWidget.table.relation) keyName = dropdwonWidget.table.relation.variableName + '__' + keyName;
                  let tempWidget = this.$refs[dropdwonWidget.variableName];
                  if (Array.isArray(tempWidget)) {
                    tempWidget.forEach(item => {
                      masterData[keyName + '__DictMap'] = {
                        id: this.formData[keyName],
                        name: item.getDictValue(this.formData[keyName])
                      }
                    });
                  }
                });
              }
            }
            this.onCancel(true, masterData);
          }
        });
      }, 30);
    },
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
            temp.columnValueStart = paramValue[0];
            temp.columnValueEnd = paramValue[1];
          }
          return temp;
        }).filter(item => item != null);
      }

      return queryParams;
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
    onTableOperationClick (operation, row, widget) {
      this.handlerOperation(operation, row, widget);
    }
  },
  mounted () {
  }
}
</script>

<style>
</style>
