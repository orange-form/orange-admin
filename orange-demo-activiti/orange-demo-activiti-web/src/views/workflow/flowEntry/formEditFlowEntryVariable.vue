<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24" v-if="datasource != null">
          <el-form-item label="绑定字段">
            <el-cascader class="input-item"
              v-model="bindColumnPath" filterable
              placeholder="变量绑定的数据源字段"
              :options="entryDatasourceData"
              :disabled="datasource == null"
              @change="onBindColumnChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="变量名称" prop="showName">
            <el-input class="input-item" v-model="formData.showName"
              :clearable="true" placeholder="变量名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="变量标识" prop="variableName">
            <el-input class="input-item" v-model="formData.variableName"
              :clearable="true" placeholder="变量标识" />
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
import { findTreeNode } from '@/utils';
import { FlowEntryVariableController } from '@/api/flowController.js';

export default {
  name: 'formEditFlowEntryVariable',
  props: {
    entryId: {
      type: String,
      required: true
    },
    datasource: {
      type: Object
    },
    rowData: {
      type: Object
    }
  },
  data () {
    return {
      bindColumnPath: [],
      formData: {
        showName: undefined,
        variableName: undefined,
        variableType: this.SysFlowVariableType.TASK,
        bindDatasourceId: undefined,
        bindRelationId: undefined,
        bindColumnId: undefined,
        builtIn: false
      },
      rules: {
        showName: [
          {required: true, message: '变量名称不能为空！', trigger: 'blur'}
        ],
        variableName: [
          {required: true, message: '变量标识不能为空！', trigger: 'blur'},
          {type: 'string', pattern: /^[a-z][a-zA-Z]+$/, message: '变量标识必须是小驼峰命名', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      this.$refs.form.validate(valid => {
        if (valid) {
          let params = {
            flowEntryVariableDto: {
              ...this.formData,
              entryId: this.entryId
            }
          }

          let httpCall = this.rowData == null ? FlowEntryVariableController.add(this, params) : FlowEntryVariableController.update(this, params);
          httpCall.then(res => {
            this.$message.success('保存成功！');
            this.onCancel(true);
          }).catch(e => {});
        }
      });
    },
    onBindColumnChange (values) {
      let columnId = null;
      if (Array.isArray(values) && values.length > 0) {
        columnId = values[values.length - 1];
      }
      this.formData.bindDatasourceId = this.datasource.datasourceId;
      this.formData.bindRelationId = undefined;
      this.formData.bindColumnId = undefined;
      if (columnId != null) {
        if (values[0] !== this.formData.bindDatasourceId) this.formData.bindRelationId = values[0];
        let column = findTreeNode(this.entryDatasourceData, columnId, 'value');
        if (column != null && column.tag != null) {
          this.formData.showName = column.tag.columnComment;
          this.formData.variableName = column.tag.objectFieldName;
          this.formData.bindColumnId = column.value;
        }
      }
    }
  },
  computed: {
    entryDatasourceData () {
      if (this.datasource == null) {
        return [];
      } else {
        let temp = [
          {
            label: this.datasource.datasourceName,
            value: this.datasource.datasourceId,
            tableId: this.datasource.masterTableId,
            children: this.datasource.columnList.map(column => {
              return {
                label: column.objectFieldName,
                value: column.columnId,
                tag: column
              }
            }),
            tag: this.datasource
          }
        ];

        if (Array.isArray(this.datasource.relationList)) {
          this.datasource.relationList.forEach(relation => {
            if (relation.relationType === this.SysOnlineRelationType.ONE_TO_ONE) {
              temp.push({
                label: relation.relationName,
                value: relation.relationId,
                tableId: relation.slaveTableId,
                children: relation.columnList.map(column => {
                  return {
                    label: column.objectFieldName,
                    value: column.columnId,
                    tag: column
                  }
                }),
                tag: relation
              });
            }
          });
        }
        return temp;
      }
    }
  },
  mounted () {
    if (this.rowData != null) {
      this.formData = {
        ...this.formData,
        ...this.rowData
      }
      if (this.formData.bindColumnId != null && this.datasource != null) {
        this.bindColumnPath = [this.formData.bindRelationId || this.formData.bindDatasourceId, this.formData.bindColumnId];
      }
    }
  }
}
</script>

<style>
</style>
