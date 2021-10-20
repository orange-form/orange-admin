<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="数据表" prop="tableId">
            <el-select class="input-item" v-model="formData.tableId"
              :clearable="true" filterable placeholder="数据表"
            >
              <el-option v-for="item in tableList" :key="item.tableId" :value="item.tableId" :label="item.tableName" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="过滤字段" prop="columnId">
            <el-select class="input-item" v-model="formData.columnId"
              :clearable="true" filterable placeholder="过滤字段"
            >
              <el-option v-for="item in columnList" :key="item.columnId" :value="item.columnId" :label="item.columnName" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="过滤类型" prop="operatorType">
            <el-select class="input-item" v-model="formData.operatorType"
              :clearable="true" filterable placeholder="过滤类型"
              @change="onOperationTypeChange"
            >
              <el-option v-for="item in SysOnlineFilterOperationType.getList()" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="过滤参数值" prop="value">
            <el-select v-if="dictValueList != null" v-model="formData.value" :disabled="!valueEnabled" placeholder="">
              <el-option v-for="item in dictValueList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
            <template v-else>
              <el-switch v-if="filterColumn != null && filterColumn.objectFieldType === 'Boolean'" :disabled="!valueEnabled" v-model="formData.value" />
              <el-input-number v-if="filterColumn != null && ['Integer', 'Long', 'BigDecimal', 'Double'].indexOf(filterColumn.objectFieldType) !== -1"
                :disabled="!valueEnabled" v-model="formData.value" :controls="false"
              />
              <el-input v-else :disabled="filterColumn == null || !valueEnabled" v-model="formData.value" clearable />
            </template>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini"
              @click="onSubmit()">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { findItemFromList } from '@/utils';
import { DropdownWidget } from '@/utils/widget.js';
import { OnlineColumnController } from '@/api/onlineController.js';
import { getDictDataList } from '../utils';

export default {
  props: {
    tableList: {
      type: Array,
      required: true
    },
    rowData: {
      type: Object
    }
  },
  data () {
    return {
      formData: {
        tableId: undefined,
        columnId: undefined,
        operatorType: this.SysOnlineFilterOperationType.EQUAL,
        value: undefined,
        dictValueName: undefined
      },
      columnList: [],
      dictValueList: [],
      operatorTypeWidget: new DropdownWidget(this.loadColumnIdDropdownList),
      rules: {}
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        let temp = findItemFromList(this.dictValueList, this.formData.value, 'id');
        this.formData.dictValueName = temp ? temp.name : undefined;
        this.observer.cancel(isSuccess, {
          ...this.formData,
          table: this.filterTable,
          column: this.filterColumn
        });
      }
    },
    onSubmit () {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        this.onCancel(true);
      });
    },
    loadOnlineTableColumnList (tableId) {
      if (tableId == null || tableId === '') return;

      let params = {
        onlineColumnDtoFilter: {
          tableId
        }
      }

      OnlineColumnController.list(this, params).then(res => {
        this.filterTable.columnList = res.data.dataList;
        this.columnList = res.data.dataList;
      }).catch(e => {});
    },
    onOperationTypeChange (value) {
      if ([this.SysOnlineFilterOperationType.NOT_NULL || this.SysOnlineFilterOperationType.IS_NULL].indexOf(value) !== -1 || value == null) {
        this.formData.value = undefined;
      }
    },
    getDictValue (dictInfo) {
      if (dictInfo == null) return;
      getDictDataList(this, dictInfo).then(res => {
        this.dictValueList = res;
      }).catch(e => {});
    }
  },
  computed: {
    valueEnabled () {
      let temp = [
        this.SysOnlineFilterOperationType.NOT_NULL,
        this.SysOnlineFilterOperationType.IS_NULL
      ];
      return temp.indexOf(this.formData.operatorType) === -1 && this.formData.operatorType != null;
    },
    filterTable () {
      return findItemFromList(this.tableList, this.formData.tableId, 'tableId');
    },
    filterColumn () {
      return findItemFromList(this.columnList, this.formData.columnId, 'columnId');
    }
  },
  mounted () {
    if (this.rowData) {
      this.formData = {
        ...this.rowData
      }
    }
  },
  watch: {
    filterTable: {
      handler (table) {
        this.columnList = [];
        if (table != null) {
          if (Array.isArray(table.columnList)) {
            this.columnList = table.columnList;
          } else {
            this.loadOnlineTableColumnList(table.tableId);
          }
        }
      }
    },
    filterColumn: {
      handler (column) {
        this.dictValueList = null;
        this.formData.dictValueName = undefined;
        if (column != null) {
          if (column.dictInfo != null) {
            this.getDictValue(column.dictInfo);
          }
        } else {
          this.formData.columnId = undefined;
        }
      }
    }
  }
}
</script>

<style>
</style>
