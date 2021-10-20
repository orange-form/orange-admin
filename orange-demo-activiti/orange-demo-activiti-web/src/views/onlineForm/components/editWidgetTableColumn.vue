<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row>
        <el-col :span="24">
          <el-form-item label="字段数据表" prop="tableId">
            <el-select class="input-item" v-model="formData.tableId"
              :clearable="true" placeholder="字段数据表"
              @change="onTableChange" >
              <el-option v-for="(table, index) in tableList" :key="table.tableId"
                :label="table.tableName" :value="table.tableId">
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{table.tableName}}</span>
                  <el-tag
                    :type="table.relationType == null ? 'success' : 'parimary'"
                    style="margin-left: 30px;" size="mini" effect="dark" >
                    {{(table.relationType == null || index === 0) ? '主表' : '一对一关联'}}
                  </el-tag>
                </el-row>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="绑定列字段" prop="columnId">
            <el-select class="input-item" v-model="formData.columnId"
              :clearable="true" placeholder="字段数据表" @change="onColumnChange">
              <el-option v-for="column in getTableColumnList" :key="column.columnId"
                :label="column.columnName" :value="column.columnId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表格列名" prop="showName">
            <el-input class="input-item" v-model="formData.showName"
              :clearable="true" placeholder="表格列名" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表格列宽" prop="columnId">
            <el-input-number class="input-item" v-model="formData.columnWidth"
              placeholder="表格列宽" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="支持排序" prop="columnId">
            <el-switch class="input-item" v-model="formData.sortable" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="显示顺序" prop="showOrder">
            <el-input-number class="input-item" v-model="formData.showOrder"
              placeholder="显示顺序" />
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

export default {
  props: {
    showOrder: {
      type: Number
    },
    rowData: {
      type: Object
    },
    tableList: {
      type: Array,
      required: true
    },
    usedColumnList: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      formData: {
        columnId: undefined,
        tableId: undefined,
        showName: undefined,
        showOrder: this.showOrder,
        sortable: false,
        columnWidth: undefined
      },
      oldColumnId: undefined,
      rules: {
        showName: [
          { required: true, message: '表格列名不能为空', trigger: 'blur' }
        ],
        tableId: [
          { required: true, message: '表格列绑定数据表不能为空', trigger: 'blur' }
        ],
        columnId: [
          { required: true, message: '表格列绑定字段不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess, this.formData);
      }
    },
    onSubmit () {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        this.formData.table = this.getCurrentTable;
        this.formData.relationId = this.getCurrentTable.relationType == null ? undefined : this.getCurrentTable.id;
        this.onCancel(true);
      });
    },
    onTableChange (value) {
      this.formData.columnId = undefined;
    },
    onColumnChange (value) {
      this.formData.column = findItemFromList(this.getTableColumnList, this.formData.columnId, 'columnId');
      this.formData.showName = this.formData.column ? this.formData.column.columnComment : undefined;
    }
  },
  computed: {
    getCurrentTable () {
      return findItemFromList(this.tableList, this.formData.tableId, 'tableId');
    },
    getTableColumnList () {
      if (this.getCurrentTable != null) {
        return this.getCurrentTable.columnList.filter(item => {
          return this.usedColumnList.indexOf(item.columnId) === -1 || this.oldColumnId === item.columnId;
        });
      } else {
        return [];
      }
    }
  },
  mounted () {
    if (this.rowData != null) {
      this.formData = {
        ...this.rowData
      }
      this.oldColumnId = this.formData.columnId;
    }
  }
}
</script>

<style>
</style>
