<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="表单编码" prop="formCode">
            <el-input class="input-item" v-model="formData.formCode"
              :clearable="true" placeholder="表单编码" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表单名称" prop="formName">
            <el-input class="input-item" v-model="formData.formName"
              :clearable="true" placeholder="表单名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表单类别" prop="formKind">
            <el-select class="input-item" v-model="formData.formKind"
              placeholder="表单类别" :disabled="pageType === SysOnlinePageType.FLOW">
              <el-option v-for="item in SysOnlineFormKind.getList()" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表单类型" prop="formType">
            <el-select class="input-item" v-model="formData.formType"
              placeholder="表单类型" :disabled="isEdit">
              <el-option v-for="item in getValidFormType" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表单数据" prop="masterTableId">
            <el-select class="input-item" v-model="formData.masterTableId" :clearable="true"
              placeholder="表单数据" :disabled="isEdit">
              <el-option v-for="item in getValidTableList" :key="item.tableId" :value="item.tableId" :label="item.tableName">
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{item.tableName}}</span>
                  <el-tag size="mini" :type="getDatasourceTableTagType(item.relationType)" effect="dark"
                    style="margin-left: 30px;"
                  >
                    {{getDatasourceTableTagName(item.relationType)}}
                  </el-tag>
                </el-row>
              </el-option>
            </el-select>
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
import { OnlineFormController } from '@/api/onlineController.js';
import { defaultFormConfig, defaultWidgetAttributes } from '../data/onlineFormOptions.js';

export default {
  props: {
    pageId: {
      type: String,
      required: true
    },
    pageType: {
      type: Number,
      required: true
    },
    datasourceTableList: {
      type: Array,
      required: true
    },
    datasourceId: {
      type: String,
      required: true
    },
    form: {
      type: Object
    }
  },
  data () {
    return {
      formData: {
        formId: undefined,
        formCode: undefined,
        formName: undefined,
        formKind: this.SysOnlineFormKind.PAGE,
        formType: this.pageType === this.SysOnlinePageType.FLOW ? this.SysOnlineFormType.FLOW : this.SysOnlineFormType.QUERY,
        masterTableId: undefined
      },
      rules: {
        formCode: [
          {required: true, message: '表单编码不能为空', trigger: true},
          {type: 'string', pattern: /^[A-Za-z0-9]+$/, message: '表单编码只允许输入字母和数字', trigger: 'blur'}
        ],
        formName: [
          {required: true, message: '表单名称不能为空', trigger: true}
        ],
        masterTableId: [
          {required: true, message: '请选择表单数据', trigger: true}
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
      let formConfig = {
        ...defaultFormConfig,
        widgetList: [],
        paramList: []
      }
      // 如果是查询页面，则添加默认的主表
      if (this.formData.formType === this.SysOnlineFormType.QUERY || this.formData.formType === this.SysOnlineFormType.WORK_ORDER) {
        formConfig.tableWidget = {
          ...defaultWidgetAttributes.table,
          tableId: this.formData.masterTableId,
          tableColumnList: [],
          variableName: this.formData.formCode,
          showName: this.formData.formName,
          operationList: this.formData.formType === this.SysOnlineFormType.QUERY ? [...defaultWidgetAttributes.table.operationList] : [],
          tableInfo: { ...defaultWidgetAttributes.table.tableInfo }
        }
      }

      let params = {
        onlineFormDto: {
          ...this.formData,
          pageId: this.pageId,
          widgetJson: this.isEdit ? this.formData.widgetJson : JSON.stringify({
            formConfig: formConfig,
            widgetList: []
          }),
          paramsJson: this.isEdit ? this.formData.paramsJson : JSON.stringify([]),
          datasourceIdList: [this.datasourceId]
        }
      }

      let httpCall = this.isEdit ? OnlineFormController.update(this, params) : OnlineFormController.add(this, params);
      httpCall.then(res => {
        this.$message.success('保存成功');
        this.onCancel(true);
      }).catch(e => {});
    },
    getDatasourceTableTagType (relationType) {
      if (relationType == null) return 'success';
      switch (relationType) {
        case this.SysOnlineRelationType.ONE_TO_ONE: return 'primary';
        case this.SysOnlineRelationType.ONE_TO_MANY: return 'warning';
        default:
          return 'info';
      }
    },
    getDatasourceTableTagName (relationType) {
      if (relationType == null) return '数据主表';
      return this.SysOnlineRelationType.getValue(relationType) || '未知类型';
    }
  },
  computed: {
    isEdit () {
      return this.form != null;
    },
    getValidFormType () {
      return this.SysOnlineFormType.getList().filter(item => {
        if (item.id === this.SysOnlineFormType.FLOW) {
          return this.pageType === this.SysOnlinePageType.FLOW;
        } else if (item.id === this.SysOnlineFormType.QUERY) {
          return this.pageType !== this.SysOnlinePageType.FLOW;
        } else if (item.id === this.SysOnlineFormType.WORK_ORDER) {
          return this.pageType === this.SysOnlinePageType.FLOW;
        } else {
          return true;
        }
      });
    },
    getValidTableList () {
      return this.datasourceTableList.filter(item => {
        switch (this.formData.formType) {
          // 工单列表页面和工作流流程页面，只能选择主表
          case this.SysOnlineFormType.FLOW:
          case this.SysOnlineFormType.WORK_ORDER:
            return item.relationType == null;
          // 流程编辑页面只支持一对多从表，普通编辑页面只支持主表和一对多从表
          case this.SysOnlineFormType.FORM:
            return this.pageType === this.SysOnlinePageType.FLOW ? item.relationType === this.SysOnlineRelationType.ONE_TO_MANY : (item.relationType == null || item.relationType === this.SysOnlineRelationType.ONE_TO_MANY);
          // 查询页面可以选择主表或者一对多从表
          case this.SysOnlineFormType.QUERY:
            return item.relationType == null || item.relationType === this.SysOnlineRelationType.ONE_TO_MANY;
        }
      });
    }
  },
  mounted () {
    if (this.isEdit) {
      this.formData = {
        ...this.form
      }
    }
  }
}
</script>

<style>
</style>
