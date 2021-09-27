<template>
  <div style="position: relative;">
    <el-form :label-width="formConfig.labelWidth + 'px'" size="mini" :label-position="formConfig.labelPosition" @submit.native.prevent>
      <filter-box :item-width="formConfig.labelWidth + 250" v-if="!isLoading">
        <CustomFilterWidget v-for="widget in formConfig.formWidgetList" :key="widget.id"
          :ref="widget.variableName"
          :widgetConfig="widget" :formConfig="formConfig"
          :getDropdownParams="getDropdownParams"
          v-model="formData.formFilter[widget.column.columnName]"
        />
        <el-button v-if="Array.isArray(formConfig.formWidgetList) && formConfig.formWidgetList.length > 0"
          slot="operator" type="primary" :plain="true" size="mini" @click="onSearch">
          查询
        </el-button>
        <el-button v-for="operation in getTableOperation(false)" :key="operation.id"
          slot="operator" size="mini"
          :plain="operation.plain"
          :type="operation.btnType"
          :disabled="!checkPermCodeExist(getPermCode(formConfig.formQueryTable, operation))"
          @click.stop="onTableOperationClick(operation, null, formConfig.formQueryTable)">
          {{operation.name}}
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24" v-if="formConfig.formQueryTable">
        <CustomTable :ref="formConfig.formQueryTable.variableName" :widgetConfig="formConfig.formQueryTable" :formType="formConfig.formType"
          :getTableQueryParams="getTableQueryParams"
          @operationClick="onTableOperationClick" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex';
import { OnlineFormMixins } from './onlineFormMixins.js';
import CustomFilterWidget from '@/views/onlineForm/components/customFilterWidget.vue';
import CustomTable from '@/views/onlineForm/components/customTable.vue';

export default {
  name: 'onlineQueryForm',
  mixins: [OnlineFormMixins],
  props: {
    formId: {
      type: String,
      required: true
    },
    closeVisible: {
      type: String,
      default: '0'
    }
  },
  components: {
    CustomFilterWidget,
    CustomTable
  },
  inject: ['preview'],
  methods: {
    getTableOperation (rowOperation) {
      if (this.formConfig.formQueryTable == null || !Array.isArray(this.formConfig.formQueryTable.operationList)) return [];
      return this.formConfig.formQueryTable.operationList.filter(operation => {
        return operation.rowOperation === rowOperation && operation.enabled;
      });
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
    onSearch () {
      this.formData.formFilterCopy = {
        ...this.formData.formFilter
      }
      this.$refs[this.formConfig.formQueryTable.variableName].refresh();
    },
    getDropdownParams (widget) {
      if (Array.isArray(widget.dictParamList)) {
        let params = {};
        for (let i = 0; i < widget.dictParamList.length; i++) {
          let dictParam = widget.dictParamList[i];
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
    ...mapMutations(['removeOnlineFormCache'])
  },
  computed: {
    ...mapGetters(['getOnlineFormCache'])
  }
}
</script>

<style>
</style>
